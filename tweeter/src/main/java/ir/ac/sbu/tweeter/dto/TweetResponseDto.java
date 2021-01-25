package ir.ac.sbu.tweeter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TweetResponseDto {

    private String body;
    private LocalDateTime time;
    private String uuid;
    private String ownerUsername;
    @Builder.Default
    private List<String> likedBy = new ArrayList<>();
    @Builder.Default
    private List<String> retweetedBy = new ArrayList<>();
    @Builder.Default
    private List<String> hashtags = new ArrayList<>();
    @Builder.Default
    private List<String> mentions = new ArrayList<>();
}
