/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.browserid;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONException;
import org.apache.commons.lang.StringUtils;

/**
 * Simple client for a BrowserID verify call.
 */
public class BrowserIdVerifier {

  private static String DEFAULT_VERIFY_URL = "https://browserid.org/verify";
  
  private String url = DEFAULT_VERIFY_URL;
  
  /**
   * 
   * @param verifyUrl The URL that performs the verification. Defaults to <code>https://browserid.org/verify</code>.
   */
  public BrowserIdVerifier(String verifyUrl){
    this.url = StringUtils.isEmpty(verifyUrl) ?  DEFAULT_VERIFY_URL : verifyUrl;
  }
  
  public BrowserIdVerifier(){}
  
  /**
   * 
   * @return The URL this verifier is using.
   */
  public String getVerifyUrl(){
    return url;
  }
  
  /**
   * Verify if the given assertion is valid
   * @param assertion The assertion as returned 
   * @param audience
   * @return
   * @throws HttpException if an HTTP protocol exception occurs or the service returns a code not in the 200 range.
   * @throws IOException if a transport error occurs.
   * @throws JSONException if the result cannot be parsed as JSON markup
   */
  public BrowserIdResponse verify(String assertion, String audience) throws HttpException, IOException, JSONException {
    
    if(StringUtils.isEmpty(assertion)) throw new IllegalArgumentException("assertion is mandatory");
    if(StringUtils.isEmpty(audience)) throw new IllegalArgumentException("audience is mandatory");
    
    HttpClient client = new HttpClient();
    
    //TODO: check certificate?
    
    PostMethod post = new PostMethod(url);

    post.addParameter("assertion", assertion);
    post.addParameter("audience", audience);
    
    try{
    
      int statusCode = client.executeMethod(post);
 
      
      if(isHttpResponseOK(statusCode)){
         return new BrowserIdResponse(post.getResponseBodyAsString());
      }
      else throw new HttpException(HttpStatus.getStatusText(statusCode));
    
    }
    finally {
      post.releaseConnection();
    }

  }
  
  private static boolean isHttpResponseOK(int statusCode){
    return statusCode >= 200 && statusCode < 300;
  }
  

  
}
