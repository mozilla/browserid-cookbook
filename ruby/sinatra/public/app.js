jQuery(function($) {
  function gotAssertion(assertion) {
    // got an assertion, now send it up to the server for verification  
    if (assertion !== null) {
      $.ajax({  
        type: 'POST',  
        url: '/auth/login',  
        data: { assertion: assertion },  
          success: function(res, status, xhr) {  
            window.location.reload();
          },  
          error: function(xhr, status, res) {
            alert("login failure" + res);
          }
        });
      }
    };

  $('#browserid').click(function() {
    navigator.id.get(gotAssertion);
  });
});
