package unq.pds.services

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.api.dtos.TeacherCreateRequestDTO
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
        var teacher = teacherService.save(TeacherCreateRequestDTO("German", "Greco Ventura", "prueba@gmail.com"))
        Assertions.assertTrue(teacher.getId() != null)
    }

    @Test
    fun `should throw an exception if no firstname is null`() {
        assertThrows<Throwable> { teacherService.save(TeacherCreateRequestDTO(null, "Greco Ventura", "prueba@gmail.com")) }
    }

    @Test
    fun `should throw an exception if no firstname is empty`() {
        assertThrows<Throwable> { teacherService.save(TeacherCreateRequestDTO("", "Greco Ventura", "prueba@gmail.com")) }
    }

    @Test
    fun `should throw an exception if the first name has any special characters`() {
        assertThrows<Throwable> { teacherService.save(TeacherCreateRequestDTO("J@", "Greco Ventura", "prueba@gmail.com")) }
    }

    @Test
    fun `should throw an exception if the first name has any number`() {
        assertThrows<Throwable> { teacherService.save(TeacherCreateRequestDTO("Jav1er", "Greco Ventura", "prueba@gmail.com")) }
    }

    @Test
    fun `should throw an exception if no lastname is null`() {
        assertThrows<Throwable> { teacherService.save(TeacherCreateRequestDTO("German", null, "prueba@gmail.com")) }
    }

    @Test
    fun `should throw an exception if no lastname is empty`() {
        assertThrows<Throwable> { teacherService.save(TeacherCreateRequestDTO("German", "", "prueba@gmail.com")) }
    }

    @Test
    fun `should throw an exception if the lastname has any special characters`() {
        assertThrows<Throwable> { teacherService.save(TeacherCreateRequestDTO("German", "Grec@ Ventura", "prueba@gmail.com")) }
    }

    @Test
    fun `should throw an exception if the lastname has any number`() {
        assertThrows<Throwable> { teacherService.save(TeacherCreateRequestDTO("German", "Grec0 Ventur4", "prueba@gmail.com")) }
    }

    @Test
    fun `should throw an exception if no email is null`() {
        assertThrows<Throwable> { teacherService.save(TeacherCreateRequestDTO("German", "Greco Ventura", null)) }
    }

    @Test
    fun `should throw an exception if no email is empty`() {
        assertThrows<Throwable> { teacherService.save(TeacherCreateRequestDTO("German", "Greco Ventura", "")) }
    }

    @Test
    fun `should throw an exception if a teacher with already registered email is added`() {
        assertThrows<Throwable> { teacherService.save(TeacherCreateRequestDTO("German", "Greco Ventura", "")) }
    }

    @Test
    fun `should change the firstname of the teacher`() {
        var teacher = teacherService.save(TeacherCreateRequestDTO("German", "Greco Ventura", "prueba@gmail.com"))
        var n = teacher.getFirstName()
        teacher.setFirstName("Juan")
        var teacherRecovery = teacherService.update(teacher)
        Assertions.assertTrue(teacherRecovery.getFirstName() == teacher.getFirstName())
    }

    @Test
    fun `should change the lastname of the teacher`() {
        var teacher = teacherService.save(TeacherCreateRequestDTO("German", "Greco Ventura", "prueba@gmail.com"))
        teacher.setLastName("Perez")
        var teacherRecovery = teacherService.update(teacher)
        Assertions.assertTrue(teacherRecovery.getLastName() == teacher.getLastName())
    }

    @Test
    fun `should change the email of the teacher`() {
        var teacher = teacherService.save(TeacherCreateRequestDTO("German", "Greco Ventura", "prueba@gmail.com"))
        teacher.setEmail("juanPerez@gmail.com")
        var teacherRecovery = teacherService.update(teacher)
        Assertions.assertTrue(teacherRecovery.getEmail() == teacher.getEmail())
    }

    @Test
    fun `should throw an exception when the email is not valid`() {
        var teacher = teacherService.save(TeacherCreateRequestDTO("German", "Greco Ventura", "prueba@gmail.com"))
        teacher.setEmail("juanPerezgmail.com")
        assertThrows<Throwable> { teacherService.update(teacher) }
    }

    @Test
    fun `should throw an exception when the email is already registered`() {
        var teacher = teacherService.save(TeacherCreateRequestDTO("German", "Greco Ventura", "prueba@gmail.com"))
        teacherService.save(TeacherCreateRequestDTO("Jose", "Martinez", "repetido@gmail.com"))
        teacher.setEmail("repetido@gmail.com")
        assertThrows<Throwable> { teacherService.update(teacher) }
    }

    @Test
    fun `should delete the teacher`() {
        var teacher = teacherService.save(TeacherCreateRequestDTO("German", "Greco Ventura", "prueba@gmail.com"))
        teacherService.delete(teacher)
        Assertions.assertTrue(teacherService.count() == 0)
    }

    @AfterEach
    fun tearDown() {
        teacherService.clearTeachers()
    }
}
