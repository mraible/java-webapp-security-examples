package security;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthResource {

    @RequestMapping("/api/health")
    public Health health() {
        return new Health("Spring Boot is up and running!");
    }
}
