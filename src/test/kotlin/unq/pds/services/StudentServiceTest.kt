package unq.pds.services

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.api.dtos.StudentCreateRequestDTO
import unq.pds.services.builder.BuilderStudentDTO.Companion.aStudentDTO
import unq.pds.services.impl.StudentServiceImpl

@SpringBootTest
class StudentServiceTest {
    @Autowired
    lateinit var studentService: StudentServiceImpl

    @Test
    fun `should be create a student when when it has valid credentials`() {
        var student = studentService.save(aStudentDTO().build())
        Assertions.assertTrue(student.getId() != null)
    }

    @Test
    fun `should throw an exception if firstname is null`() {
        var request = aStudentDTO().withFirstName(null).build()

        Assertions.assertThrows(RuntimeException::class.java) { studentService.save(request) }
    }

    @Test
    fun `should throw an exception if firstname is empty`() {
        var request = aStudentDTO().withFirstName("").build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) { studentService.save(request) }

        Assertions.assertEquals(
            "The firstname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if the firstname has any special characters`() {
        var request = aStudentDTO().withFirstName("J@").build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) { studentService.save(request) }

        Assertions.assertEquals(
            "The firstname can not contain special characters",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if the firstname has any number`() {
        var request = aStudentDTO().withFirstName("Jav1er").build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) { studentService.save(request) }

        Assertions.assertEquals(
            "The firstname can not contain numbers",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if lastname is null`() {
        var request = aStudentDTO().withLastName(null).build()

        Assertions.assertThrows(RuntimeException::class.java) { studentService.save(request) }
    }

    @Test
    fun `should throw an exception if lastname is empty`() {
        var request = aStudentDTO().withLastName("").build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) { studentService.save(request) }

        Assertions.assertEquals(
            "The lastname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if the lastname has any special characters`() {
        var request = aStudentDTO().withLastName("Gr#co").build()

        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) { studentService.save(request) }

        Assertions.assertEquals(
            "The lastname can not contain special characters",
            thrown.message
        )

    }

    @Test
    fun `should throw an exception if the lastname has any number`() {
        var request = aStudentDTO().withLastName("Gr3c0").build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) { studentService.save(request) }

        Assertions.assertEquals(
            "The lastname can not contain numbers",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if email is null`() {
        var request = aStudentDTO().withEmail(null).build()

        Assertions.assertThrows(RuntimeException::class.java) { studentService.save(request) }
    }

    @Test
    fun `should throw an exception if email is empty`() {
        var request = aStudentDTO().withEmail("").build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) { studentService.save(request) }

        Assertions.assertEquals(
            "The email cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when the email is already registered`() {
        var request1 = aStudentDTO().withEmail("repetido@gmail.com").build()
        var request2 = aStudentDTO().withEmail("repetido@gmail.com").build()

        studentService.save(request1)
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) { studentService.save(request2) }

        Assertions.assertEquals(
            "The email is already registered",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when the email is not valid`() {
        var request = aStudentDTO().withEmail("juanPerezgmail.com").build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) { studentService.save(request) }

        Assertions.assertEquals(
            "The email is not valid",
            thrown.message
        )
    }


    @Test
    fun `should update student name when firstname is valid`() {
        var request = aStudentDTO().withEmail("prueba@gmail.com").build()
        var student = studentService.save(request)
        student.setFirstName("Juan")
        var studentUpdated = studentService.update(student)
        Assertions.assertTrue(studentUpdated.getFirstName() == student.getFirstName())
    }


    @Test
    fun `should update student lastname when lastname is valid`() {
        var request = aStudentDTO().withEmail("prueba@gmail.com").build()
        var student = studentService.save(request)
        student.setLastName("Perez")

        var studentUpdated = studentService.update(student)

        Assertions.assertTrue(studentUpdated.getLastName() == student.getLastName())
    }

    @Test
    fun `should update student email when email is valid`() {
        var request = aStudentDTO().withEmail("prueba@gmail.com").build()
        var student = studentService.save(request)
        student.setEmail("juanPerez@gmail.com")
        var studentUpdated = studentService.update(student)

        Assertions.assertTrue(studentUpdated.getEmail() == student.getEmail())
    }

    @Test
    fun `should not update the student if the email already exists`() {
        var request = aStudentDTO().withEmail("prueba@gmail.com").build()
        studentService.save(request)
        var request2 = aStudentDTO().withEmail("jose@gmail.com").build()
        var student = studentService.save(request2)
        student.setEmail("prueba@gmail.com")
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) { studentService.update(student) }


        Assertions.assertEquals(
            "The email is already registered",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when update a non-existent student`() {
        var student = studentService.save(aStudentDTO().build())
        student.setId(-5)

        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) { studentService.update(student) }


        Assertions.assertEquals(
            "Not found the student with id -5",
            thrown.message
        )
    }


    @Test
    fun `should delete a student if it exists`() {
        var student = studentService.save(StudentCreateRequestDTO("German", "Greco Ventura", "prueba@gmail.com"))
        student.getId()?.let { studentService.deleteById(it) }
        Assertions.assertTrue(studentService.count() == 0)
    }


    @Test
    fun `should throw an exception when deleting a non-existent student`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) { studentService.deleteById(-1) }

        Assertions.assertEquals(
            "The student with id -1 is not registered",
            thrown.message
        )
    }

    @Test
    fun `should return a student when searched for by id`() {
        var student = studentService.save(aStudentDTO().build())
        var studentRecovery = studentService.findById(student.getId()!!)

        Assertions.assertTrue(studentRecovery.getId() == student.getId())
    }

    @Test
    fun `should throw an exception if the student does not exist`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) { studentService.findById(-1) }

        Assertions.assertEquals(
            "Not found the student with id -1",
            thrown.message
        )
    }

    @Test
    fun `should return a student when searched for by email`() {
        var student = studentService.save(aStudentDTO().build())
        var studentRecovery = studentService.findByEmail(student.getEmail()!!)

        Assertions.assertTrue(studentRecovery.getId() == student.getId())
    }

    @Test
    fun `should throw an exception if the email does not exist`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) { studentService.findByEmail("german@gmial.com") }

        Assertions.assertEquals(
            "Not found the student with email german@gmial.com",
            thrown.message
        )
    }

    @AfterEach
    fun tearDown() {
        studentService.clearStudents()
    }
}