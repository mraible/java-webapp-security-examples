package security;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {

    @RequestMapping(value="/api/csrf",produces="application/json")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }
}
