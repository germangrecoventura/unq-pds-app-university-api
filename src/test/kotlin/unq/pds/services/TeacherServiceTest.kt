package unq.pds.services

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.model.Teacher

@SpringBootTest
class TeacherServiceTest {
    @Autowired
    lateinit var teacherService: TeacherService

    @BeforeEach
    fun setUp() {

    }

    @Test
    fun `should add a teacher`() {
        var teacher = teacherService.save(Teacher("German", "Greco Ventura", "prueba@gmail.com"))
        Assertions.assertTrue(teacher.getId() != null)
    }

    @Test
    fun `should throw an exception if no firstname is null`() {
        assertThrows<Throwable> { teacherService.save(Teacher(null, "Greco Ventura", "prueba@gmail.com")) }
    }

    @Test
    fun `should throw an exception if no lastname is null`() {
        assertThrows<Throwable> { teacherService.save(Teacher("German", null, "prueba@gmail.com")) }
    }

    @Test
    fun `should throw an exception if no email is null`() {
        assertThrows<Throwable> { teacherService.save(Teacher("German", "Greco Ventura", null)) }
    }

    @Test
    fun `should throw an exception if no firstname is empty`() {
        assertThrows<Throwable> { teacherService.save(Teacher("", "Greco Ventura", "prueba@gmail.com")) }
    }

    @Test
    fun `should throw an exception if no lastname is empty`() {
        assertThrows<Throwable> { teacherService.save(Teacher("German", "", "prueba@gmail.com")) }
    }

    @Test
    fun `should throw an exception if no email is empty`() {
        assertThrows<Throwable> { teacherService.save(Teacher("German", "Greco Ventura", "")) }
    }

    @Test
    fun `should change the firstname of the teacher`() {
        var teacher = teacherService.save(Teacher("German", "Greco Ventura", "prueba@gmail.com"))
        teacher.setFirstName("Juan")
        var teacherRecovery = teacherService.save(teacher)
        Assertions.assertTrue(teacherRecovery.getFirstName() == teacher.getFirstName())
    }

    @Test
    fun `should change the lastname of the teacher`() {
        var teacher = teacherService.save(Teacher("German", "Greco Ventura", "prueba@gmail.com"))
        teacher.setLastName("Perez")
        var teacherRecovery = teacherService.save(teacher)
        Assertions.assertTrue(teacherRecovery.getLastName() == teacher.getLastName())
    }

    @Test
    fun `should change the email of the teacher`() {
        var teacher = teacherService.save(Teacher("German", "Greco Ventura", "prueba@gmail.com"))
        teacher.setEmail("juanPerez@gmail.com")
        var teacherRecovery = teacherService.save(teacher)
        Assertions.assertTrue(teacherRecovery.getEmail() == teacher.getEmail())
    }

    @Test
    fun `should delete the teacher`() {
        var teacher = teacherService.save(Teacher("German", "Greco Ventura", "prueba@gmail.com"))
        teacherService.delete(teacher)
        Assertions.assertTrue(teacherService.count() == 0)
    }

    @AfterEach
    fun tearDown() {
        teacherService.clearTeachers()
    }
}
