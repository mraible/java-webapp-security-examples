package security;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/logout")
@Produces(MediaType.TEXT_PLAIN)
public class LogoutResource {

    @Inject
    HttpServletRequest request;

    @GET
    public String getStatus() throws ServletException {
        request.logout();
        request.getSession().invalidate();
        return "OK";
    }
}
