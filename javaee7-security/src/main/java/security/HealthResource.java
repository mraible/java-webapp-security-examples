package security;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/health")
public class HealthResource {
    @GET
    @Produces("application/json")
    public Health health() {
        return new Health("Jersey is up and running!");
    }
}
