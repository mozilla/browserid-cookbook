from django.conf.urls import patterns, url
from django.views.generic import TemplateView

urlpatterns = patterns('example.browserid.views',
    url(r'^$', TemplateView.as_view(template_name='index.html')),
    url(r'^status/$', 'status'),
)
