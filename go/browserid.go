package main

import (
    "encoding/json"
    "html/template"
    "io"
    "io/ioutil"
    "log"
    "net/http"
    "net/url"
    "fmt"
)

const loginform = `
<html>
    <head>
        <script src="https://login.persona.org/include.js"></script>
        <script>
        function login() {
            navigator.id.get(function(assertion) {
                if (assertion) {
                    var assertion_field = document.getElementById("assertion-field");
                    assertion_field.value = assertion;
                    var login_form = document.getElementById("login-form");
                    login_form.submit();
                }
            });
        }
        </script>
    </head>
<body>
    <form id="login-form" method="POST">
        <input id="assertion-field" type="hidden" name="assertion" value="">
    </form>

    <p><a href="javascript:login()">Login</a></p>
</body>
</html>
`

const statuspage = `
<html>
    <body>
        <p>
            {{if .Okay}}
                Logged in as: {{ .Email }}
            {{else}}
                Error: {{ .Reason }}
            {{end}}
        </p>

        <p><a href="/">Return to login page</a></p>
    </body>
</html>
`

type BrowserIDResponse struct {
    Status, Email, Reason string
}

func (b BrowserIDResponse) Okay() bool {
    return b.Status == "okay"
}

func browserIDResponseFromJson(r io.Reader) (resp BrowserIDResponse) {
    body, err := ioutil.ReadAll(r)

    if err != nil {
        log.Fatal(err)
    }

    err = json.Unmarshal(body, &resp)

    if err != nil {
        log.Fatal(err)
    }

    return resp
}

func verifyAssertion(assertion string) BrowserIDResponse {
    audience := "http://localhost:8080"
    resp, _ := http.PostForm(
        "https://verifier.login.persona.org/verify",
        url.Values{
            "assertion": {assertion},
            "audience": {audience},
    })
    response := browserIDResponseFromJson(resp.Body)
    resp.Body.Close()

    return response
}

func rootPageHandler(w http.ResponseWriter, r *http.Request) {
    if r.Method == "POST" {
        result := verifyAssertion(r.FormValue("assertion"))
        t := template.Must(template.New("statuspage").Parse(statuspage))
        err := t.Execute(w, result)

        if err != nil {
            log.Println("executing template: ", err)
        }
    } else {
        w.Write([]byte(loginform))
    }
}

func main() {
    fmt.Println("Now open http://localhost:8080")
    http.HandleFunc("/", rootPageHandler)
    http.ListenAndServe(":8080", nil)
}
