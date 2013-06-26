import requests

from django.shortcuts import render_to_response
from django.http import HttpResponseRedirect

def status(request):
    if not request.POST:
        return HttpResponseRedirect('/')

    audience = 'http://localhost:8000'
    assertion = request.POST['assertion']

    try:
        page = requests.post('https://verifier.login.persona.org/verify',
                             verify=True,
                             data={ "assertion": assertion,
                                    "audience": audience})
        data = page.json()
    except requests.exceptions.SSLError:
        data = { "status": "failed",
                 "reason": "Could not verify SSL certificate" }
    except requests.exceptions.ConnectionError:
        data = { "status": "failed",
                 "reason": "Could not connect to server" }


    return render_to_response('status.html', { 'data': data })
