<?php
if (!empty($_POST)) {
    $result = verify_assertion($_POST['assertion']);
    if ($result->status === 'okay') {
        print_header();
        echo "<p>Logged in as: " . $result->email . "</p>";
        echo '<p><a href="javascript:navigator.id.logout()">Logout</a></p>';
        echo "<p><a href=\"browserid.php\">Back to login page</p>";
        print_footer($result->email);
    } else {
        print_header();
        echo "<p>Error: " . $result->reason . "</p>";
        echo "<p><a href=\"browserid.php\">Back to login page</p>";
        print_footer();
    }
} elseif (!empty($_GET['logout'])) {
    print_header();
    echo "<p>You have logged out.</p>";
    echo "<p><a href=\"browserid.php\">Back to login page</p>";
    print_footer();
} else {
    print_header();
    echo "<p><a href=\"javascript:navigator.id.request()\">Login</a>";
    print_footer();
}

function print_header() {
    echo <<<EOF
<!DOCTYPE html><html><head><meta charset="utf-8"></head>
<body>
<form id="login-form" method="POST">
<input id="assertion-field" type="hidden" name="assertion" value="">
</form>
EOF;
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

function verify_assertion($assertion, $cabundle = NULL) {
    $audience = (empty($_SERVER['HTTPS']) ? 'http://' : 'https://') . $_SERVER['SERVER_NAME'] . ':' . $_SERVER['SERVER_PORT'];
    $postdata = 'assertion=' . urlencode($assertion) . '&audience=' . urlencode($audience);

    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, "https://verifier.login.persona.org/verify");
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_POSTFIELDS, $postdata);
    if (substr(PHP_OS, 0, 3) == 'WIN') {
        if (!isset($cabundle)) {
            $cabundle = dirname(__FILE__).DIRECTORY_SEPARATOR.'cabundle.crt';
        }
        curl_setopt($ch, CURLOPT_CAINFO, $cabundle);
    }
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, true);
    curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 2);
    $json = curl_exec($ch);
    curl_close($ch);

    return json_decode($json);
}
