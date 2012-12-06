<?php
/**
 * Example script that shows a very basic functionality of Persona in PHP
 *
 * This script does not represent production quality code, but
 * should provide a general overview of the steps.
 */

if (!empty($_POST)) {
    // A user has attempted to log in
    $result = verify_assertion($_POST['assertion']);
    if ($result->status === 'okay') {
        // Login successful
        print_header();
        echo "<p>Logged in as: " . $result->email . "</p>";
        echo '<p><a href="javascript:navigator.id.logout()">Logout</a></p>';
        print_backLink();
        print_footer($result->email);
    } else {
        // Login-attempt not successful
        print_header();
        echo "<p>Error: " . $result->reason . "</p>";
        // Note that the explanation is technical and not user friendly
        print_backLink();
        print_footer();
    }
} elseif (!empty($_GET['logout'])) {
    // Logout request submitted
    print_header();
    echo "<p>You have logged out.</p>";
    print_backLink();
    print_footer();
} else {
    // The state of the page
    print_header();
    echo "<p><a href=\"javascript:navigator.id.request()\">Login</a></p>";
    print_footer();
}

function print_header() {
    // A very simple form is being used to mimick an Ajax request
    echo <<<EOF
<!DOCTYPE html><html><head><meta charset="utf-8"></head>
<body>
<form id="login-form" method="POST">
<input id="assertion-field" type="hidden" name="assertion" value="">
</form>
EOF;
}

function print_backLink() {
    echo "<p><a href=\"persona.php\">Back to login page</a></p>";
}

function print_footer($email = 'null') {
    if ($email !== 'null') {
        $email = "'$email'";
    }
    echo <<<EOF
<script src="https://login.persona.org/include.js"></script>
<script>
navigator.id.watch({
    loggedInUser: $email,
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
</body></html>
EOF;
}

/**
 * Verify that the user has got a real asserion
 *
 * @param string $assertion The assertion as received from the login dialog
 * @return object
 */
function verify_assertion($assertion) {
    $audience = (empty($_SERVER['HTTPS']) ? 'http://' : 'https://') . $_SERVER['SERVER_NAME'] . ':' . $_SERVER['SERVER_PORT'];
    $postdata = 'assertion=' . urlencode($assertion) . '&audience=' . urlencode($audience);

    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, "https://verifier.login.persona.org/verify");
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_POSTFIELDS, $postdata);
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, true);
    curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 2);
    $json = curl_exec($ch);
    curl_close($ch);

    return json_decode($json);
}
