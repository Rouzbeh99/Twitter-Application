package ir.ac.sbu.tweeter.manager.Tweet;

import ir.ac.sbu.tweeter.dao.TweetDao;
import ir.ac.sbu.tweeter.dto.TweetSaveRequestDto;
import ir.ac.sbu.tweeter.dto.TweetSearchParamsDto;
import ir.ac.sbu.tweeter.entity.Tweet;
import ir.ac.sbu.tweeter.entity.User;
import ir.ac.sbu.tweeter.manager.User.UserManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class TweetManager {

    private final TweetDao tweetDao;
    private final UserManager userManager;

    @Autowired
    public TweetManager(TweetDao tweetDao, UserManager userManager) {
        this.tweetDao = tweetDao;
        this.userManager = userManager;
    }

    @Transactional
    public Tweet save(TweetSaveRequestDto saveDto) {
        Tweet tweet = createEntity(saveDto);
        User user = userManager.loadByUsername(saveDto.getOwnerUsername());
        user.getTweets().add(tweet);
        return tweetDao.save(tweet);
    }

    @Transactional
    public Tweet loadByUUID(String uuid) {
        return tweetDao.loadByUsername(uuid);
    }

    @Transactional
    public void delete(String uuid) {
        tweetDao.delete(uuid);
        log.info("tweet with uuid : {} deleted", uuid);
    }

    @Transactional
    public List<Tweet> search(TweetSearchParamsDto params) {
        List<Tweet> tweets = tweetDao.search(params);
        log.debug("search method with parameters : {} executed", params);
        return tweets;
    }

    @Transactional
    public void retweet(String tweetUUID, String username) {
        User user = userManager.loadByUsername(username);
        Tweet tweet = loadByUUID(tweetUUID);
        user.getTweets().add(tweet);
        tweet.getRetweetedBy().add(user);
    }

    @Transactional
    public void like(String tweetUUID, String username) {
        User user = userManager.loadByUsername(username);
        Tweet tweet = loadByUUID(tweetUUID);
        user.getLikedTweets().add(tweet);
        tweet.getLikedBy().add(user);
    }

    private Tweet createEntity(TweetSaveRequestDto saveDto) {
        return Tweet.builder()
                .body(saveDto.getBody())
                .hashtags(saveDto.getHashtags())
                .mentions(saveDto.getMentions())
                .time(saveDto.getTime())
                .uuid(UUID.randomUUID().toString())
                .owner(userManager.loadByUsername(saveDto.getOwnerUsername()))
                .build();
    }

}
