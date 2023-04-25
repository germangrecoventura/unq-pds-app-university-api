package unq.pds.services

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.Initializer
import unq.pds.model.builder.BuilderStudent.Companion.aStudent
import unq.pds.model.builder.ProjectBuilder
import unq.pds.services.builder.BuilderStudentDTO.Companion.aStudentDTO
import javax.management.InvalidAttributeValueException

@SpringBootTest
class StudentServiceTest {

    @Autowired lateinit var studentService: StudentService
    @Autowired lateinit var projectService: ProjectService
    @Autowired lateinit var initializer: Initializer

    @BeforeEach
    fun tearDown() {
        initializer.cleanDataBase()
    }

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
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { studentService.save(request) }

        Assertions.assertEquals(
            "The firstname cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if the firstname has any special characters`() {
        var request = aStudentDTO().withFirstName("J@").build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { studentService.save(request) }

        Assertions.assertEquals(
            "The firstname can not contain special characters",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if the firstname has any number`() {
        var request = aStudentDTO().withFirstName("Jav1er").build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { studentService.save(request) }

        Assertions.assertEquals(
            "The firstname can not contain numbers",
            thrown!!.message
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
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { studentService.save(request) }

        Assertions.assertEquals(
            "The lastname cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if the lastname has any special characters`() {
        var request = aStudentDTO().withLastName("Gr#co").build()

        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { studentService.save(request) }

        Assertions.assertEquals(
            "The lastname can not contain special characters",
            thrown!!.message
        )

    }

    @Test
    fun `should throw an exception if the lastname has any number`() {
        var request = aStudentDTO().withLastName("Gr3c0").build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { studentService.save(request) }

        Assertions.assertEquals(
            "The lastname can not contain numbers",
            thrown!!.message
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
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { studentService.save(request) }

        Assertions.assertEquals(
            "The email cannot be empty",
            thrown!!.message
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
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { studentService.save(request) }

        Assertions.assertEquals(
            "The email is not valid",
            thrown!!.message
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
        val thrown: NoSuchElementException =
            Assertions.assertThrows(NoSuchElementException::class.java) { studentService.update(aStudent().build()) }


        Assertions.assertEquals(
            "Student does not exist",
            thrown.message
        )
    }


    @Test
    fun `should delete a student if it exists`() {
        var student = studentService.save(aStudentDTO().build())
        student.getId()?.let { studentService.deleteById(it) }
        Assertions.assertTrue(studentService.count() == 0)
    }


    @Test
    fun `should throw an exception when deleting a non-existent student`() {
        val thrown: NoSuchElementException =
            Assertions.assertThrows(NoSuchElementException::class.java) { studentService.deleteById(-1) }

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

    @Test
    fun `should add a project to a student when it was not previously added and both exist`() {
        val student = studentService.save(aStudentDTO().build())
        val project = projectService.save(ProjectBuilder.aProject().build())
        Assertions.assertEquals(0, student.projects.size)
        val groupWithAProject = studentService.addProject(student.getId()!!, project.getId()!!)
        Assertions.assertEquals(1, groupWithAProject.projects.size)
    }

    @Test
    fun `should throw an exception when trying to add the same project to a student twice and both exist`() {
        val student = studentService.save(aStudentDTO().build())
        val project = projectService.save(ProjectBuilder.aProject().build())
        studentService.addProject(student.getId()!!, project.getId()!!)
        try {
            studentService.addProject(student.getId()!!, project.getId()!!)
        } catch (e: CloneNotSupportedException) {
            Assertions.assertEquals("The project has already been added", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to add a project to a student and the project does not exist`() {
        val student = studentService.save(aStudentDTO().build())
        try {
            studentService.addProject(student.getId()!!, -1)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no project with that id", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to add a project to a student and the student does not exist`() {
        val project = projectService.save(ProjectBuilder.aProject().build())
        try {
            studentService.addProject(-1, project.getId()!!)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("Not found the student with id -1", e.message)
        }
    }

    @Test
    fun `should recover an empty list of students when recover all and there is no persistence`() {
        Assertions.assertEquals(0, studentService.readAll().size)
    }

    @Test
    fun `should recover a list with two students when recover all and there are exactly two persisted`() {
        studentService.save(aStudentDTO().build())
        studentService.save(aStudentDTO().withEmail("germanF@gmail.com").build())
        val students = studentService.readAll()

        Assertions.assertEquals(2, students.size)
        Assertions.assertTrue(students.any { it.getEmail() == "german@gmail.com" })
        Assertions.assertTrue(students.any { it.getEmail() == "germanF@gmail.com" })
    }
}