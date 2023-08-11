package example.micronaut;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class User {

    private String username;
    private Boolean loggedIn;

    public User() {
    }

    public User(String username, boolean loggedIn) {
        this.username = username;
        this.loggedIn = loggedIn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
