package ir.ac.sbu.tweeter.manager.Tweet;

import lombok.Getter;

public class TweetNotFoundException extends RuntimeException {
    @Getter
    private final String uuid;

    public TweetNotFoundException(String uuid) {
        super("Tweet with uuid : " + uuid + " not found");
        this.uuid = uuid;
    }
}
