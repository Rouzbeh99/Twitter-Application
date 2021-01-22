package ir.ac.sbu.tweeter.Manager;

import ir.ac.sbu.tweeter.dto.UserSaveRequestDto;
import ir.ac.sbu.tweeter.dto.UserSearchDto;
import ir.ac.sbu.tweeter.dto.UserUpdateRequestDto;
import ir.ac.sbu.tweeter.entity.User;
import ir.ac.sbu.tweeter.manager.User.UserManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@Slf4j
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class UserManagerIntegrationTest {
    public static final String NAME_1 = "Rouzbeh";
    public static final String NAME_2 = "Mohammad";
    public static final String NAME_3 = "Kian";
    public static final String USERNAME_1 = "NGC";
    public static final String USERNAME_2 = "ABC";
    public static final String USERNAME_3 = "L12";
    public static final String PASSWORD_1 = "1234";
    public static final String PASSWORD_2 = "5678";
    public static final String PASSWORD_3 = "9012";

    @BeforeEach
    public void init() {
        UserSaveRequestDto.UserSaveRequestDtoBuilder builder = UserSaveRequestDto.builder();
        userManager.save(builder.name(NAME_1).password(PASSWORD_1).username(USERNAME_1).build());
        userManager.save(builder.name(NAME_2).password(PASSWORD_2).username(USERNAME_2).build());
        userManager.save(builder.name(NAME_3).password(PASSWORD_3).username(USERNAME_3).build());
    }

    @Autowired
    private UserManager userManager;

    @Test
    @Transactional
    public void testSaveAndLoad(){
        User user1 = userManager.loadByUsername(USERNAME_1);
        User user2 = userManager.loadByUsername(USERNAME_2);
        User user3 = userManager.loadByUsername(USERNAME_3);
        assertThat(user1.getName(),is(equalTo(NAME_1)));
        assertThat(user2.getName(),is(equalTo(NAME_2)));
        assertThat(user3.getName(),is(equalTo(NAME_3)));
        assertThat(user1.getPassword(),is(equalTo(PASSWORD_1)));
        assertThat(user2.getPassword(),is(equalTo(PASSWORD_2)));
        assertThat(user3.getPassword(),is(equalTo(PASSWORD_3)));
    }

    @Test
    @Transactional
    public void testUpdate(){
        UserUpdateRequestDto dto = UserUpdateRequestDto.builder()
                .newName(NAME_2)
                .newPassword(PASSWORD_3)
                .build();
        userManager.update(USERNAME_1,dto);
        User user1 = userManager.loadByUsername(USERNAME_1);
        assertThat(user1.getName(),is(equalTo(NAME_2)));
        assertThat(user1.getPassword(),is(equalTo(PASSWORD_3)));
    }

    @Test
    @Transactional
    public void testSearch(){
        UserSearchDto params = UserSearchDto.builder()
                .name(NAME_3)
                .build();
        List<User> result = userManager.search(params);
        assertThat(result.size(),is(equalTo(1)));
        assertThat(result.get(0).getName(),is(equalTo(NAME_3)));
        assertThat(result.get(0).getUsername(),is(equalTo(USERNAME_3)));
        assertThat(result.get(0).getPassword(),is(equalTo(PASSWORD_3)));
    }

    @Test
    @Transactional
    public void testFollowAndUnFollow(){
        userManager.follow(USERNAME_1,USERNAME_2);
        userManager.follow(USERNAME_1,USERNAME_3);
        User user1 = userManager.loadByUsername(USERNAME_1);
        assertThat(user1.getFollowers().size(),is(equalTo(2)));
        assertThat(Arrays.asList(NAME_1,NAME_2).contains(user1.getFollowers().get(0).getName()),is(true));
        userManager.unFollow(USERNAME_1, USERNAME_2);
        user1 = userManager.loadByUsername(USERNAME_1);
        assertThat(user1.getFollowers().size(),is(equalTo(1)));
        assertThat(user1.getFollowers().get(0).getName(),is(equalTo(NAME_3)));
    }





}
