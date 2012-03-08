/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.browserid.spring;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.providers.AbstractAuthenticationToken;

import pt.webdetails.browserid.BrowserIdResponse;

public class BrowserIdAuthenticationToken extends AbstractAuthenticationToken implements BrowserIdAuthentication {

  private static final long serialVersionUID = 1L;
  
  private BrowserIdResponse auth;
  private String assertion;
  private String audience;

  
  public String getAudience() {
    return audience;
  }

  public String getAssertion() {
    return assertion;
  }

  @Override
  public Object getCredentials() {
    return getAssertion();
  }

  @Override
  public Object getPrincipal() {
      return auth != null ? auth.getEmail() : null;
  }
  
  public BrowserIdResponse getVerificationResponse(){
    return auth;
  }
  
  public boolean isVerified(){
    return auth != null && auth.getStatus() == BrowserIdResponse.Status.OK;
  }
  
  /**
   * Create token bwith empty authorities.
   * @param response BrowserID verification response
   * @param assertion JWT encoded assertion as recieved from navigator.id.get
   */
  public BrowserIdAuthenticationToken(BrowserIdResponse response, String assertion){
    super(new GrantedAuthority[0]);
    this.auth = response;
    this.assertion = assertion;
    setAuthenticated(false);
  }
  
  /**
   * Create authenticated token (if response status is okay)
   * @param response 
   * @param assertion assertion encoded assertion as recieved from navigator.id.get
   * @param grantedAuthorities
   */
  public BrowserIdAuthenticationToken(BrowserIdResponse response, String assertion, GrantedAuthority[] grantedAuthorities){
    super(grantedAuthorities);
    this.auth = response;
    this.assertion = assertion;
    setAuthenticated(response != null && response.getStatus() == BrowserIdResponse.Status.OK);
    setDetails(response);
  }
  
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(super.toString()).append("; ");
    sb.append("BrowserID response:");
    sb.append(auth == null ? "" : auth.toString());
    return sb.toString();
}


}
