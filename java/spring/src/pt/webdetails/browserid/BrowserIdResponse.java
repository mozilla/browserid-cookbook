/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.browserid;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.lang.StringUtils;


/**  
 * A response from a BrowserID verification service.
 * {@link #getStatus} will always be available, other fields' availability depend on its value:<br/>
 * &nbsp;&nbsp; if {@link Status#OK}:&nbsp; {@link #getEmail}, {@link #getAudience}, {@link #getExpires}, {@link #getIssuer} <br/>
 * &nbsp;&nbsp; if {@link Status#FAILURE}:&nbsp;  {@link #getReason} <br/>
*/
public class BrowserIdResponse {
  
  private Status status;
  private String email;
  private String audience;
  private long expires;
  private String issuer;
  private String reason;
  
  private JSONObject jsonResponse;
  
  /**
   * 
   * @return status of the verification. Will be {@link Status#OK} if the assertion is valid.
   */
  public Status getStatus() {
    return status;
  }

  /**
   * 
   * @return email address (the identity) that was verified
   */
  public String getEmail() {
    return email;
  }

  /**
   * 
   * @return domain for which the assertion is valid
   */
  public String getAudience() {
    return audience;
  }


  /**
   * 
   * @return expiration date for the assertion
   */
  public Date getExpires() {
    return new Date(expires);
  }


  /**
   * 
   * @return domain of the certifying authority
   */
  public String getIssuer() {
    return issuer;
  }

  /**
   * 
   * @return reason for verification failure
   */
  public String getReason() {
    return reason;
  }

  private static final class ResponseFields {
    static final String STATUS="status";
    static final String EMAIL="email";
    static final String AUDIENCE="audience";
    static final String EXPIRES="expires";
    static final String ISSUER="issuer";
    static final String REASON="reason";
  }
  
  /**
   * 
   * @param response result of a call to a BrowserID verify service
   * @throws JSONException if the response cannot be parsed as JSON markup.
   */
  public BrowserIdResponse(String response) throws JSONException{
    jsonResponse = new JSONObject(response);
    status = Status.parse((String) jsonResponse.get(ResponseFields.STATUS));
    
    switch(status){
      case OK:
        email = jsonResponse.getString(ResponseFields.EMAIL);
        audience = jsonResponse.getString(ResponseFields.AUDIENCE);
        expires = jsonResponse.getLong(ResponseFields.EXPIRES);
        if(jsonResponse.has(ResponseFields.ISSUER)) issuer = jsonResponse.getString(ResponseFields.ISSUER); 
        break;
      case FAILURE:
        if(jsonResponse.has(ResponseFields.REASON)) reason = jsonResponse.getString(ResponseFields.REASON);
        break;
    }
  
  }
  
  /**
   * BrowserID response status
   */
  public enum Status {
    /**
     * Verification was successful. ("okay")
     */
    OK("okay"),
    /**
     * Verification failed. ("failure")
     */
    FAILURE("failure");
    
    private String value;
    
    Status(String value){
      this.value = value;
    }
    
    public String toString(){
      return value;
    }
    
    public static Status parse(String value){
      if(StringUtils.equals(value, OK.value)){
        return OK;
      }
      return FAILURE;
    }
  }
  
  public String toString(){
    return jsonResponse.toString();
  }
  
  
}
