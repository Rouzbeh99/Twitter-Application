package ir.ac.sbu.tweeter.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class TweetSaveRequestDto {

    private String body;
    private String OwnerUsername;
    private List<String> hashtags;
    private String time;
    private List<String> mentions;

}
