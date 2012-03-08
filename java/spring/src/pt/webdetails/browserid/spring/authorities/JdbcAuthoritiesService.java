package pt.webdetails.browserid.spring.authorities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.util.Assert;


public class JdbcAuthoritiesService implements GrantedAuthoritiesService, InitializingBean {

  private String fetchAuthoritiesByEmailQuery;
  private MappingSqlQuery authoritiesMappingSqlQuery;
  private DataSource dataSource;
  //1-based index
  private int resultRoleColumnNumber = 1;
  
  
  public DataSource getDataSource() {
    return dataSource;
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }


  public int getResultRoleColumnNumber() {
    return resultRoleColumnNumber;
  }

  public void setResultRoleColumnNumber(int resultRoleColumnIndex) {
    this.resultRoleColumnNumber = resultRoleColumnIndex;
  }

  
  public String getFetchAuthoritiesByEmailQuery() {
    return fetchAuthoritiesByEmailQuery;
  }

  public void setFetchAuthoritiesByEmailQuery(String fetchAuthoritiesByUserNameQuery) {
    this.fetchAuthoritiesByEmailQuery = fetchAuthoritiesByUserNameQuery;
  }

  

  @Override
  public GrantedAuthority[] getAuthoritiesForUser(String userName) {
    
    @SuppressWarnings("unchecked")
    List<GrantedAuthority> authoritiesList =  authoritiesMappingSqlQuery.execute(userName);
    return authoritiesList.toArray(new GrantedAuthority[authoritiesList.size()]);
    
  }
  


  @Override
  public void afterPropertiesSet() throws Exception {
    
    //build query
    authoritiesMappingSqlQuery = new MappingSqlQuery(getDataSource(), getFetchAuthoritiesByEmailQuery()) {
      @Override
      protected GrantedAuthority mapRow(ResultSet rs, int rowNum) throws SQLException, IllegalArgumentException {
        return new GrantedAuthorityImpl(rs.getString(getResultRoleColumnNumber()));
      }
    };
    authoritiesMappingSqlQuery.declareParameter(new SqlParameter(Types.VARCHAR));
    authoritiesMappingSqlQuery.compile();
        
    Assert.hasLength(getFetchAuthoritiesByEmailQuery(), "fetchAuthoritiesByUserNameSql must be set");
    Assert.notNull(getDataSource(), "dataSource must be set");
    Assert.isTrue(getResultRoleColumnNumber() >= 0 &&  (authoritiesMappingSqlQuery.getRowsExpected() == 0 || 
                                                       getResultRoleColumnNumber() < authoritiesMappingSqlQuery.getRowsExpected()), 
                  "resultRoleColumnIndex out of range");
  }

}
