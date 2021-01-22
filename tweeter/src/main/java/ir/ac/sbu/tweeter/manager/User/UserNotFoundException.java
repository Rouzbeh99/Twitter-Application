package ir.ac.sbu.tweeter.manager.User;

import lombok.Getter;

public class UserNotFoundException extends RuntimeException {
    @Getter
    private final String username;

    public UserNotFoundException(String username) {
        super("User with username : " + username + " not found");
        this.username = username;
    }

}
