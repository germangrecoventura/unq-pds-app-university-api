package unq.pds

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
open class AppUniversity

fun main(args: Array<String>) {
    runApplication<AppUniversity>(*args)
}