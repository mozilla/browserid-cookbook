<?php
/**
 * Example script that shows a very basic functionality of Persona in PHP
 *
 * This script does not represent production quality code, but
 * should provide a general overview of the steps.
 */

$body = $email = NULL;
if (isset($_POST['assertion'])) { // @TODO security: what is assertion here?
    $persona = new Persona();
    
    // A user has attempted to log in
    $result = $persona->verifyAssertion($_POST['assertion']);
    
    if ($result->status === 'okay') {
        $body = "<p>Logged in as: " . $result->email . "</p>";
        $body .= '<p><a href="javascript:navigator.id.logout()">Logout</a></p>';
        $body .= html_backLink();
        $email = $result->email;
        
    } else {
        // Login-attempt not successful
        
        // Note that the explanation is technical and not user friendly
        $body = "<p>Error: " . $result->reason . "</p>";
        
        $body .= html_backLink();
    }
} elseif (!empty($_GET['logout'])) {
    $body = "<p>You have logged out.</p>";
    $body .= html_backLink();
} else {
    // The state of the page
    $body = "<p><a href=\"javascript:navigator.id.request()\">Login</a></p>";
}

function html_backLink() {
    return "<p><a href=\"persona.php\">Back to login page</a></p>";
}

// A very simple form is being used to mimick an Ajax request
?><!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
  </head>
  <body>
    <form id="login-form" method="POST">
      <input id="assertion-field" type="hidden" name="assertion" value="">
    </form>
    <?= $body ?>
    <script src="https://login.persona.org/include.js"></script>
    <script>
    navigator.id.watch({
        loggedInUser: <?= $email ? "'$email'" : 'null' ?>,
        onlogin: function (assertion) {
            var assertion_field = document.getElementById("assertion-field");
            assertion_field.value = assertion;
            var login_form = document.getElementById("login-form");
            login_form.submit();
        },
        onlogout: function () {
            window.location = '?logout=1';
        }
    });
    </script>
  </body>
</html>
<?php

class Persona
{
    /**
     * Audience full ServerName
     * 
     * @var string including http:// or https:// including : and port
     */
    protected $audience;

    /**
     * constructs a new Persona
     *
     * if $audience is not provided the audience will be guessed from $_SERVER
     * @param string $audience including http:// or https:// including : and port
     */
    public function __construct($audience = NULL, $serverPort = NULL)
    {
        $this->audience = $audience ?: $this->guessAudience();
    }
  
    /**
     * Verify that the user has got a real asserion
     *
     * returns the response from the persona verifier service
     * @param string $assertion The assertion as received from the login dialog
     * @return object
     */
    public function verifyAssertion($assertion)
    {
        $postdata = 'assertion=' . urlencode($assertion) . '&audience=' . urlencode($this->audience);
    
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, "https://verifier.login.persona.org/verify");
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $postdata);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, true);
        curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 2);
        $response = curl_exec($ch);
        curl_close($ch);
    
        return json_decode($response);
    }
    
    /**
     * Guesses an Audience from global $_SERVER vars
     *
     * @return string the full server name with port an http://
     */
    protected function guessAudience()
    {
        $audience = isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on' ? 'https://' : 'http://';
        $audience .= $_SERVER['SERVER_NAME'];
        $audience .= ':'.$_SERVER['SERVER_PORT'];
        
        return $audience;
    }
}
?>