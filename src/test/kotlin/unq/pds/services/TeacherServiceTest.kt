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
        var email = teacher.getEmail()
        Assertions.assertTrue(email == "prueba@gmail.com")
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

    @AfterEach
    fun tearDown() {
        teacherService.clearTeachers()
    }
}
