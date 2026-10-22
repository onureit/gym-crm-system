package kz.olzhas.model;

import java.util.Objects;

public abstract class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean active;

    protected User(String firstName, String lastName, boolean active) {
        this(null, firstName, lastName, null, null, active);
    }

    protected User(
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

    public void assignId(long id) {
        if (this.id != null) {
            throw new IllegalStateException("ID is already assigned");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be positive");
        }
        this.id = id;
    }

    public void assignCredentials(String username, String password) {
        if (this.username != null || this.password != null) {
            throw new IllegalStateException("Credentials are already assigned");
        }
        this.username = Objects.requireNonNull(username, "username");
        this.password = Objects.requireNonNull(password, "password");
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
