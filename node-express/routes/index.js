var https = require('https'),
    verify = require('browserid-verify')();

/*
 * GET home page.
 */

exports.index = function(req, resp){
  resp.render('index', { title: 'Express', user: req.session.email, csrf: req.session._csrf })
};

exports.auth = function (audience) {

  return function(req, resp){
    console.info('verifying with persona');

    var assertion = req.body.assertion;

    verify(assertion, audience, function(err, email, data) {
      if (err) {
        // return JSON with a 500 saying something went wrong
        console.warn('request to verifier failed : ' + err);
        return resp.send(500, { status : 'failure', reason : '' + err });
      }

      // got a result, check if it was okay or not
      if ( email ) {
        console.info('browserid auth successful, setting req.session.email');
        req.session.email = email;
        return resp.redirect('/');
      }

      // request worked, but verfication didn't, return JSON
      console.error(data.reason);
      resp.send(403, data)
    });
  };

};

exports.logout = function (req, resp) {
  req.session.destroy();
  resp.redirect('/');
};
