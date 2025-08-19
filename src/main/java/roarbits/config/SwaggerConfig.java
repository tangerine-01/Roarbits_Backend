package roarbits.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        io.swagger.v3.oas.models.security.SecurityScheme bearer =
                new io.swagger.v3.oas.models.security.SecurityScheme()
                    .name("bearerAuth")
                    .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT");
            return new OpenAPI()
                    .info(new Info()
                            .title("Course Registartion Assistant App API")
                            .description("수강신청 웹사이트 API 문서")
                            .version("1.0.0"))
                    .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                    .components(new Components().addSecuritySchemes("bearerAuth", bearer));
    }
}
