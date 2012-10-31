window.onload = function () {
  navigator.id.watch({
    loggedInUser: loggedInUser,
    onlogin: function (assertion) {
      var assertion_field = document.getElementById("assertion-field");
      assertion_field.value = assertion;
      var login_form = document.getElementById("login-form");
      login_form.submit();
    },
    onlogout: function () {
      window.location = '/logout';
    }
  });
};
