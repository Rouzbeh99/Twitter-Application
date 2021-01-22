package ir.ac.sbu.tweeter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDto {
    String newUsername;
    String newName;
    String newPassword;
}
