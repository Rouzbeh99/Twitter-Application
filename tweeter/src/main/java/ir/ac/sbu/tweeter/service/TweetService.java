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
            TweetResponseDto responseDto = creatDto(savedTweet,dto.getOwnerUsername());
            response = ok(responseDto).build();
        } catch (UserNotFoundException e) {
            response = Response.status(NOT_FOUND).build();
        }
        return response;
    }

    @GET
    @Path("{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadByUsername(@PathParam("uuid") String uuid) {
        Response response;
        try {
            Tweet tweet = tweetManager.loadByUUID(uuid);
            TweetResponseDto dto = creatDto(tweet, tweet.getOwner().getUsername());
            response = Response.ok(dto).build();
        } catch (UserNotFoundException e) {
            response = status(NOT_FOUND).build();
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
            tweetResponseDtos.add(creatDto(tweet,ownerUsername));
        }
        TweetPageDto resultDto = TweetPageDto.builder()
                .tweets(tweetResponseDtos)
                .build();
        log.info("page is :{}",resultDto);
        return Response.ok(resultDto).build();

    }

    @GET
    @Path("like")
    public Response like(@QueryParam("uuid") String tweetUUID,
                         @QueryParam("username") String ownerUsername){
        log.info("method invoked");
        Response response;
        try {
            tweetManager.like(tweetUUID, ownerUsername);
            log.info("manager finished");
            response = noContent().build();
        } catch (TweetNotFoundException | UserNotFoundException e) {
            log.info("exception thrown");
            response = status(NOT_FOUND).build();
        }
        return response;
    }

    @GET
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

    private TweetResponseDto creatDto(Tweet savedTweet,String ownerUsername) {
        return TweetResponseDto.builder()
                .body(savedTweet.getBody())
                .ownerUsername(ownerUsername)
                .hashtags(savedTweet.getHashtags())
                .mentions(savedTweet.getMentions())
//                .time(savedTweet.getTime())
                .uuid(savedTweet.getUuid())
                .likedBy(savedTweet.getLikedBy().stream().map(User::getUsername).collect(Collectors.toList()))
                .retweetedBy(savedTweet.getRetweetedBy().stream().map(User::getUsername).collect(Collectors.toList()))
                .build();
    }

}
