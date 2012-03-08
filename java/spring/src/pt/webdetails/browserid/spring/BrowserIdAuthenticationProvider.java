/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.browserid.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.providers.AuthenticationProvider;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.util.Assert;

import pt.webdetails.browserid.BrowserIdResponse;
import pt.webdetails.browserid.spring.authorities.GrantedAuthoritiesService;
import pt.webdetails.browserid.spring.authorities.UserDetailsWrapperAuthoritiesService;

/**
 * {@link AuthenticationProvider} for {@link BrowserIdAuthenticationToken}
 */
public class BrowserIdAuthenticationProvider implements InitializingBean, AuthenticationProvider {
  
  private static String DEFAULT_AUTHENTICATION_SERVICE = "https://browserid.org/verify";
  private static Log log = LogFactory.getLog(BrowserIdAuthenticationProvider.class);
  
  private String verificationServiceUrl = DEFAULT_AUTHENTICATION_SERVICE; 
  
  private GrantedAuthoritiesService authoritiesService;
//  private UserDetailsService userDetailsService;

  public String getVerificationServiceUrl() {
    return verificationServiceUrl;
  }

  /**
   * 
   * @param verificationServiceUrl
   */
  public void setVerificationServiceUrl(String verificationServiceUrl) {
    this.verificationServiceUrl = verificationServiceUrl;
  }

//  public UserDetailsService getUserDetailsService() {
//    return userDetailsService;
//  }
//
  public void setUserDetailsService(UserDetailsService userDetailsService) {
    
    this.authoritiesService = userDetailsService != null ? new UserDetailsWrapperAuthoritiesService(userDetailsService) : null ;
  }
  
  public GrantedAuthoritiesService getAuthoritiesService() {
    return authoritiesService;
  }

  public void setAuthoritiesService(GrantedAuthoritiesService authoritiesService) {
    this.authoritiesService = authoritiesService;
  }

  public BrowserIdAuthenticationProvider(){
    
  }
  
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    Assert.isInstanceOf(BrowserIdAuthentication.class, authentication,
                        "Only " + BrowserIdAuthentication.class.getName() + " is supported.");

    BrowserIdAuthentication browserIdAuth = (BrowserIdAuthentication) authentication;
    
    BrowserIdResponse response = //browserIdAuth.getVerificationResponse() == null ? 
                                 //verifyAuthentication(browserIdAuth):
                                 browserIdAuth.getVerificationResponse();
        
    if(response != null && response.getStatus() == BrowserIdResponse.Status.OK ){
      String identity = response.getEmail();
      //get authorities
      GrantedAuthority[] grantedAuthorities = getAuthoritiesService().getAuthoritiesForUser(identity);
      if(grantedAuthorities == null || grantedAuthorities.length == 0){
        throw new BrowserIdAuthenticationException("No authorities granted to " + identity);
      }
      
      BrowserIdAuthenticationToken authenticatedToken = new BrowserIdAuthenticationToken(response, browserIdAuth.getAssertion(), grantedAuthorities);
      
       if(log.isDebugEnabled()) {
            log.debug("Upgraded token with authorities: " + authenticatedToken);
       }
       return authenticatedToken;
    }
    
    else {
      throw new BrowserIdAuthenticationException("User not verified: " + response);
    }
  }
  


//  /**
//   * @param browserIdAuth non-authenticated token
//   * @return
//   */
//  private BrowserIdResponse verifyAuthentication(BrowserIdAuthentication browserIdAuth) {
//    
//    BrowserIdVerifier verifier = new BrowserIdVerifier(getVerificationServiceUrl());
//    
//    BrowserIdResponse response = null;
//    try {
//      response = verifier.verify(browserIdAuth.getAssertion(), browserIdAuth.getAudience());
//    } catch (HttpException e) {
//      throw new BrowserIdAuthenticationException("Problem contacting verification service.", e);
//    } catch (IOException e) {
//      throw new BrowserIdAuthenticationException("Failed to parse response from verification service.", e);
//    } catch (JSONException e) {
//      throw new BrowserIdAuthenticationException("Failed to parse response from verification service.");
//    }
//    
//    if(response == null){
//      throw new BrowserIdAuthenticationException("Recieved no valid response from verification service.");
//    }
//    return response;
//  }


  @SuppressWarnings("rawtypes")
  @Override
  public boolean supports(Class authentication) {
    return BrowserIdAuthentication.class.isAssignableFrom(authentication);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
   
  }

}
