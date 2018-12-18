package pl.edu.wat.gadugadu.common;

public class Authentication {
    private Integer id;
    private String password;
    private AuthenticationStatus authenticationStatus;
    private String name;

    public Authentication(Integer id, String password) {
        this.id = id;
        this.password = password;
    }

    // login ok
    public Authentication(String name, AuthenticationStatus authenticationStatus) {
        this.authenticationStatus = authenticationStatus;
        this.name = name;
    }

    // login error
    public Authentication(AuthenticationStatus authenticationStatus) {
        this.authenticationStatus = authenticationStatus;
    }

    public Integer getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public AuthenticationStatus getAuthenticationStatus() {
        return authenticationStatus;
    }

    public String getName() {
        return name;
    }
}
