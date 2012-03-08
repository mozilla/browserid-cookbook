/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.browserid.spring;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpHost;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.json.JSONException;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.ui.AbstractProcessingFilter;
import org.springframework.util.Assert;

import pt.webdetails.browserid.BrowserIdResponse;
import pt.webdetails.browserid.BrowserIdVerifier;

/**
 * Spring security filter for BrowserID authentication.
 */
public class BrowserIdProcessingFilter extends AbstractProcessingFilter {

  private static final String DEFAULT_FILTER_PROCESS_URL = "/j_spring_security_check";
  private static final String DEFAULT_ASSERTION_PARAMETER = "assertion";
//  private static final String DEFAULT_AUDIENCE_PARAMETER = "audience";
  
  private String verificationServiceUrl; 
  private String assertionParameterName = DEFAULT_ASSERTION_PARAMETER;
//  private String audienceParameterName = DEFAULT_AUDIENCE_PARAMETER;
  private int order;
  
  public String getAssertionParameterName() {
    return assertionParameterName;
  }

  /**
   * 
   * @param assertionParameterName 
   */
  public void setAssertionParameterName(String assertionParameterName) {
    this.assertionParameterName = assertionParameterName;
  }

//  public String getAudienceParameterName() {
//    return audienceParameterName;
//  }
//
//  public void setAudienceParameterName(String audienceParameterName) {
//    this.audienceParameterName = audienceParameterName;
//  }

  public String getVerificationServiceUrl() {
    return verificationServiceUrl;
  }

  public void setVerificationServiceUrl(String verificationServiceUrl) {
    this.verificationServiceUrl = verificationServiceUrl;
  }

  
  @Override
  public int getOrder() {
    return order;
  }

  public void setOrder(int order) {
    this.order = order;
  }

  /**
   * 
   */
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request) throws AuthenticationException {
    String browserIdAssertion = request.getParameter(getAssertionParameterName());
//    String assertionAudience = request.getParameter(getAudienceParameterName());
    
    if(browserIdAssertion != null) {
     
      BrowserIdVerifier verifier = new BrowserIdVerifier(getVerificationServiceUrl());
      BrowserIdResponse response = null;
      
      String audience  = request.getRequestURL().toString();
      try {
        URL url = new URL(audience);
        audience = url.getHost();
      } catch (MalformedURLException e) {
        throw new BrowserIdAuthenticationException("Malformed request URL", e);
      }
      
//      Assert.hasLength("Unable to determine hostname",audience);
//      if(!StringUtils.equals(audience, assertionAudience)){
//        logger.error("Server and client-side audience don't match");
//      }
      
      try {
        response = verifier.verify(browserIdAssertion, audience);
      } catch (HttpException e) {
        throw new BrowserIdAuthenticationException("Error calling verify service [" + verifier.getVerifyUrl() + "]", e);
      } catch (IOException e) {
        throw new BrowserIdAuthenticationException("Error calling verify service [" + verifier.getVerifyUrl() + "]", e);
      } catch (JSONException e){
        throw new BrowserIdAuthenticationException("Could not parse response from verify service [" + verifier.getVerifyUrl() + "]", e);
      }

      if(response != null){
        if(response.getStatus() == BrowserIdResponse.Status.OK){
          BrowserIdAuthenticationToken token = new BrowserIdAuthenticationToken(response, browserIdAssertion);
          //send to provider to get authorities
          return getAuthenticationManager().authenticate(token);
        }
        else {
          throw new BrowserIdAuthenticationException("BrowserID verification failed, reason: " + response.getReason());
        }
      }
      else throw new BrowserIdAuthenticationException("Verification yielded null response");
    }
    //may not be a BrowserID authentication
    return null;
  }

  @Override
  public String getDefaultFilterProcessesUrl() {
    return DEFAULT_FILTER_PROCESS_URL;
  }
  
  
  @Override
  public void afterPropertiesSet() throws Exception {
    super.afterPropertiesSet();
    //request parameters
    Assert.hasLength(getAssertionParameterName(), "assertionParameterName cannot be empty.");
//    Assert.hasLength(getAudienceParameterName(), "audienceParameterName cannot be empty.");
    
    //check URL
    Assert.hasLength(getVerificationServiceUrl());
    try{
      HttpHost host = new HttpHost(new URI(getVerificationServiceUrl(), false));
      Assert.isTrue(host.getProtocol().isSecure(), "verificationServiceUrl does not use a secure protocol");
    } catch (URIException e){
      throw new IllegalArgumentException("verificationServiceUrl is not a valid URI",e);
    }
  }
  
  @Override
  protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException {
    if (authResult instanceof BrowserIdAuthenticationToken) {
      logger.debug(((BrowserIdAuthenticationToken) authResult));
      
    }

  }
}
