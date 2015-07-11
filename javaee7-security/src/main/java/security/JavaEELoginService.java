package security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public class JavaEELoginService implements LoginService {
    private Log log = LogFactory.getLog(JavaEELoginService.class);

    @Inject
    private HttpServletRequest request;

    public LoginStatus getStatus() {
        if (request.getRemoteUser() != null) {
            return new LoginStatus(true, request.getRemoteUser());
        } else {
            return new LoginStatus(false, null);
        }
    }

    @Override
    public LoginStatus login(String username, String password) {
        try {
            if (request.getRemoteUser() == null) {
                request.login(username, password);
                log.debug("Login succeeded!");
            }
            return new LoginStatus(true, request.getRemoteUser());
        } catch (ServletException e) {
            e.printStackTrace();
            return new LoginStatus(false, null);
        }
    }
}
