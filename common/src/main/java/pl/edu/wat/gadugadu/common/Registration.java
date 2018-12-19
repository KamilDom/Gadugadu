package pl.edu.wat.gadugadu.common;

public class Registration {
    private String name;
    private String password;
    private Integer newId;
    private RegistrationStatus registrationStatus;


    public Registration(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public Registration(Integer newId, RegistrationStatus registrationStatus) {
        this.newId = newId;
        this.registrationStatus = registrationStatus;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public Integer getNewId() {
        return newId;
    }

    public RegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }
}
