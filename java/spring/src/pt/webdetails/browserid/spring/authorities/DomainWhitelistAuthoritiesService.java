/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.browserid.spring.authorities;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


/**
 * Will grant authorities 
 */
public class DomainWhitelistAuthoritiesService implements InitializingBean, GrantedAuthoritiesService {

  private Map<String,GrantedAuthority[]> domainMap;
  
//  public Map<String, GrantedAuthority[]> getDomainMap() {
//    return domainMap;
//  }

//  public void setDomainMap(Map<String, GrantedAuthority[]> domainMap) {
//    this.domainMap = domainMap;
//  }
  

  
  /**
   * 
   * @param properties entries in the form &lt;domain&gt;=&lt;roleName&gt;[,&ltroleName&gt;]*&nbsp;
   */
  public void setDomainMap(Properties properties){
    Map<String, GrantedAuthority[]> domainMap = new HashMap<String, GrantedAuthority[]>(properties.size());
    for(Entry<Object,Object> entry : properties.entrySet()){
      
      String domain = (String) entry.getKey();
      String authoritiesAsText = (String) entry.getValue();
      
      //parse authorities, may return empty
      String[] roles = StringUtils.commaDelimitedListToStringArray(authoritiesAsText);
      GrantedAuthority[] authorities = new GrantedAuthority[roles.length];
      int i =0 ;
      for(String role : roles){
        authorities[i++] = new GrantedAuthorityImpl(role);
      }
      
      domainMap.put(domain, authorities);
      
    }
    this.domainMap = domainMap;
  }
  
  @Override
  public GrantedAuthority[] getAuthoritiesForUser(String email) throws IllegalArgumentException{
    String[] emailParts = StringUtils.split(email, "@");
    if(emailParts == null) throw new IllegalArgumentException("unparsable email");
    String domain = emailParts[1];
    return domainMap.get(domain);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    Assert.notNull(domainMap, "domainMap not set!"); 
  }

  
}
