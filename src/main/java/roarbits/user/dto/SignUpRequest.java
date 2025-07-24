package roarbits.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequest {
    @NotBlank(message = "name")
    private String name;

    @Email(message = "email")
    @NotBlank(message = "email")
    private String email;

    @NotBlank(message = "password")
    private String password;

    @NotBlank(message = "password")
    private String passwordConfirm;
}
