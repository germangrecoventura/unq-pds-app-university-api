package unq.pds.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.model.builder.BuilderTeacher.Companion.aTeacher

@SpringBootTest
class TeacherTest {

    @Test
    fun `should be create a teacher when when it has valid credentials`() {
        Assertions.assertDoesNotThrow {
            aTeacher()
        }
    }

    @Test
    fun `should throw an exception if firstname is null`() {
        Assertions.assertThrows(RuntimeException::class.java) { aTeacher().withFirstName(null).build() }
    }

    @Test
    fun `should throw an exception if firstname is empty`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) { aTeacher().withFirstName("").build() }

        Assertions.assertEquals(
            "The firstname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if the firstname has any special characters`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) { aTeacher().withFirstName("Germ@n").build() }

        Assertions.assertEquals(
            "The firstname can not contain special characters",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if the firstname has any number`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java
            ) { aTeacher().withFirstName("G3rman").build() }

        Assertions.assertEquals(
            "The firstname can not contain numbers",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if lastname is null`() {
        Assertions.assertThrows(RuntimeException::class.java) { aTeacher().withLastName(null).build() }
    }

    @Test
    fun `should throw an exception if lastname is empty`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java
            )
            { aTeacher().withLastName("").build() }

        Assertions.assertEquals(
            "The lastname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if the lastname has any special characters`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java
            ) { aTeacher().withLastName("Grec@").build() }

        Assertions.assertEquals(
            "The lastname can not contain special characters",
            thrown.message
        )

    }

    @Test
    fun `should throw an exception if the lastname has any number`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java
            )
            { aTeacher().withLastName("Gr3co").build() }

        Assertions.assertEquals(
            "The lastname can not contain numbers",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if email is null`() {
        Assertions.assertThrows(RuntimeException::class.java) { aTeacher().withEmail(null).build() }
    }

    @Test
    fun `should throw an exception if email is empty`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java
            )
            { aTeacher().withEmail("").build() }
        Assertions.assertEquals(
            "The email cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if email is invalid`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java
            )
            { aTeacher().withEmail("dasdasd").build() }
        Assertions.assertEquals(
            "The email is not valid",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when updating a firstname with null`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java
            )
            { aTeacher().build().setFirstName(null) }
        Assertions.assertEquals(
            "The firstname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when updating a firstname with empty`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java
            )
            { aTeacher().build().setFirstName("") }
        Assertions.assertEquals(
            "The firstname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when updating a firstname with numbers`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java
            )
            { aTeacher().build().setFirstName("H2") }
        Assertions.assertEquals(
            "The firstname can not contain numbers",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when updating a firstname with special character`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java
            )
            { aTeacher().build().setFirstName("H@") }
        Assertions.assertEquals(
            "The firstname can not contain special characters",
            thrown.message
        )
    }


    @Test
    fun `should throw an exception when updating a lastname with null`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java
            )
            { aTeacher().build().setLastName(null) }
        Assertions.assertEquals(
            "The lastname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when updating a lastname with empty`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java
            )
            { aTeacher().build().setLastName("") }
        Assertions.assertEquals(
            "The lastname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when updating a lastname with numbers`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java
            )
            { aTeacher().build().setLastName("H2") }
        Assertions.assertEquals(
            "The lastname can not contain numbers",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when updating a lastname with special character`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java
            )
            { aTeacher().build().setLastName("H@") }
        Assertions.assertEquals(
            "The lastname can not contain special characters",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when updating a email with null`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java
            )
            { aTeacher().build().setEmail(null) }
        Assertions.assertEquals(
            "The email cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when updating a email with empty`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java
            )
            { aTeacher().build().setEmail("") }
        Assertions.assertEquals(
            "The email cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when an invalid mail is updated`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java
            )
            { aTeacher().build().setEmail("hola") }
        Assertions.assertEquals(
            "The email is not valid",
            thrown.message
        )
    }
}