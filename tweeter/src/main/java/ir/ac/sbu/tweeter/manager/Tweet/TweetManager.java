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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
        if(!user.getTimeline().contains(tweet)) {
            user.getTimeline().add(tweet);
            Collections.sort(user.getTimeline());
        }
        for (User user1 : user.getFollowers()) {
            if(!user1.getTimeline().contains(tweet)) {
                user1.getTimeline().add(tweet);
            }
            Collections.sort(user1.getTimeline());
        }
        return tweetDao.save(tweet);
    }

    @Transactional
    public Tweet loadByUUID(String uuid) {
        Tweet tweet = tweetDao.loadByUsername(uuid);
        tweet.getRetweetedBy().size();
        tweet.getMentions().size();
        tweet.getHashtags().size();
        tweet.getLikedBy().size();
        return tweet;
    }

    @Transactional
    public void delete(String uuid) {
        tweetDao.delete(uuid);
        log.info("tweet with uuid : {} deleted", uuid);
    }

    @Transactional
    public List<Tweet> search(TweetSearchParamsDto params) {
        List<Tweet> tweets = tweetDao.search(params);
        for (Tweet tweet : tweets) {
            log.info("hashtags :{} and mentions :{}",tweet.getHashtags(),tweet.getMentions());
        }
        log.debug("search method with parameters : {} executed", params);
        return tweets;
    }

    @Transactional
    public void retweet(String tweetUUID, String username) {
        User user = userManager.loadByUsername(username);
        Tweet tweet = loadByUUID(tweetUUID);
        user.getRetweets().add(tweet);
        tweet.getRetweetedBy().add(user);
        if(!user.getTimeline().contains(tweet)) {
            user.getTimeline().add(tweet);
            Collections.sort(user.getTimeline());
        }
        for (User user1 : user.getFollowers()) {
            if(!user1.getTimeline().contains(tweet)) {
                user1.getTimeline().add(tweet);
            }
            Collections.sort(user1.getTimeline());
        }
    }
    @Transactional
    public void unRetweet(String tweetUUID, String username) {
        User user = userManager.loadByUsername(username);
        Tweet tweet = loadByUUID(tweetUUID);
        user.getRetweets().remove(tweet);
        tweet.getRetweetedBy().remove(user);
        user.getTimeline().remove(tweet);
        for (User user1 : user.getFollowers()) {
            user1.getTimeline().remove(tweet);
        }
    }

    @Transactional
    public void like(String tweetUUID, String username) {
        User user = userManager.loadByUsername(username);
        Tweet tweet = loadByUUID(tweetUUID);
        user.getLikedTweets().add(tweet);
        tweet.getLikedBy().add(user);
    }

    @Transactional
    public void unLike(String tweetUUID, String username) {
        User user = userManager.loadByUsername(username);
        Tweet tweet = loadByUUID(tweetUUID);
        user.getLikedTweets().remove(tweet);
        tweet.getLikedBy().remove(user);
    }

    private Tweet createEntity(TweetSaveRequestDto saveDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Tweet tweet = Tweet.builder()
                .body(saveDto.getBody())
                .hashtags(saveDto.getHashtags())
                .mentions(saveDto.getMentions())
                .uuid(UUID.randomUUID().toString())
                .owner(userManager.loadByUsername(saveDto.getOwnerUsername()))
                .build();
        if(Objects.nonNull(saveDto.getTime())){
            tweet.setTime(LocalDateTime.parse(saveDto.getTime(),formatter));
        }
        return tweet;
    }

}
