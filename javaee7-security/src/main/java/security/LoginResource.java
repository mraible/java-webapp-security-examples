package security;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public class LoginResource {

    @Inject
    LoginService loginService;

    @GET
    public LoginStatus getStatus() {
        return loginService.getStatus();
    }

    @POST
    public LoginStatus login(@FormParam("j_username") String username,
                             @FormParam("j_password") String password) {
        return loginService.login(username, password);
    }
}
