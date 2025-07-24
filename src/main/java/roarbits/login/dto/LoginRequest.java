package roarbits.login.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    @Email(message = "email")
    @NotBlank(message = "email")
    private String email;

    @NotBlank(message = "password")
    private String password;
}
