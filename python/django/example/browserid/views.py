import urllib
import urllib2
import json

from django.shortcuts import render_to_response
from django.http import HttpResponseRedirect

def status(request):
    if not request.POST:
        return HttpResponseRedirect('/')

    audience = 'http://localhost:8000'
    assertion = request.POST['assertion']
    page = urllib2.urlopen('https://browserid.org/verify',
                           urllib.urlencode({ "assertion": assertion,
                                              "audience": audience}))
    data = json.load(page)

    return render_to_response('status.html', { 'data': data })
