/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.browserid.spring;

import org.springframework.security.Authentication;

import pt.webdetails.browserid.BrowserIdResponse;

public interface BrowserIdAuthentication extends Authentication {
  
  /**
   * 
   * @return Domain and optionally port for which the assertion is intended
   */
  String getAudience();
  /**
   * 
   * @return Encoded JWT identity assertion.
   */
  String getAssertion();
  
  /**
   * 
   * @return Verification response (if made), <code>null</code> otherwise
   */
  BrowserIdResponse getVerificationResponse();

}
