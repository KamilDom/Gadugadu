package pl.edu.wat.gadugadu.server;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @SequenceGenerator(name = "seq", sequenceName = "user_seq", allocationSize = 1)
    @GeneratedValue(generator = "seq")
    private Long id;
    private String defaultNick;
    private String avatarURI;


    public User(String defaultNick, String avatarURI) {
        this.defaultNick = defaultNick;
        this.avatarURI = avatarURI;
    }

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDefaultNick() {
        return defaultNick;
    }

    public void setDefaultNick(String defaultNick) {
        this.defaultNick = defaultNick;
    }

    public String getAvatarURI() {
        return avatarURI;
    }

    public void setAvatarURI(String avatarURI) {
        this.avatarURI = avatarURI;
    }
}
