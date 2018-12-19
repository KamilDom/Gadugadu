package pl.edu.wat.gadugadu.server.database;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @SequenceGenerator(name = "seq", initialValue=1000, sequenceName = "user_seq", allocationSize = 1)
    @GeneratedValue(generator = "seq")
    private Integer id;
    private String name;
    private String password;
    private String avatarURI;


    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatarURI() {
        return avatarURI;
    }

    public void setAvatarURI(String avatarURI) {
        this.avatarURI = avatarURI;
    }
}
