<?php

$body = $email = NULL;
if (isset($_POST['assertion'])) {
    $persona = new Persona();
    $result = $persona->verifyAssertion($_POST['assertion']);

    if ($result->status === 'okay') {
        $body = "<p>Logged in as: " . $result->email . "</p>";
        $body .= '<p><a href="javascript:navigator.id.logout()">Logout</a></p>';
        $email = $result->email;
    } else {
        $body = "<p>Error: " . $result->reason . "</p>";
    }
    $body .= "<p><a href=\"persona.php\">Back to login page</a></p>";
} elseif (!empty($_GET['logout'])) {
    $body = "<p>You have logged out.</p>";
    $body .= "<p><a href=\"persona.php\">Back to login page</a></p>";
} else {
    $body = "<p><a class=\"persona-button\" href=\"javascript:navigator.id.request()\"><span>Login with Persona</span></a></p>";
}

?><!DOCTYPE html>
<html>
  <head><meta http-equiv="X-UA-Compatible" content="IE=Edge">
  <link rel="stylesheet" type="text/css" href="css/persona-buttons.css"
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
     * Scheme, hostname and port
     */
    protected $audience;

    /**
     * Constructs a new Persona (optionally specifying the audience)
     */
    public function __construct($audience = NULL)
    {
        $this->audience = $audience ?: $this->guessAudience();
    }

    /**
     * Verify the validity of the assertion received from the user
     *
     * @param string $assertion The assertion as received from the login dialog
     * @return object The response from the Persona online verifier
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
     * Guesses the audience from the web server configuration
     */
    protected function guessAudience()
    {
        $audience = isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on' ? 'https://' : 'http://';
        $audience .= $_SERVER['SERVER_NAME'] . ':'.$_SERVER['SERVER_PORT'];
        return $audience;
    }
}
