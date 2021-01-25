package ir.ac.sbu.tweeter.Manager;

import ir.ac.sbu.tweeter.dto.TweetSaveRequestDto;
import ir.ac.sbu.tweeter.dto.TweetSearchParamsDto;
import ir.ac.sbu.tweeter.dto.UserSaveRequestDto;
import ir.ac.sbu.tweeter.entity.Tweet;
import ir.ac.sbu.tweeter.entity.User;
import ir.ac.sbu.tweeter.manager.Tweet.TweetManager;
import ir.ac.sbu.tweeter.manager.User.UserManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@Slf4j
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class TweetManagerIntegrationTest {
    private static String BODY_1 = "hello world!!!!";
    private static String BODY_2 = "it is a test tweet";
    private static  String MENTION_1 = "Rouzbeh";
    private static  String MENTION_2 = "Mohammad";
    private static  String USERNAME_1 = "NGC";
    private static  String USERNAME_2 = "ABC";
    private static  String HASHTAG_1 = "hashtag number 1";
    private static  String HASHTAG_2 = "hashtag number 2";
    private static String UUID_1;
    private static String UUID_2;

    @Autowired
    private TweetManager tweetManager;

    @Autowired
    private UserManager userManager;

    @BeforeEach
    public void init() {
        TweetSaveRequestDto.TweetSaveRequestDtoBuilder builder = TweetSaveRequestDto.builder();
        UUID_1 = tweetManager.save(builder.body(BODY_1).hashtags(Collections.singletonList(HASHTAG_1)).mentions(Collections.singletonList(MENTION_1)).OwnerUsername(USERNAME_1).build()).getUuid();
        UUID_2 = tweetManager.save(builder.body(BODY_2).hashtags(Arrays.asList(HASHTAG_1,HASHTAG_2)).mentions(Arrays.asList(MENTION_1,MENTION_2)).OwnerUsername(USERNAME_2).build()).getUuid();
    }

    @Test
    @Transactional
    public void testSaveAndLoad(){
        Tweet tweet1 = tweetManager.loadByUUID(UUID_1);
        Tweet tweet2 = tweetManager.loadByUUID(UUID_2);
        assertThat(tweet1.getBody(),is(equalTo(BODY_1)));
        assertThat(tweet2.getBody(),is(equalTo(BODY_2)));
        assertThat(tweet1.getHashtags().size(),is(equalTo(1)));
        assertThat(tweet1.getHashtags().get(0),is(equalTo(HASHTAG_1)));
        assertThat(tweet1.getMentions().size(),is(equalTo(1)));
        assertThat(tweet1.getMentions().get(0),is(equalTo(MENTION_1)));
        assertThat(tweet2.getHashtags().size(),is(equalTo(2)));
        assertThat(tweet2.getMentions().size(),is(equalTo(2)));
    }

    @Test
    @Transactional
    public void testSearchByOwnerName(){
        TweetSearchParamsDto searchDto = TweetSearchParamsDto.builder()
                .ownerUsername(USERNAME_1)
                .build();
        List<Tweet> result = tweetManager.search(searchDto);
        assertThat(result.size(),is(equalTo(1)));
        assertThat(result.get(0).getBody(),is(equalTo(BODY_1)));
        assertThat(result.get(0).getMentions().size(),is(equalTo(1)));
        assertThat(result.get(0).getMentions().get(0),is(equalTo(MENTION_1)));
    }

    @Test
    @Transactional
    public void testSearchByHashtags(){
        TweetSearchParamsDto searchDto = TweetSearchParamsDto.builder()
                .hashtag(HASHTAG_1)
                .build();
        List<Tweet> result = tweetManager.search(searchDto);
        assertThat(result.size(),is(equalTo(2)));
        assertThat(result.get(0).getBody(),is(equalTo(BODY_1)));
        assertThat(result.get(1).getBody(),is(equalTo(BODY_2)));
    }

    @Test
    @Transactional
    public void testRetweet(){
        tweetManager.retweet(UUID_1,USERNAME_2);
        Tweet tweet = tweetManager.loadByUUID(UUID_1);
        assertThat(tweet.getRetweetedBy().size(),is(equalTo(1)));
        assertThat(tweet.getRetweetedBy().get(0).getUsername(),is(equalTo(USERNAME_2)));
        User user = userManager.loadByUsername(USERNAME_2);
        assertThat(user.getTweets().size(),is(equalTo(2)));
    }

    @Test
    @Transactional
    public void testLike(){
        tweetManager.like(UUID_1,USERNAME_2);
        Tweet tweet = tweetManager.loadByUUID(UUID_1);
        assertThat(tweet.getLikedBy().size(),is(equalTo(1)));
        assertThat(tweet.getLikedBy().get(0).getUsername(),is(equalTo(USERNAME_2)));
        User user = userManager.loadByUsername(USERNAME_2);
        assertThat(user.getLikedTweets().size(),is(equalTo(1)));
        assertThat(user.getLikedTweets().get(0).getBody(),is(equalTo(BODY_1)));
    }


}
