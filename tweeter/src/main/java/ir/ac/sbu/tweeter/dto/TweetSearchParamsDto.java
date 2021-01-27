package ir.ac.sbu.tweeter.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TweetSearchParamsDto {
    private String ownerUsername;
    private String hashtag;
    private LocalDateTime startDate;
    private LocalDateTime finishDate;
}
