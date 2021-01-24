package ir.ac.sbu.tweeter.manager.User;

import ir.ac.sbu.tweeter.dao.UserDao;
import ir.ac.sbu.tweeter.dto.UserSaveRequestDto;
import ir.ac.sbu.tweeter.dto.UserSearchDto;
import ir.ac.sbu.tweeter.dto.UserUpdateRequestDto;
import ir.ac.sbu.tweeter.entity.Tweet;
import ir.ac.sbu.tweeter.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class UserManager {

    private final UserDao userDao;

    @Autowired
    public UserManager(UserDao userDao) {
        this.userDao = userDao;
    }

    @Transactional
    public void save(UserSaveRequestDto saveDto) {
        try {
            User entity = createEntity(saveDto);
            userDao.save(entity);
        } catch (Exception e) {
            throw new UserExistsException(saveDto.getUsername());
        }
    }

    private User createEntity(UserSaveRequestDto saveDto) {
        return User.builder()
                .name(saveDto.getName())
                .username(saveDto.getUsername())
                .password(saveDto.getPassword())
                .build();
    }

    @Transactional
    public User loadByUsername(String username) {
        User user = userDao.loadByUsername(username);
        List<User> followers = user.getFollowers();
        List<User> followings = user.getFollowings();
        log.info("followers are :{}",followers);
        log.info("followings are :{}",followings);
        return user;
    }

    @Transactional
    public void update(String username, UserUpdateRequestDto dto) {
        User user = loadByUsername(username);
        User newUser = createUpdatedUser(user, dto);
        userDao.update(newUser);
    }

    @Transactional
    public void delete(String username) {
        userDao.delete(username);
        log.info("user with username : {} deleted", username);
    }

    @Transactional
    public List<User> search(UserSearchDto params) {
        List<User> users = userDao.search(params);
        log.debug("search method with parameters : {} executed", params);
        return users;
    }


    @Transactional
    public List<User> retrieveUsers(List<String> usernames) {
        List<User> result = new ArrayList<>();
        for (String username : usernames) {
            result.add(loadByUsername(username));
        }
        return result;
    }

    @Transactional
    public void follow(String followedUsername, String followerUsername) {
        User followed = loadByUsername(followedUsername);
        User follower = loadByUsername(followerUsername);
        followed.getFollowers().add(follower);
        follower.getFollowings().add(followed);
        log.info("followed is :{}", followed);
        log.info("follower is :{}", follower);
        userDao.update(followed);
        userDao.update(follower);
    }

    @Transactional
    public void unFollow(String followedUsername, String followerUsername) {
        User followed = loadByUsername(followedUsername);
        User follower = loadByUsername(followerUsername);
        followed.getFollowers().remove(follower);
        follower.getFollowings().remove(followed);
        userDao.update(followed);
        userDao.update(follower);
    }


    private User createUpdatedUser(User user, UserUpdateRequestDto dto) {
        String username = StringUtils.hasText(dto.getNewUsername()) ? dto.getNewUsername() : user.getUsername();
        String name = StringUtils.hasText(dto.getNewName()) ? dto.getNewName() : user.getName();
        String password = StringUtils.hasText(dto.getNewPassword()) ? dto.getNewPassword() : user.getPassword();
        return User.builder()
                .id(user.getId())
                .username(username)
                .name(name)
                .password(password)
                .build();
    }

//    @Transactional
//    public List<Tweet> retrieveOriginalTweets(String username){
//
//    }
//
//    @Transactional
//    public List<Tweet> retrieveReTweets(String username){
//
//    }
//
//    @Transactional
//    public List<Tweet> retrieveLikedTweets(String username){
//
//    }

//    @Transactional
//    public List<Tweet> updatePicture(String username, String Mid){
//
//    }


}
