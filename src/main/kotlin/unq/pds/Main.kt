package unq.pds

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication
import unq.pds.services.*
import unq.pds.services.builder.BuilderStudentDTO

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
open class AppUniversity : CommandLineRunner {

@Autowired
private lateinit var initializer: Initializer
    override fun run(vararg args: String?) {
        initializer.cleanDataBase()
        initializer.loadData()
    }
}

fun main(args: Array<String>) {
    runApplication<AppUniversity>(*args)
}