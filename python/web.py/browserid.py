#!/usr/bin/python2

import web
import requests

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

        try:
            page = requests.post('https://verifier.login.persona.org/verify',
                                 verify=True,
                                 data={ "assertion": i.assertion,
                                        "audience": audience})
            data = page.json()
        except requests.exceptions.SSLError:
            data = { "status": "failed",
                     "reason": "Could not verify SSL certificate" }
        except requests.exceptions.ConnectionError:
            data = { "status": "failed",
                     "reason": "Could not connect to server" }

        if data['status'] == "okay":
            message = "Logged in as: %s" % data['email']
        else:
            message = "Error: %s" % data['reason']

        return render.status(message)

if __name__ == '__main__':
    app.run()
