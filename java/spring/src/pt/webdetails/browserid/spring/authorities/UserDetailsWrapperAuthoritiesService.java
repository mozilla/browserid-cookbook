/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.browserid.spring.authorities;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.util.Assert;


public class UserDetailsWrapperAuthoritiesService implements InitializingBean, GrantedAuthoritiesService {

  public UserDetailsWrapperAuthoritiesService(){}
  
  public UserDetailsWrapperAuthoritiesService(UserDetailsService userDetailsService) throws IllegalArgumentException{
    setUserDetailsService(userDetailsService);
    afterPropertiesSet();
  }
  
  private UserDetailsService userDetailsService;
  
  public UserDetailsService getUserDetailsService() {
    return userDetailsService;
  }

  public void setUserDetailsService(UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  @Override
  public GrantedAuthority[] getAuthoritiesForUser(String username) {
    
   UserDetails userDetails = userDetailsService.loadUserByUsername(username); 
   return userDetails.getAuthorities();
  }

  @Override
  public void afterPropertiesSet() throws IllegalArgumentException {
   Assert.notNull(userDetailsService, "userDetailsService must be set");
  }

}
