/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.browserid.spring.authorities;

import org.springframework.security.GrantedAuthority;

/**
 * Simple service providing authorities for an already authenticated user
 */
public interface GrantedAuthoritiesService {

  /**
   * 
   * @param email
   * @return 
   */
  GrantedAuthority[] getAuthoritiesForUser(String email);
  
}
