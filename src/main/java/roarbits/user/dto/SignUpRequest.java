package roarbits.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
    private String username;
    private String password;
    private String email;
}
