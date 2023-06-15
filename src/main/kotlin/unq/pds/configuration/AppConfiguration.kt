package unq.pds.configuration

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.modelmapper.ModelMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AppConfiguration {

    @Bean
    open fun modelMapper(): ModelMapper? {
        return ModelMapper()
    }
}

@OpenAPIDefinition
@Configuration
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
open class SpringDocConfig {
    @Bean
    open fun baseOpenAPI(): OpenAPI {
        return OpenAPI().info(Info().title("Documentation API University").version("1.0.0").description("Developed by: Germán Greco Ventura and Lucas Ziegemann"))
    }
}