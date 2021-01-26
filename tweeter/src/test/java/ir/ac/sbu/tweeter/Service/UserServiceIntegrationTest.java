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
import java.util.UUID;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceIntegrationTest {

    @LocalServerPort
    private int port;


    private static Client client;
    private static WebTarget webTarget;
    public static final String NAME_1 = "Rouzbeh";
    public static final String NAME_2 = "Mohammad";
    public static final String USERNAME_1 = "NGC";
    public static final String USERNAME_2 = "ABC";
    public static final String PASSWORD_1 = "1234";
    public static final String PASSWORD_2 = "5678";
    private static int CONFLICT_STATUS_CODE = 409;
    private static UserSaveRequestDto user1;
    private static UserSaveRequestDto user2;


    @BeforeAll
    public static void init() {
        client = ClientBuilder.newClient();
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
        webTarget = client.target("http://localhost:" + port).path("/tweeter/user");

    }

    @AfterAll
    public static void finish() {
        client.close();
    }

    @Test
    public void testAddUserHappyPath() {
        Response response1 = saveUser(user1);
        Response response2 = saveUser(user2);
        assertThat(response1.getStatus(), is(equalTo(OK.getStatusCode())));
        assertThat(response2.getStatus(), is(equalTo(OK.getStatusCode())));
    }

    @Test
    public void testAddUserRepeatitiveUsername() {
        saveUser(user1);
        UserSaveRequestDto user = UserSaveRequestDto.builder()
                .username(USERNAME_1)
                .name(NAME_1)
                .password(PASSWORD_1)
                .build();
        Response response = saveUser(user);
        assertThat(response.getStatus(), is(equalTo(CONFLICT_STATUS_CODE)));
    }

//    @Test
//    public void loadByUsernameHappyPath() {
//        saveUser(user2);
//        Response response = webTarget.path(USERNAME_2).request(MediaType.APPLICATION_JSON).get();
//        assertThat(response.getStatus(), is(equalTo(OK.getStatusCode())));
//        UserResponseDto dto = response.readEntity(UserResponseDto.class);
//        assertThat(dto.getName(), is(equalTo(NAME_2)));
//    }

    @Test
    public void testRetrieveUsers() {
        saveUser(user1);
        saveUser(user2);
        UserListDto dto = UserListDto.builder()
                .usernames(Arrays.asList(USERNAME_1, USERNAME_2))
                .build();
        Response response = webTarget.path("users").request(MediaType.APPLICATION_JSON).post(Entity.json(dto));
        assertThat(response.getStatus(), is(equalTo(OK.getStatusCode())));
        UserPageDto userPageDto = response.readEntity(UserPageDto.class);
        assertThat(userPageDto.getUsers().size(), is(equalTo(2)));
        assertThat(userPageDto.getUsers().get(0).getUsername(), is(equalTo(USERNAME_1)));
        assertThat(userPageDto.getUsers().get(1).getUsername(), is(equalTo(USERNAME_2)));
    }

//    @Test
//    public void testLoadByCodeWithNonExistentCode() {
//        String randomCode = UUID.randomUUID().toString();
//        webTarget = webTarget.path(randomCode);
//        Response response = webTarget.request(MediaType.APPLICATION_JSON).get();
//        assertThat(response.getStatus(), is(equalTo(NOT_FOUND.getStatusCode())));
//    }

    @Test
    public void editUserHappyPath() {
        saveUser(user2);
        String name = "hello";
        String password = "world";
        UserUpdateRequestDto updateDto = UserUpdateRequestDto.builder()
                .newName(name)
                .newPassword(password)
                .build();
        Response response = webTarget.path(USERNAME_2).request(MediaType.APPLICATION_JSON).put(Entity.json(updateDto));
        assertThat(response.getStatus(), is(equalTo(OK.getStatusCode())));
        Response response2 = client.target("http://localhost:" + port).path("/tweeter/user").path("authenticate")
                .queryParam("username", USERNAME_2)
                .queryParam("password", password)
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertThat(response2.getStatus(), is(equalTo(OK.getStatusCode())));
        UserResponseDto dto = response2.readEntity(UserResponseDto.class);
        assertThat(dto.getName(), is(equalTo(name)));
    }

    @Test
    public void testSearchByName() {
        saveUser(user1);
        saveUser(user2);
        Response response = webTarget.queryParam("name", NAME_1).request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus(), is(equalTo(OK.getStatusCode())));
        UserPageDto dto = response.readEntity(UserPageDto.class);
        assertThat(dto.getUsers().size(), is(equalTo(1)));
        assertThat(dto.getUsers().get(0).getUsername(), is(equalTo(USERNAME_1)));
    }

    @Test
    public void testSearchByUsername() {
        saveUser(user1);
        saveUser(user2);
        Response response = webTarget.queryParam("username", USERNAME_1).request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus(), is(equalTo(OK.getStatusCode())));
        UserPageDto dto = response.readEntity(UserPageDto.class);
        assertThat(dto.getUsers().size(), is(equalTo(1)));
        assertThat(dto.getUsers().get(0).getName(), is(equalTo(NAME_1)));
    }

    @Test
    public void testSearchByNameAndUsername() {
        saveUser(user1);
        saveUser(user2);
        Response response = webTarget.queryParam("name", NAME_1).queryParam("username", USERNAME_1).request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus(), is(equalTo(OK.getStatusCode())));
        UserPageDto dto = response.readEntity(UserPageDto.class);
        assertThat(dto.getUsers().size(), is(equalTo(1)));
        assertThat(dto.getUsers().get(0).getUsername(), is(equalTo(USERNAME_1)));
        assertThat(dto.getUsers().get(0).getName(), is(equalTo(NAME_1)));
    }

    @Test
    public void testAuthenticateHappyPath() {
        saveUser(user1);
        saveUser(user2);
        Response response = webTarget.path("authenticate")
                .queryParam("name", NAME_1).queryParam("username", USERNAME_1).queryParam("password", PASSWORD_1).request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus(), is(equalTo(OK.getStatusCode())));

    }

    @Test
    public void testAuthenticateNonExistent() {
        saveUser(user1);
        saveUser(user2);
        Response response = webTarget.path("authenticate")
                .queryParam("name", NAME_2).queryParam("username", USERNAME_1).queryParam("password", PASSWORD_2).request(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus(), is(equalTo(NOT_FOUND.getStatusCode())));


    }

    @Test
    public void testFollowAndUnFollow() {
        saveUser(user1);
        saveUser(user2);
        UserFollow_UnFollowDto dto = UserFollow_UnFollowDto.builder()
                .followedUsername(USERNAME_1)
                .followingUsername(USERNAME_2)
                .build();
        //follow
        Response response1 = webTarget.path("follow").request(MediaType.APPLICATION_JSON).put(Entity.json(dto));
        assertThat(response1.getStatus(), is(equalTo(OK.getStatusCode())));

        //get followed
        Response response = client.target("http://localhost:" + port).path("/tweeter/user").path("authenticate")
                .queryParam("username", USERNAME_1)
                .queryParam("password", PASSWORD_1)
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertThat(response.getStatus(), is(equalTo(OK.getStatusCode())));
        UserResponseDto user1_dto = response.readEntity(UserResponseDto.class);
        assertThat(user1_dto.getFollowersUsername().size(), is(equalTo(1)));
        assertThat(user1_dto.getFollowersUsername().get(0), is(equalTo(USERNAME_2)));

        //get follower
        Response response2 = client.target("http://localhost:" + port).path("/tweeter/user").path("authenticate")
                .queryParam("username", USERNAME_2)
                .queryParam("password", PASSWORD_2)
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertThat(response2.getStatus(), is(equalTo(OK.getStatusCode())));
        UserResponseDto user2_dto = response2.readEntity(UserResponseDto.class);
        assertThat(user2_dto.getFollowingsUsername().size(), is(equalTo(1)));
        assertThat(user2_dto.getFollowingsUsername().get(0), is(equalTo(USERNAME_1)));

        //unFollow
        response1 = client.target("http://localhost:" + port).path("/tweeter/user").path("unFollow").request(MediaType.APPLICATION_JSON).put(Entity.json(dto));
        assertThat(response1.getStatus(), is(equalTo(OK.getStatusCode())));

        //get UnFollowed
        response = client.target("http://localhost:" + port).path("/tweeter/user").path("authenticate")
                .queryParam("username", USERNAME_1)
                .queryParam("password", PASSWORD_1)
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertThat(response.getStatus(), is(equalTo(OK.getStatusCode())));
        user1_dto = response.readEntity(UserResponseDto.class);
        assertThat(user1_dto.getFollowersUsername().size(), is(equalTo(0)));

        //get unFollower
        response2 = client.target("http://localhost:" + port).path("/tweeter/user").path("authenticate")
                .queryParam("username", USERNAME_2)
                .queryParam("password", PASSWORD_2)
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertThat(response2.getStatus(), is(equalTo(OK.getStatusCode())));
        user2_dto = response2.readEntity(UserResponseDto.class);
        assertThat(user2_dto.getFollowingsUsername().size(), is(equalTo(0)));
    }


    private Response saveUser(UserSaveRequestDto dto) {
        return webTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(dto));
    }


}
