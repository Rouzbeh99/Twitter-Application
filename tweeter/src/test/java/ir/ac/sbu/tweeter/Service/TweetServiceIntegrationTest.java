package ir.ac.sbu.tweeter.Service;

import ir.ac.sbu.tweeter.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;

import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TweetServiceIntegrationTest {

    @LocalServerPort
    private int port;

    private static Client client;
    private static WebTarget webTarget;
    private static String BODY_1 = "hello world!!!!";
    private static String BODY_2 = "it is a test tweet";
    private static String MENTION_1 = "Rouzbeh";
    private static String MENTION_2 = "Mohammad";
    private static String USERNAME_1 = "NGC";
    private static String USERNAME_2 = "ABC";
    private static String HASHTAG_1 = "hashtag number 1";
    private static String HASHTAG_2 = "hashtag number 2";
    public static final String NAME_1 = "Rouzbeh";
    public static final String NAME_2 = "Mohammad";
    public static final String PASSWORD_1 = "1234";
    public static final String PASSWORD_2 = "5678";
    private static int CONFLICT_STATUS_CODE = 409;
    private static UserSaveRequestDto user1;
    private static UserSaveRequestDto user2;
    private static String UUID_1;
    private static String UUID_2;
    private static TweetSaveRequestDto saveRequestDto1;
    private static TweetSaveRequestDto saveRequestDto2;

    @BeforeAll
    public static void init() {
        client = ClientBuilder.newClient();
        saveRequestDto1 = TweetSaveRequestDto.builder()
                .body(BODY_1)
                .OwnerUsername(USERNAME_1)
                .hashtags(Collections.singletonList(HASHTAG_1))
                .mentions(Collections.singletonList(MENTION_1))
                .build();
        saveRequestDto2 = TweetSaveRequestDto.builder()
                .body(BODY_2)
                .OwnerUsername(USERNAME_2)
                .hashtags(Arrays.asList(HASHTAG_1, HASHTAG_2))
                .mentions(Arrays.asList(MENTION_1, MENTION_2))
                .build();

        user1 = UserSaveRequestDto.builder()
                .username(USERNAME_1)
                .name(NAME_1)
                .password(PASSWORD_1)
                .build();
        user2 = UserSaveRequestDto.builder()
                .username(USERNAME_2)
                .name(NAME_2)
                .password(PASSWORD_2)
                .build();
    }

    @BeforeEach
    public void makeTarget() {
        webTarget = client.target("http://localhost:" + port).path("/tweeter/tweet");

    }

    @AfterAll
    public static void finish() {
        client.close();
    }


    @Test
    public void testSearchByHashtag() {
        saveTweet(saveRequestDto1);
        saveTweet(saveRequestDto2);
        Response response = webTarget.queryParam("hashtag", HASHTAG_1).request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus(), is(equalTo(OK.getStatusCode())));
        TweetPageDto dto = response.readEntity(TweetPageDto.class);
        assertThat(dto.getTweets().size(), is(equalTo(2)));
        assertThat(dto.getTweets().get(0).getBody(), is(equalTo(BODY_1)));
        assertThat(dto.getTweets().get(0).getHashtags().get(0), is(equalTo(HASHTAG_1)));
        assertThat(dto.getTweets().get(1).getBody(), is(equalTo(BODY_2)));

    }

    @Test
    public void testLike() {
        UUID_1 = saveTweet(saveRequestDto1);
        UUID_2 = saveTweet(saveRequestDto2);
        Response response = webTarget.path("like")
                .queryParam("uuid", UUID_1)
                .queryParam("username", USERNAME_2)
                .request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus(), is(equalTo(NO_CONTENT.getStatusCode())));
        Response response1 = client.target("http://localhost:" + port)
                .path("/tweeter/user").path("authenticate")
                .queryParam("username", USERNAME_2)
                .queryParam("password", PASSWORD_2)
                .request(MediaType.APPLICATION_JSON).get();
        assertThat(response1.getStatus(), is(equalTo(OK.getStatusCode())));
        UserResponseDto userDto = response1.readEntity(UserResponseDto.class);
        assertThat(userDto.getLikedTweets().size(), is(equalTo(1)));
        assertThat(userDto.getLikedTweets().get(0), is(equalTo(UUID_1)));

    }

    @Test
    public void testRetweet() {
        UUID_1 = saveTweet(saveRequestDto1);
        UUID_2 = saveTweet(saveRequestDto2);
        Response response = webTarget.path("retweet")
                .queryParam("uuid", UUID_1)
                .queryParam("username", USERNAME_2)
                .request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus(), is(equalTo(NO_CONTENT.getStatusCode())));
        response = webTarget.path("retweet")
                .queryParam("uuid", UUID_2)
                .queryParam("username", USERNAME_2)
                .request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus(), is(equalTo(NO_CONTENT.getStatusCode())));

        Response response1 = client.target("http://localhost:" + port)
                .path("/tweeter/user").path("authenticate")
                .queryParam("username", USERNAME_2)
                .queryParam("password", PASSWORD_2)
                .request(MediaType.APPLICATION_JSON).get();
        assertThat(response1.getStatus(), is(equalTo(OK.getStatusCode())));
        UserResponseDto userDto = response1.readEntity(UserResponseDto.class);
        assertThat(userDto.getReTweets().size(), is(equalTo(2)));
        assertThat(userDto.getReTweets().get(0), is(equalTo(UUID_1)));
        assertThat(userDto.getReTweets().get(1), is(equalTo(UUID_2)));

    }

    @Test
    public void testTimeline(){

        UUID_1 = saveTweet(saveRequestDto1);
        UUID_2 = saveTweet(saveRequestDto2);
        UserFollow_UnFollowDto dto = UserFollow_UnFollowDto.builder()
                .followedUsername(USERNAME_1)
                .followerUsername(USERNAME_2)
                .build();

        //follow
        Response response1 = client.target("http://localhost:" + port).path("/tweeter/user").path("follow").request(MediaType.APPLICATION_JSON).put(Entity.json(dto));
        assertThat(response1.getStatus(), is(equalTo(OK.getStatusCode())));

        //get follower
        Response response2 = client.target("http://localhost:" + port).path("/tweeter/user").path("authenticate")
                .queryParam("username", USERNAME_2)
                .queryParam("password", PASSWORD_2)
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertThat(response2.getStatus(), is(equalTo(OK.getStatusCode())));
        UserResponseDto followerDto = response2.readEntity(UserResponseDto.class);
        assertThat(followerDto.getTimeline().size(),is(equalTo(2)));
        assertThat(Arrays.asList(UUID_1,UUID_2).contains(followerDto.getTimeline().get(0)),is(equalTo(true)));
        assertThat(Arrays.asList(UUID_1,UUID_2).contains(followerDto.getTimeline().get(1)),is(equalTo(true)));

        //unFollow
        response1 = client.target("http://localhost:" + port).path("/tweeter/user").path("unFollow").request(MediaType.APPLICATION_JSON).put(Entity.json(dto));
        assertThat(response1.getStatus(), is(equalTo(OK.getStatusCode())));


        //get unFollower
        response2 = client.target("http://localhost:" + port).path("/tweeter/user").path("authenticate")
                .queryParam("username", USERNAME_2)
                .queryParam("password", PASSWORD_2)
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertThat(response2.getStatus(), is(equalTo(OK.getStatusCode())));
        UserResponseDto unFollower = response2.readEntity(UserResponseDto.class);
        assertThat(unFollower.getFollowingsUsername().size(), is(equalTo(0)));
        assertThat(unFollower.getTimeline().size(),is(equalTo(1)));
        assertThat(unFollower.getTimeline().get(0),is(equalTo(UUID_2)));
    }

    public String saveTweet(TweetSaveRequestDto dto) {
        saveUser(user1);
        saveUser(user2);
        Response response = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(dto));
        assertThat(response.getStatus(),is(equalTo(OK.getStatusCode())));
        return response.readEntity(TweetResponseDto.class).getUuid();
    }

    private void saveUser(UserSaveRequestDto dto) {
         client.target("http://localhost:" + port).path("/tweeter/user")
                .request(MediaType.APPLICATION_JSON).post(Entity.json(dto));
    }


}
