package unq.pds.configuration

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