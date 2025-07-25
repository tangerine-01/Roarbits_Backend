package roarbits.login.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import roarbits.login.dto.LoginResponse;
import roarbits.user.dto.SignUpResponse;
import roarbits.user.entity.User;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "name", source = "user.name")
    SignUpResponse toSignUpResponse(User user);
    LoginResponse toLoginResponse(String accessToken, String refreshToken);
}