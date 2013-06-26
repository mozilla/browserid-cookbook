#lang racket
(require web-server/servlet web-server/servlet-env web-server/templates web-server/http/cookie json)


;;response/full is needed to send a template back
(define (api-main req)
  (response/full
   200 #"Okay"
   (current-seconds) TEXT/HTML-MIME-TYPE
   empty
   (list (string->bytes/utf-8 (include-template "template.html")))))

;;performs the persona login
(define (persona-login assertion audience)
  (let* ([post-data (jsexpr->string (hash 'assertion assertion 'audience audience))] ;builds post json
         [url "https://verifier.login.persona.org/verify"]
         [data (port->string (post-pure-port
                              (string->url url)
                              (string->bytes/utf-8 post-data)
                              '("Content-Type:application/json" "Accept:application/json")))]) ;send request
    data))

;;handles persona login request
(define (api-login-persona req)
  (let* ([bindings (request-bindings req)] ;get the post bindings
         [assertion (and (exists-binding? 'assertion bindings) ;extract assertion from post
                         (extract-binding/single 'assertion bindings))])
    (if assertion 
        (let* ([data (persona-login assertion "http://localhost:8910/")] ;perform login
              [email (hash-ref (string->jsexpr data) 'email)] ;get the email out of the response
              [cookie (make-cookie "email" email)]) 
          (response/xexpr 
           #:cookies (list cookie) ;send the cookie
           data))  ; and the json data
        (response/xexpr 
         '(h1 "missing assertion"))))) ;if no data provided, return herror

(define (api-logout-persona req)
  (response/xexpr '(h1 "LOGOUT")))


;; url dispatcher
(define-values (blog-dispatch blog-url)
  (dispatch-rules
   [("") api-main]
   [("api" "login") #:method "post" api-login-persona]
   [("api" "logout") #:method "post" api-logout-persona]))

;; run the server
(define (start-server)
  (serve/servlet blog-dispatch
                 #:quit? #f
                 #:listen-ip #f
                 #:port 8910
                 #:servlet-regexp #rx""))

(start-server)
