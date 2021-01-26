package ir.ac.sbu.tweeter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private String name;
    private String username;
    @Builder.Default
    private List<String> followersUsername = new ArrayList<>();
    @Builder.Default
    private List<String> followingsUsername = new ArrayList<>();
    @Builder.Default
    private List<String> likedTweets = new ArrayList<>();
    @Builder.Default
    private List<String> tweets = new ArrayList<>();
    @Builder.Default
    private List<String> reTweets = new ArrayList<>();

}
