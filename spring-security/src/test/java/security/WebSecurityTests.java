package security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Basic integration tests for demo application.
 *
 * @author Dave Syer
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebAppConfiguration
@IntegrationTest({"server.port:0", "server.ssl.enabled:false"})
@DirtiesContext
public class WebSecurityTests {

    @Value("${local.server.port}")
    private int port;

    @Test
    public void testHome() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
        ResponseEntity<String> entity = new TestRestTemplate().exchange(
                "http://localhost:" + this.port, HttpMethod.GET, new HttpEntity<Void>(
                        headers), String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertTrue("Wrong body (title doesn't match):\n" + entity.getBody(), entity
                .getBody().contains("<title>Spring"));
    }

    @Test
    public void testLogin() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.set("username", "user");
        form.set("password", "password");
        form.set("remember-me", "true");
        getCsrf(form, headers);
        ResponseEntity<String> entity = new TestRestTemplate().exchange(
                "http://localhost:" + this.port + "/login", HttpMethod.POST,
                new HttpEntity<>(form, headers),
                String.class);
        assertEquals(HttpStatus.FOUND, entity.getStatusCode());
        List<String> cookies = entity.getHeaders().get("Set-Cookie");
        assertTrue(cookies.toString().contains("remember-me"));
        assertEquals("http://localhost:" + this.port + "/", entity.getHeaders()
                .getLocation().toString());
    }

    @Test
    public void testDenied() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.set("username", "admin");
        form.set("password", "admin");
        getCsrf(form, headers);
        ResponseEntity<String> entity = new TestRestTemplate().exchange(
                "http://localhost:" + this.port + "/login", HttpMethod.POST,
                new HttpEntity<>(form, headers),
                String.class);
        assertEquals(HttpStatus.FOUND, entity.getStatusCode());
        String cookie = entity.getHeaders().getFirst("Set-Cookie");
        headers.set("Cookie", cookie);
        ResponseEntity<String> page = new TestRestTemplate().exchange(entity.getHeaders()
                        .getLocation(), HttpMethod.GET, new HttpEntity<Void>(headers),
                String.class);
        assertEquals(HttpStatus.OK, page.getStatusCode());
        cookie = entity.getHeaders().getFirst("Set-Cookie");
        assertTrue(cookie.contains("remember-me"));
        assertTrue("Wrong body (message doesn't match):\n" + entity.getBody(), page
                .getBody().contains("Invalid username and password"));
    }

    @Test
    public void testProtected() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        ResponseEntity<String> entity = new TestRestTemplate().exchange(
                "http://localhost:" + this.port + "/api/health", HttpMethod.GET,
               new HttpEntity<>(headers),
               String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
    }

    @Test
    public void testAuthorizedAccess() throws Exception {
        ResponseEntity<String> entity = new TestRestTemplate("user", "password")
                .getForEntity("http://localhost:" + this.port + "/api/health", String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    public void testUnauthorizedAccess() throws Exception {
        ResponseEntity<String> entity = new TestRestTemplate("admin", "admin")
                .getForEntity("http://localhost:" + this.port + "/api/health", String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
    }

    private void getCsrf(MultiValueMap<String, String> form, HttpHeaders headers) {
        ResponseEntity<? extends CsrfToken> page = new TestRestTemplate().getForEntity(
                "http://localhost:" + this.port + "/api/csrf", CsrfToken.class);
        String cookie = page.getHeaders().getFirst("Set-Cookie");
        headers.set("Cookie", cookie);
        CsrfToken token = page.getBody();
        form.set(token.parameterName, token.token);
    }

    static class CsrfToken {
        private String parameterName;
        private String token;

        public String getParameterName() {
            return parameterName;
        }
        public void setParameterName(String parameterName) {
            this.parameterName = parameterName;
        }
        public String getToken() {
            return token;
        }
        public void setToken(String token) {
            this.token = token;
        }
    }

}
