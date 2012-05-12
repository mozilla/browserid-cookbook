# Django settings for example project.

DEBUG = True

# List of callables that know how to import templates from various sources.
TEMPLATE_LOADERS = (
    'django.template.loaders.app_directories.Loader',
)

ROOT_URLCONF = 'example.urls'

INSTALLED_APPS = (
    'example.browserid'
)
