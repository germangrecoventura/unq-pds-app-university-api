package unq.pds

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class AppUniversity

fun main(args: Array<String>) {
    runApplication<AppUniversity>(*args)
}