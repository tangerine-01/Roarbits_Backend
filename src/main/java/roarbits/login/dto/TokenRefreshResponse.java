package roarbits.login.dto; // 또는 roarbits.login.auth.dto

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter // 필드에 대한 getter 메서드 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성 (JSON 역직렬화에 필요)
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자 자동 생성
public class TokenRefreshResponse {
    private String accessToken; // 새로 발급된 Access Token
    private String refreshToken; // (선택 사항) 갱신된 Refresh Token 또는 기존 Refresh Token
}
