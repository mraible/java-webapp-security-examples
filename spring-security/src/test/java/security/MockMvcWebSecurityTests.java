package security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Demonstrates some of the integrations with Spring Security's Test support.
 * This is not a complete showcase. For additional features and details on what
 * is shown refer to the <a href=
 * "http://docs.spring.io/spring-security/site/docs/4.0.x/reference/htmlsingle/#test"
 * >reference</a>
 *
 * @author Rob Winch
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebAppConfiguration
public class MockMvcWebSecurityTests {
    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
    }

    /**
     * Easily make and verify a request to the home page
     */
    @Test
    public void testHome() throws Exception {
        mockMvc
                .perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<title>Spring")));
    }

    /**
     * Demonstrate how to easily make a form based login request.
     * <ul>
     * <li>Default username is "user"</li>
     * <li>Default password is "password"</li>
     * <li>Automatically includes a valid CSRF token</li>
     * <li>We are able to verify the user we are authenticated with</li>
     * </ul>
     */
    @Test
    public void testLogin() throws Exception {
        mockMvc
                .perform(formLogin())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("user"));
    }

    /**
     * Demonstrate how to easily make a form based login request.
     *
     * <ul>
     * <li>Default username is "user"</li>
     * <li>Override the default password to "invalid"</li>
     * <li>Automatically includes a valid CSRF token</li>
     * <li>We are able to verify we are unauthenticated</li>
     * </ul>
     */
    @Test
    public void testDenied() throws Exception {
        String loginErrorUrl = "/login?error";
        mockMvc
                .perform(formLogin().password("invalid"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl( loginErrorUrl))
                .andExpect(unauthenticated());

        mockMvc
                .perform(get(loginErrorUrl))
                .andExpect(content().string(containsString("Invalid username and password")));
    }

    /**
     * Demonstrates requesting a protected page as an unauthenticated user
     */
    @Test
    public void testProtected() throws Exception {
        mockMvc
                .perform(get("/api/health").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Demonstrates requesting a protected page with valid http basic credentials
     */
    @Test
    public void testAuthorizedAccessHttpBasic() throws Exception {
        mockMvc
                .perform(get("/api/health").with(httpBasic("user", "password")))
                .andExpect(status().isOk());
    }

    /**
     * Demonstrates running a request as a user using {@link WithMockUser}.
     *
     * <ul>
     * <li>The default username is "user"</li>
     * <li>The default role is "ROLE_USER"</li>
     * <li>The user does NOT need to exist</li>
     * </ul>
     */
    @WithMockUser
    @Test
    public void testAuthorizedAccessWithMockUser() throws Exception {
        mockMvc
                .perform(get("/api/health"))
                .andExpect(status().isOk());
    }

    /**
     * Demonstrates requesting a protected page with invalid http basic credentials
     */
    @Test
    public void testUnauthorizedAccess() throws Exception {
        mockMvc
                .perform(get("/api/health").with(httpBasic("user", "invalid")))
                .andExpect(status().isUnauthorized());
    }

}
