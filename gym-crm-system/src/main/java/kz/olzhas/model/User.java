package kz.olzhas.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.springframework.lang.NonNull;

@Entity
@Table (name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column (name = "last_name", nullable = false)
    private String lastName;
    
    @NonNull
    @Column (name = "username", nullable = false, unique = true)
    private String username;

    @Column (name = "password", nullable = false)
    private String password;

    @Column (name = "active", nullable = false)
    private boolean active;

    public User() {
    }

    public  User(String firstName, String lastName, boolean active) {
        this(null, firstName, lastName, null, null, active);
    }

    public User(
            Long id,
            String firstName,
            String lastName,
            String username,
            String password,
            boolean active) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.active = active;
    }


    protected void rename(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isActive() {
        return active;
    }
}
