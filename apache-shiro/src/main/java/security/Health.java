package security;

public class Health {

    private String status;

    protected Health() {}

    public Health(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
