package security;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.AnonymousFilter;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebSecurityConfig {

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilter() {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        Map<String, String> filterChainDefinitionMapping = new HashMap<>();
        filterChainDefinitionMapping.put("/api/health", "authc,roles[guest],ssl[8443]");
        filterChainDefinitionMapping.put("/login", "authc");
        filterChainDefinitionMapping.put("/logout", "logout");
        shiroFilter.setFilterChainDefinitionMap(filterChainDefinitionMapping);
        shiroFilter.setSecurityManager(securityManager());
        shiroFilter.setLoginUrl("/login");
        Map<String, Filter> filters = new HashMap<>();
        filters.put("anon", new AnonymousFilter());
        filters.put("authc", new FormAuthenticationFilter());
        LogoutFilter logoutFilter = new LogoutFilter();
        logoutFilter.setRedirectUrl("/login?logout");
        filters.put("logout", logoutFilter);
        filters.put("roles", new RolesAuthorizationFilter());
        filters.put("user", new UserFilter());
        shiroFilter.setFilters(filters);
        return shiroFilter;
    }

    @Bean(name = "securityManager")
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(jdbcRealm());
        return securityManager;
    }

    @Bean(name = "jdbcRealm")
    @DependsOn("lifecycleBeanPostProcessor")
    public JdbcRealm jdbcRealm() {
        JdbcRealm realm = new JdbcRealm();
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashAlgorithmName(Sha256Hash.ALGORITHM_NAME);
        realm.setCredentialsMatcher(credentialsMatcher);
        realm.setAuthenticationQuery("select user_pass from users where user_name = ?");
        realm.setUserRolesQuery("select role_name from user_roles where user_name = ?");
        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName("localhost");
        ds.setUser("root");
        ds.setDatabaseName("appfuse");
        realm.setDataSource(ds);
        realm.init();
        return realm;
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }
}