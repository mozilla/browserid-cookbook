Spring Security BrowserID Login
===============================

**NOTE**: This directory is a copy of [webdetails SpringSecurityBrowserID](https://github.com/webdetails/SpringSecurityBrowserID)

Does this recipie work for you? [Let us know good or bad](https://github.com/mozilla/browserid-cookbook/issues/1)

### Description

This project provides [BrowserID](http://browserid.org) login integration with the [Spring framework](http://www.springsource.org)

### Simple usage example in [Pentaho BI](http://community.pentaho.com):

1. add the jar from this project to your webapp (ex.:server/webapps/pentaho/WEB-INF/lib)

2. adapt applicationContext-spring-security-browserid.xml to your needs (as is should work with default hibernate configuration)

3. move it to the system dir and add it to pentaho-spring-beans.xml

4. in applicationContext-spring-security.xml make sure the BrowserIdProcessingFilter bean is included in FilterChainProxy and BrowserIdAuthenticationProvider is in the AuthenticationManager's provider list

5. change/create custom PUCLogin.jsp:

5.1 Import BrowserID client-side code:

      <script src="https://browserid.org/include.js" type="text/javascript"></script>

5.2 Add a login button:

      <input type="image" src="https://browserid.org/i/sign_in_green.png" alt="BrowserID login"  onclick="doBrowserIdLogin();">

5.3 Add client code to get an assertion and use it for authentication :

    <script type="text/javascript">

      var verifyBrowserIdLogin = function(assertion){
        if(assertion == null){
        return false;
        }
        
        jQuery.ajax({
            type: "POST",
            url: "browserid_security_check",
            data: "assertion=" + assertion,
            success:function(data,textStatus,jqXHR){
              bounceToReturnLocation();
            },
            error: function(xhr,  ajaxOptions, thrownError){
              if (xhr.status == 404) {
                // if we get a 404 it means login was successful but intended resource does not exist
                // just let it go - let the user get the 404
                bounceToReturnLocation();
                return;
              }
              // fail
              DisplayAlert('loginError', 40, 30);
            }
            
        });
      };

      var doBrowserIdLogin = function(){

        if (<%=loggedIn%>) {
          bounceToReturnLocation();
          return false;
        }
        
        //fetch the assertion
        navigator.id.get(function(assertion) {
          if (assertion) {
            //ok, check in server  
            verifyBrowserIdLogin(assertion);
          } else {
            //cancelled
            return false;
          }
        });
      };
    </script>
