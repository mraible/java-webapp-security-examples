package security;

import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Component
@Path("/health")
public class HealthController {
    @GET
    @Produces("application/json")
    public Health health() {
        return new Health("Jersey: Up and Running!");
    }
}
