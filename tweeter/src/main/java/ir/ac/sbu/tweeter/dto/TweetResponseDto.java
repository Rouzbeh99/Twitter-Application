package ir.ac.sbu.tweeter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
    private List<String> likedBy;
    private List<String> retweetedBy;
    private List<String> hashtags;
    private List<String> mentions;
}
