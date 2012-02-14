<?php
if (!empty($_POST)) {
    echo "<html><body>";
    $result = verify_assertion($_POST['assertion']);
    if ($result->status === 'okay') {
        echo "<p>Logged in as: " . $result->email . "</p>";
    } else {
        echo "<p>Error: " . $result->reason . "</p>";
    }
    echo "<p><a href=\"browserid.php\">Back to login page</p>";
    echo "</body></html>";
} else {
    print_login_form();
}

function print_login_form() {
    echo <<<EOF
<html>
<head>
<script src="https://browserid.org/include.js"></script>
<script>
function login() {
    navigator.id.get(function (assertion) {
        if (assertion) {
            var assertion_field = document.getElementById("assertion-field");
            assertion_field.value = assertion;
            var login_form = document.getElementById("login-form");
            login_form.submit();
        }
    });
}
</script>
</head>

<body>
<form id="login-form" method="POST">
<input id="assertion-field" type="hidden" name="assertion" value="missing">
</form>

<p><a href="javascript:login()">Login</a></p>
</body>
</html>
EOF;
}

function verify_assertion($assertion) {
    $audience = ($_SERVER['HTTPS'] ? 'https://' : 'http://') . $_SERVER['SERVER_NAME'] . ':' . $_SERVER['SERVER_PORT'];
    $postdata = 'assertion=' . urlencode($assertion) . '&audience=' . urlencode($audience);

    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, "https://browserid.org/verify");
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_POSTFIELDS, $postdata);
    $json = curl_exec($ch);
    curl_close($ch);

    return json_decode($json);
}
