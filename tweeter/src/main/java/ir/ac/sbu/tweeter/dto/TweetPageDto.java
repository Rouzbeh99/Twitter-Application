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
public class TweetPageDto {
    @Builder.Default
    private List<TweetResponseDto> tweets = new ArrayList<>();
}
