<?php

require_once "persona.php";

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
    $body .= "<p><a href=\"testPersona.php\">Back to login page</a></p>";
} elseif (!empty($_GET['logout'])) {
    $body = "<p>You have logged out.</p>";
    $body .= "<p><a href=\"testPersona.php\">Back to login page</a></p>";
} else {
    $body = "<p><a class=\"persona-button\" href=\"javascript:navigator.id.request()\"><span>Login with Persona</span></a></p>";
}

?><!DOCTYPE html>
<html>
  <head><meta http-equiv="X-UA-Compatible" content="IE=Edge">
  <link rel="stylesheet" type="text/css" href="css/persona-buttons.css"
  </head>
  <body>
    <form id="login-form" method="POST" action="testPersona.php">
      <input id="assertion-field" type="hidden" name="assertion" value="">
    </form>
    <?php echo $body ?>
    <script src="https://login.persona.org/include.js"></script>
    <script>
    navigator.id.watch({
        loggedInUser: <?php echo $email ? "'$email'" : 'null' ?>,
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
