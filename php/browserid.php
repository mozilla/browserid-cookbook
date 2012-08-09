<?php
if (!empty($_POST)) {
    $result = verify_assertion($_POST['assertion']);
    if ($result->status === 'okay') {
        print_header($result->email);
        echo "<p>Logged in as: " . $result->email . "</p>";
        echo '<p><a href="javascript:navigator.id.logout()">Logout</a></p>';
    } else {
        print_header();
        echo "<p>Error: " . $result->reason . "</p>";
    }
    echo "<p><a href=\"browserid.php\">Back to login page</p>";
} elseif (!empty($_GET['logout'])) {
    print_header();
    echo "<p>You have logged out.</p>";
    echo "<p><a href=\"browserid.php\">Back to login page</p>";
} else {
    print_header();
    echo "<p><a href=\"javascript:navigator.id.request()\">Login</a>";
}
echo "</body></html>";

function print_header($email = 'null') {
    if ($email !== 'null') {
        $email = "'$email'";
    }
    echo <<<EOF
<!DOCTYPE html><html><head><meta charset="utf-8">
<script src="https://login.persona.org/include.js"></script>
</head>
<body>
<script>
navigator.id.watch({
    loggedInEmail: $email,
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
<form id="login-form" method="POST">
<input id="assertion-field" type="hidden" name="assertion" value="">
</form>
EOF;
}

function verify_assertion($assertion) {
    $audience = (empty($_SERVER['HTTPS']) ? 'http://' : 'https://') . $_SERVER['SERVER_NAME'] . ':' . $_SERVER['SERVER_PORT'];
    $postdata = 'assertion=' . urlencode($assertion) . '&audience=' . urlencode($audience);

    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, "https://verifier.login.persona.org/verify");
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_POSTFIELDS, $postdata);
    $json = curl_exec($ch);
    curl_close($ch);

    return json_decode($json);
}
