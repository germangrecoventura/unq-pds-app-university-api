package unq.pds.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class StudentTest {

    @BeforeEach
    fun setUp() {

    }

    @Test
    fun `should be create a student when when it has valid credentials`() {
        Assertions.assertDoesNotThrow {
            Student("German", "Greco Ventura", "prueba@gmail.com")
        }
    }

    @Test
    fun `should throw an exception if firstname is null`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java,
                { Student(null, null, null) })

        Assertions.assertEquals(
            "The firstname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if firstname is empty`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java,
                { Student("", null, null) })

        Assertions.assertEquals(
            "The firstname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if the firstname has any special characters`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java,
                { Student("Germ@n", null, null) })

        Assertions.assertEquals(
            "The firstname can not contain special characters",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if the firstname has any number`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java,
                { Student("G3rman", null, null) })

        Assertions.assertEquals(
            "The firstname can not contain numbers",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if lastname is null`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java,
                { Student("German", null, null) })

        Assertions.assertEquals(
            "The lastname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if lastname is empty`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java,
                { Student("German", "", null) })

        Assertions.assertEquals(
            "The lastname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if the lastname has any special characters`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java,
                { Student("German", "Grecc#", null) })

        Assertions.assertEquals(
            "The lastname can not contain special characters",
            thrown.message
        )

    }

    @Test
    fun `should throw an exception if the lastname has any number`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java,
                { Student("German", "Gr3ec0", null) })

        Assertions.assertEquals(
            "The lastname can not contain numbers",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if email is null`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java,
                { Student("German", "Greco", null) })
        Assertions.assertEquals(
            "The email cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if email is empty`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java,
                { Student("German", "Greco", "") })
        Assertions.assertEquals(
            "The email cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when updating a firstname with null`() {
        var student = Student("German", "Greco", "german@gmail.com")

        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java,
                { student.setFirstName(null) })
        Assertions.assertEquals(
            "The firstname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when updating a firstname with empty`() {
        var student = Student("German", "Greco", "german@gmail.com")

        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java,
                { student.setFirstName("") })
        Assertions.assertEquals(
            "The firstname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when updating a firstname with numbers`() {
        var student = Student("German", "Greco", "german@gmail.com")

        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java,
                { student.setFirstName("H2") })
        Assertions.assertEquals(
            "The firstname can not contain numbers",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when updating a firstname with special character`() {
        var student = Student("German", "Greco", "german@gmail.com")

        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java,
                { student.setFirstName("H@") })
        Assertions.assertEquals(
            "The firstname can not contain special characters",
            thrown.message
        )
    }

    // TODO FALTA HACER LOS TESTS PARA LOS SETTERS

    @Test
    fun `should throw an exception when updating a lastname with null`() {
        var student = Student("German", "Greco", "german@gmail.com")

        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java,
                { student.setLastName(null) })
        Assertions.assertEquals(
            "The lastname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when updating a lastname with empty`() {
        var student = Student("German", "Greco", "german@gmail.com")

        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java,
                { student.setLastName("") })
        Assertions.assertEquals(
            "The lastname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when updating a lastname with numbers`() {
        var student = Student("German", "Greco", "german@gmail.com")

        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java,
                { student.setLastName("H2") })
        Assertions.assertEquals(
            "The lastname can not contain numbers",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when updating a lastname with special character`() {
        var student = Student("German", "Greco", "german@gmail.com")

        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java,
                { student.setLastName("H@") })
        Assertions.assertEquals(
            "The lastname can not contain special characters",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when updating a email with null`() {
        var student = Student("German", "Greco", "german@gmail.com")

        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java,
                { student.setEmail(null) })
        Assertions.assertEquals(
            "The email cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when updating a email with empty`() {
        var student = Student("German", "Greco", "german@gmail.com")

        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java,
                { student.setEmail("") })
        Assertions.assertEquals(
            "The email cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when an invalid mail is updated`() {
        var student = Student("German", "Greco", "german@gmail.com")

        val thrown: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java,
                { student.setEmail("hola") })
        Assertions.assertEquals(
            "The email is not valid",
            thrown.message
        )
    }
}