package ir.ac.sbu.tweeter.service;

import ir.ac.sbu.tweeter.dto.*;
import ir.ac.sbu.tweeter.entity.Tweet;
import ir.ac.sbu.tweeter.entity.User;
import ir.ac.sbu.tweeter.manager.Tweet.TweetManager;
import ir.ac.sbu.tweeter.manager.Tweet.TweetNotFoundException;
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

@Slf4j
@Path("tweet")
@Component
public class TweetService {

    private final TweetManager tweetManager;
    private static final int NOT_FOUND = 404;

    @Autowired
    public TweetService(TweetManager tweetManager) {
        this.tweetManager = tweetManager;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addTweet(TweetSaveRequestDto dto) {
        Response response;
        try {
            Tweet savedTweet = tweetManager.save(dto);
            TweetResponseDto responseDto = creatDto(savedTweet);
            response = ok(responseDto).build();
        } catch (UserNotFoundException e) {
            response = Response.status(NOT_FOUND).build();
        }
        return response;
    }


    @DELETE
    @Path("{uuid}")
    public Response removeUser(@PathParam("uuid") String uuid) {

        Response response;
        try {
            tweetManager.delete(uuid);
            response = noContent().build();
        } catch (TweetNotFoundException e) {
            response = status(NOT_FOUND).build();
        }
        return response;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@QueryParam("username") String ownerUsername,
                           @QueryParam("hashtag") String hashtag) {
        TweetSearchParamsDto dto = TweetSearchParamsDto.builder()
                .ownerUsername(ownerUsername)
                .hashtag(hashtag)
                .build();
        List<Tweet> tweetList = tweetManager.search(dto);
        List<TweetResponseDto> tweetResponseDtos = new ArrayList<>();
        for (Tweet tweet : tweetList) {
            tweetResponseDtos.add(creatDto(tweet));
        }
        TweetPageDto resultDto = TweetPageDto.builder()
                .tweets(tweetResponseDtos)
                .build();
        return Response.ok(resultDto).build();

    }

    @PUT
    @Path("like")
    public Response like(@QueryParam("uuid") String tweetUUID,
                         @QueryParam("username") String ownerUsername){

        Response response;
        try {
            tweetManager.like(tweetUUID, ownerUsername);
            response = noContent().build();
        } catch (TweetNotFoundException | UserNotFoundException e) {
            response = status(NOT_FOUND).build();
        }
        return response;
    }

    @PUT
    @Path("retweet")
    public Response retweet(@QueryParam("uuid") String tweetUUID,
                         @QueryParam("username") String ownerUsername){

        Response response;
        try {
            tweetManager.retweet(tweetUUID, ownerUsername);
            response = noContent().build();
        } catch (TweetNotFoundException | UserNotFoundException e) {
            response = status(NOT_FOUND).build();
        }
        return response;
    }

    private TweetResponseDto creatDto(Tweet savedTweet) {
        return TweetResponseDto.builder()
                .body(savedTweet.getBody())
                .ownerUsername(savedTweet.getOwner().getUsername())
                .hashtags(savedTweet.getHashtags())
                .mentions(savedTweet.getMentions())
                .time(savedTweet.getTime())
                .uuid(savedTweet.getUuid())
                .likedBy(savedTweet.getLikedBy().stream().map(User::getUsername).collect(Collectors.toList()))
                .retweetedBy(savedTweet.getRetweetedBy().stream().map(User::getUsername).collect(Collectors.toList()))
                .build();
    }

}
