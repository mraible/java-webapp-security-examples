package security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    LoginService loginService;

    public LoginStatus getStatus() {
        return loginService.getStatus();
    }

    @RequestMapping(method = RequestMethod.POST)
    public LoginStatus login(@RequestParam("username") String username,
                             @RequestParam("password") String password) {

        return loginService.login(username, password);
    }
}
