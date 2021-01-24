package ir.ac.sbu.tweeter.service;

import ir.ac.sbu.tweeter.dto.*;
import ir.ac.sbu.tweeter.entity.User;
import ir.ac.sbu.tweeter.manager.User.UserExistsException;
import ir.ac.sbu.tweeter.manager.User.UserManager;
import ir.ac.sbu.tweeter.manager.User.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static javax.ws.rs.core.Response.*;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Slf4j
@Path("user")
@Component
public class UserService {
    private final UserManager userManager;
    private static int CONFLICT_STATUS_CODE = 409;

    @Autowired
    public UserService(UserManager userManager) {
        this.userManager = userManager;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(UserSaveRequestDto dto) {
        Response response;
        try {
            userManager.save(dto);
            response = ok().build();
        } catch (UserExistsException e) {
            response = status(CONFLICT_STATUS_CODE).build();
        }
        return response;
    }

    @DELETE
    @Path("{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeUser(@PathParam("username") String username) {

        Response response;
        try {
            userManager.delete(username);
            response = noContent().build();
        } catch (UserNotFoundException e) {
            response = status(NOT_FOUND).build();
        }
        return response;
    }

    @GET
    @Path("{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadByUsername(@PathParam("username") String username) {
        Response response;
        try {
            User user = userManager.loadByUsername(username);
            UserResponseDto dto = createResponseDto(user);
            response = Response.ok(dto).build();
        } catch (UserNotFoundException e) {
            response = status(NOT_FOUND).build();
        }
        return response;
    }

    @PUT
    @Path("{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editUser(@PathParam("username") String username, UserUpdateRequestDto dto) {
        Response response;
        try {
            userManager.update(username, dto);
            response = ok().build();
        } catch (UserNotFoundException e) {
            response = status(NOT_FOUND).build();
        }
        return response;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@QueryParam("username") String username,
                           @QueryParam("name") String name) {
        UserSearchDto dto = UserSearchDto.builder()
                .name(name)
                .UsernameValue(username)
                .build();
        List<User> userList = userManager.search(dto);
        List<UserResponseDto> userResponseDtos = new ArrayList<>();
        for (User user : userList) {
            userResponseDtos.add(
                    UserResponseDto.builder()
                            .name(user.getName())
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .build());
        }
        UserPageDto resultDto = UserPageDto.builder()
                .users(userResponseDtos)
                .build();
        return Response.ok(resultDto).build();

    }

    @PUT
    @Path("follow")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response follow(UserFollow_UnFollowDto dto) {
        Response response;
        try {
            userManager.follow(dto.getFollowedUsername(), dto.getFollowingUsername());
            response = ok().build();
        } catch (UserNotFoundException e) {
            response = status(NOT_FOUND).build();
        }
        return response;
    }

    @PUT
    @Path("unFollow")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response unFollow(UserFollow_UnFollowDto dto) {
        Response response;
        try {
            userManager.unFollow(dto.getFollowedUsername(), dto.getFollowingUsername());
            response = ok().build();
        } catch (UserNotFoundException e) {
            response = status(NOT_FOUND).build();
        }
        return response;
    }

    @GET
    @Path("authenticate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(@QueryParam("username") String username,
                                 @QueryParam("name") String name,
                                 @QueryParam("password") String password) {
        Response response;
        try {
            User user = userManager.loadByUsername(username);
            if (user.getName().equals(name) && user.getPassword().equals(password)) {
                response = ok().build();
            } else {
                response = status(NOT_FOUND).build();
            }
        } catch (UserNotFoundException e) {
            response = status(NOT_FOUND).build();
        }
        return response;
    }


    private UserResponseDto createResponseDto(User user) {

        return UserResponseDto.builder()
                .name(user.getName())
                .username(user.getUsername())
                .password(user.getPassword())
                .followersUsername(user.getFollowers().stream().map(User::getUsername).collect(Collectors.toList()))
                .followingsUsername(user.getFollowings().stream().map(User::getUsername).collect(Collectors.toList()))
                .build();
    }


}
