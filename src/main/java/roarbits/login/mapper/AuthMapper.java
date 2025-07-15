package roarbits.login.mapper;

import org.mapstruct.Mapper;
import roarbits.login.dto.LoginResponse;
import roarbits.user.dto.SignUpResponse;
import roarbits.user.entity.User;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    SignUpResponse toSignUpResponse(User user);
    LoginResponse toLoginResponse(String accessToken, String refreshToken);
}
