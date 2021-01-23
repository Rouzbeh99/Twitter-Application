package ir.ac.sbu.tweeter.manager.User;

import lombok.Getter;

public class UserExistsException extends RuntimeException {
    @Getter
    private final String username;

    public UserExistsException(String username) {
        super("User with username : " + username + " already exists");
        this.username = username;
    }

}
