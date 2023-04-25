package unq.pds.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.model.builder.BuilderStudent.Companion.aStudent
import unq.pds.model.builder.ProjectBuilder.Companion.aProject
import javax.management.InvalidAttributeValueException

@SpringBootTest
class StudentTest {

    @Test
    fun `should be create a student when when it has valid credentials`() {
        Assertions.assertDoesNotThrow {
            aStudent()
        }
    }

    @Test
    fun `should throw an exception if firstname is null`() {
        Assertions.assertThrows(RuntimeException::class.java) { aStudent().withFirstName(null).build() }
    }

    @Test
    fun `should throw an exception if firstname is empty`() {
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(
                InvalidAttributeValueException::class.java
            )
            { aStudent().withFirstName("").build() }

        Assertions.assertEquals(
            "The firstname cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if the firstname has any special characters`() {
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(
                InvalidAttributeValueException::class.java
            )
            { aStudent().withFirstName("Germ@n").build() }

        Assertions.assertEquals(
            "The firstname can not contain special characters",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if the firstname has any number`() {
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(
                InvalidAttributeValueException::class.java
            )
            { aStudent().withFirstName("G3rman").build() }

        Assertions.assertEquals(
            "The firstname can not contain numbers",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if lastname is null`() {
        Assertions.assertThrows(RuntimeException::class.java) { aStudent().withLastName(null).build() }
    }

    @Test
    fun `should throw an exception if lastname is empty`() {
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(
                InvalidAttributeValueException::class.java
            )
            { aStudent().withLastName("").build() }

        Assertions.assertEquals(
            "The lastname cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if the lastname has any special characters`() {
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(
                InvalidAttributeValueException::class.java
            )
            { aStudent().withLastName("Grec#").build() }

        Assertions.assertEquals(
            "The lastname can not contain special characters",
            thrown!!.message
        )

    }

    @Test
    fun `should throw an exception if the lastname has any number`() {
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(
                InvalidAttributeValueException::class.java
            )
            { aStudent().withLastName("Gr3co").build() }

        Assertions.assertEquals(
            "The lastname can not contain numbers",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if email is null`() {
        Assertions.assertThrows(RuntimeException::class.java) { aStudent().withEmail(null).build() }
    }

    @Test
    fun `should throw an exception if email is empty`() {
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(
                InvalidAttributeValueException::class.java
            )
            { aStudent().withEmail("").build() }
        Assertions.assertEquals(
            "The email cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception when updating a firstname with null`() {
        val student = aStudent().build()

        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(
                InvalidAttributeValueException::class.java
            )
            { student.setFirstName(null) }
        Assertions.assertEquals(
            "The firstname cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception when updating a firstname with empty`() {
        val student = aStudent().build()

        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(
                InvalidAttributeValueException::class.java
            )
            { student.setFirstName("") }
        Assertions.assertEquals(
            "The firstname cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception when updating a firstname with numbers`() {
        val student = aStudent().build()

        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(
                InvalidAttributeValueException::class.java
            )
            { student.setFirstName("H2") }
        Assertions.assertEquals(
            "The firstname can not contain numbers",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception when updating a firstname with special character`() {
        val student = aStudent().build()

        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(
                InvalidAttributeValueException::class.java
            )
            { student.setFirstName("H@") }
        Assertions.assertEquals(
            "The firstname can not contain special characters",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception when updating a lastname with null`() {
        val student = aStudent().build()

        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(
                InvalidAttributeValueException::class.java
            )
            { student.setLastName(null) }
        Assertions.assertEquals(
            "The lastname cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception when updating a lastname with empty`() {
        val student = aStudent().build()

        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(
                InvalidAttributeValueException::class.java
            )
            { student.setLastName("") }
        Assertions.assertEquals(
            "The lastname cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception when updating a lastname with numbers`() {
        val student = aStudent().build()

        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(
                InvalidAttributeValueException::class.java
            )
            { student.setLastName("H2") }
        Assertions.assertEquals(
            "The lastname can not contain numbers",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception when updating a lastname with special character`() {
        val student = aStudent().build()

        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(
                InvalidAttributeValueException::class.java
            )
            { student.setLastName("H@") }
        Assertions.assertEquals(
            "The lastname can not contain special characters",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception when updating a email with null`() {
        val student = aStudent().build()

        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(
                InvalidAttributeValueException::class.java
            )
            { student.setEmail(null) }
        Assertions.assertEquals(
            "The email cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception when updating a email with empty`() {
        val student = aStudent().build()

        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(
                InvalidAttributeValueException::class.java
            )
            { student.setEmail("") }
        Assertions.assertEquals(
            "The email cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception when an invalid mail is updated`() {
        val student = aStudent().build()

        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(
                InvalidAttributeValueException::class.java
            )
            { student.setEmail("hola") }
        Assertions.assertEquals(
            "The email is not valid",
            thrown!!.message
        )
    }

    @Test
    fun `should add a project when it has not been added previously`() {
        val student = aStudent().build()
        Assertions.assertEquals(0, student.projects.size)
        student.addProject(aProject().build())
        Assertions.assertEquals(1, student.projects.size)
    }

    @Test
    fun `should throw an exception when trying to add the same project to a student twice`() {
        val student = aStudent().build()
        student.addProject(aProject().build())
        try {
            student.addProject(aProject().build())
        } catch (e: CloneNotSupportedException) {
            Assertions.assertEquals("The project has already been added", e.message)
        }
    }
}