package unq.pds.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.model.builder.BuilderRepository.Companion.aRepository
import javax.management.InvalidAttributeValueException

@SpringBootTest
class RepositoryTest {

    @Test
    fun `should be create a repository when when it has valid credentials`() {
        Assertions.assertDoesNotThrow {
            aRepository().build()
        }
    }

    @Test
    fun `should throw an exception if id is null`() {
        Assertions.assertThrows(RuntimeException::class.java) { aRepository().withId(null).build() }
    }

    @Test
    fun `should throw an exception if name is null`() {
        Assertions.assertThrows(RuntimeException::class.java) { aRepository().withName(null).build() }
    }

    @Test
    fun `should throw an exception if owner is null`() {
        Assertions.assertThrows(RuntimeException::class.java) { aRepository().withOwner(null).build() }
    }

    @Test
    fun `should throw an exception if url is null`() {
        Assertions.assertThrows(RuntimeException::class.java) { aRepository().withOwner(null).build() }
    }

    @Test
    fun `should throw an exception if name is empty`() {
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(
                InvalidAttributeValueException::class.java
            )
            { aRepository().withName("").build() }

        Assertions.assertEquals(
            "Name cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if name has special character not valid`() {
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(
                InvalidAttributeValueException::class.java
            )
            { aRepository().withName("App@jose#").build() }

        Assertions.assertEquals(
            "The name cannot contain special characters except - and _",
            thrown!!.message
        )
    }
}