# Django settings for example project.

DEBUG = True

STATIC_ROOT = ''

STATIC_URL = '/static/'

STATICFILES_FINDERS = (
    'django.contrib.staticfiles.finders.FileSystemFinder',
    'django.contrib.staticfiles.finders.AppDirectoriesFinder',
#    'django.contrib.staticfiles.finders.DefaultStorageFinder',
)


# List of callables that know how to import templates from various sources.
TEMPLATE_LOADERS = (
    'django.template.loaders.app_directories.Loader',
)

ROOT_URLCONF = 'example.urls'

INSTALLED_APPS = (
    'example.browserid',
    'django.contrib.staticfiles',
)
