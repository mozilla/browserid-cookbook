#/usr/bin/env python
import urllib

import tornado.escape
import tornado.ioloop
import tornado.httpclient
import tornado.gen
import tornado.web


class MainHandler(tornado.web.RequestHandler):

    def get(self):
        self.render('index.html')


class BrowseridHandler(tornado.web.RequestHandler):

    @tornado.web.asynchronous
    @tornado.gen.engine
    def post(self):
        assertion = self.get_argument('assertion')
        http_client = tornado.httpclient.AsyncHTTPClient()
        url = 'https://browserid.org/verify'
        data = {
            'assertion': assertion,
            'audience': 'localhost:8080',
        }
        response = yield tornado.gen.Task(
            http_client.fetch,
            url,
            method='POST',
            body=urllib.urlencode(data),
        )
        data = tornado.escape.json_decode(response.body)

        if data['status'] == "okay":
            message = "Logged in as: %s" % data['email']
            #self.set_secure_cookie('user', data['email'], expires_days=10)
        else:
            message = "Error: %s" % data['reason']

        self.render('status.html', message=message)


if __name__ == '__main__':
    application = tornado.web.Application([
        (r'/', MainHandler),
        (r'/status', BrowseridHandler),
    ], template_path='templates')

    application.listen(8080)
    print "http://localhost:8080"
    tornado.ioloop.IOLoop.instance().start()
