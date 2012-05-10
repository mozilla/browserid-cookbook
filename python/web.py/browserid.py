#!/usr/bin/python2

import web
import urllib
import urllib2
import json

urls = (
    '/', 'index',
    '/status', 'status'
)

app = web.application(urls, globals())
render = web.template.render('templates/')

class index:
    def GET(self):
        return render.index()

class status:
    def POST(self):
        audience = "http://localhost:8080"
        i = web.input()
        page = urllib2.urlopen('https://browserid.org/verify',
                               urllib.urlencode({ "assertion": i.assertion,
                                                  "audience": audience }))
        data = json.load(page)

        if data['status'] == "okay":
            message = "Logged in as: %s" % data['email']
        else:
            message = "Error: %s" % data['reason']

        return render.status(message)

if __name__ == '__main__':
    app.run()
