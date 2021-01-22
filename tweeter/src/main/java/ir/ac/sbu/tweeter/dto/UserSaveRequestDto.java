package ir.ac.sbu.tweeter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSaveRequestDto {
    String name;
    String username;
    String password;
}
