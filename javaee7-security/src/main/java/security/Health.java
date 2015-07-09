package security;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "security.health")
public class Health {

    private String status;

    protected Health() {
    }

    public Health(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
