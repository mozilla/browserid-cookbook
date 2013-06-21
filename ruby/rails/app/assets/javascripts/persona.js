var currentUser = $USER;

navigator.id.watch({
  loggedInUser: currentUser,
  onlogin: function(assertion) {
    $.ajax({
      type: 'POST',
      url: '/login',
      data: {assertion: assertion},
      success: function(res, status, xhr) {
        window.location.reload();
        console.log('persona authentication succesful');
      },
      error: function(xhr, status, err) {
        navigator.id.logout();
        console.log("Login failure: " + err);
      }
    });
  },
  onlogout: function() {
    $.ajax({
      type: 'POST',
      url: '/logout',
      success: function(res, status, xhr) {
        window.location.reload();
        console.log('logout from persona succesful');
      },
      error: function(xhr, status, err) { console.log("Logout failure: " + err); }
    });
  }
});

$('#signin').click(function(){
  navigator.id.request();
});

$('#signout').click(function(){
  navigator.id.logout();
});
