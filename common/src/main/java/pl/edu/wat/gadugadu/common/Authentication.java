package pl.edu.wat.gadugadu.common;

public class Authentication {
    private String login;
    private String password;

    public Authentication(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
