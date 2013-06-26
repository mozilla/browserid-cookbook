If you are running on Windows, you may run into problems verifying the SSL certificate.

If so, you'll need to download a certificate bundle:

  http://curl.haxx.se/docs/caextract.html

and then add something like this:

  curl_setopt($ch, CURLOPT_CAINFO, 'cacert.pem');
