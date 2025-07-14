package roarbits.login.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter // 필드에 대한 getter 메서드를 자동으로 생성합니다.
@NoArgsConstructor // 기본 생성자를 자동으로 생성합니다. (JSON 역직렬화에 필요)
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자를 자동으로 생성합니다.
public class LoginResponse {
    private String accessToken;  // 발급된 Access Token
    private String refreshToken; // 발급된 Refresh Token
}
