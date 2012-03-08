/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.browserid.spring;

import org.springframework.security.AuthenticationException;

public class BrowserIdAuthenticationException extends AuthenticationException {

  public BrowserIdAuthenticationException(String msg) {
    super(msg);
  }
  
  public BrowserIdAuthenticationException(String msg, Throwable t){
    super(msg, t);
  }

  private static final long serialVersionUID = 1L;

}
