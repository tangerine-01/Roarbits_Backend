package roarbits.login.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import roarbits.login.dto.LoginResponse;
import roarbits.user.dto.SignUpResponse;
import roarbits.user.entity.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-22T22:21:57+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.13.jar, environment: Java 17.0.13 (Oracle Corporation)"
)
@Component
public class AuthMapperImpl implements AuthMapper {

    @Override
    public SignUpResponse toSignUpResponse(User user) {
        if ( user == null ) {
            return null;
        }

        SignUpResponse.SignUpResponseBuilder signUpResponse = SignUpResponse.builder();

        signUpResponse.id( user.getId() );
        signUpResponse.email( user.getEmail() );

        return signUpResponse.build();
    }

    @Override
    public LoginResponse toLoginResponse(String accessToken, String refreshToken) {
        if ( accessToken == null && refreshToken == null ) {
            return null;
        }

        LoginResponse loginResponse = new LoginResponse();

        return loginResponse;
    }
}
