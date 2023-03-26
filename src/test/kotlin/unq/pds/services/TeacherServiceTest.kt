package unq.pds.services

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.service.TeacherServiceImpl
import unq.pds.webservice.dto.TeacherCreateRequestDTO

@SpringBootTest
class TeacherServiceTest {
    @Autowired
    lateinit var teacherService: TeacherServiceImpl

    @BeforeEach
    fun setUp() {

    }

    @Test
    fun `should be create a teacher when when it has valid credentials`() {
        var request =
            TeacherCreateRequestDTO.BuilderTeacherCreateDTO().withFirstName("German").withLastName("Greco Ventura")
                .withEmail("prueba@gmail.com").build()
        var teacher = teacherService.save(request)
        Assertions.assertTrue(teacher.getId() != null)
    }

    @Test
    fun `should throw an exception if firstname is null`() {
        var request = TeacherCreateRequestDTO.BuilderTeacherCreateDTO().withFirstName(null).build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { teacherService.save(request) })

        Assertions.assertEquals(
            "The firstname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if firstname is empty`() {
        var request = TeacherCreateRequestDTO.BuilderTeacherCreateDTO().withFirstName("").build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { teacherService.save(request) })

        Assertions.assertEquals(
            "The firstname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if the firstname has any special characters`() {
        var request = TeacherCreateRequestDTO.BuilderTeacherCreateDTO().withFirstName("J@").build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { teacherService.save(request) })

        Assertions.assertEquals(
            "The firstname can not contain special characters",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if the firstname has any number`() {
        var request = TeacherCreateRequestDTO.BuilderTeacherCreateDTO().withFirstName("Jav1er").build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { teacherService.save(request) })

        Assertions.assertEquals(
            "The firstname can not contain numbers",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if lastname is null`() {
        var request =
            TeacherCreateRequestDTO.BuilderTeacherCreateDTO().withFirstName("Javier").withLastName(null).build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { teacherService.save(request) })

        Assertions.assertEquals(
            "The lastname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if lastname is empty`() {
        var request =
            TeacherCreateRequestDTO.BuilderTeacherCreateDTO().withFirstName("Javier").withLastName("").build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { teacherService.save(request) })

        Assertions.assertEquals(
            "The lastname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if the lastname has any special characters`() {
        assertThrows<Throwable> {
            var request =
                TeacherCreateRequestDTO.BuilderTeacherCreateDTO().withFirstName("Javier").withLastName("Gr#co").build()
            val thrown: RuntimeException =
                Assertions.assertThrows(RuntimeException::class.java, { teacherService.save(request) })

            Assertions.assertEquals(
                "The firstname cannot be empty",
                thrown.message
            )
        }
    }

    @Test
    fun `should throw an exception if the lastname has any number`() {
        var request =
            TeacherCreateRequestDTO.BuilderTeacherCreateDTO().withFirstName("Javier").withLastName("Gr3c0").build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { teacherService.save(request) })

        Assertions.assertEquals(
            "The lastname can not contain numbers",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if email is null`() {
        var request =
            TeacherCreateRequestDTO.BuilderTeacherCreateDTO().withFirstName("Javier").withLastName("Greco")
                .withEmail(null).build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { teacherService.save(request) })

        Assertions.assertEquals(
            "The email cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if email is empty`() {
        var request =
            TeacherCreateRequestDTO.BuilderTeacherCreateDTO().withFirstName("Javier").withLastName("Greco")
                .withEmail("").build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { teacherService.save(request) })

        Assertions.assertEquals(
            "The email cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when the email is already registered`() {
        var request1 =
            TeacherCreateRequestDTO.BuilderTeacherCreateDTO().withFirstName("Javier").withLastName("Ventura")
                .withEmail("repetido@gmail.com").build()
        var request2 =
            TeacherCreateRequestDTO.BuilderTeacherCreateDTO().withFirstName("German").withLastName("Greco")
                .withEmail("repetido@gmail.com").build()

        teacherService.save(request1)
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { teacherService.save(request2) })

        Assertions.assertEquals(
            "The email is already registered",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when the email is not valid`() {
        var request =
            TeacherCreateRequestDTO.BuilderTeacherCreateDTO().withFirstName("Javier").withLastName("Greco")
                .withEmail("juanPerezgmail.com").build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { teacherService.save(request) })

        Assertions.assertEquals(
            "The email is not valid",
            thrown.message
        )
    }


    @Test
    fun `should update teacher name when firstname is valid`() {
        var request =
            TeacherCreateRequestDTO.BuilderTeacherCreateDTO().withFirstName("German").withLastName("Greco")
                .withEmail("prueba@gmail.com").build()
        var teacher = teacherService.save(request)
        teacher.setFirstName("Juan")
        var teacherRecovery = teacherService.update(teacher)
        Assertions.assertTrue(teacherRecovery.getFirstName() == teacher.getFirstName())
    }

    @Test
    fun `should throw an exception when firstname is null`() {
        var request =
            TeacherCreateRequestDTO.BuilderTeacherCreateDTO().withFirstName("German").withLastName("Greco")
                .withEmail("prueba@gmail.com").build()
        var teacher = teacherService.save(request)
        teacher.setFirstName(null)

        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { teacherService.update(teacher) })

        Assertions.assertEquals(
            "The firstname cannot be empty",
            thrown.message
        )

    }

    @Test
    fun `should throw an exception when firstname is empty`() {
        var request =
            TeacherCreateRequestDTO.BuilderTeacherCreateDTO().withFirstName("German").withLastName("Greco")
                .withEmail("prueba@gmail.com").build()
        var teacher = teacherService.save(request)
        teacher.setFirstName("")

        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { teacherService.update(teacher) })

        Assertions.assertEquals(
            "The firstname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when firstname contains numbers`() {
        var request =
            TeacherCreateRequestDTO.BuilderTeacherCreateDTO().withFirstName("German").withLastName("Greco")
                .withEmail("prueba@gmail.com").build()
        var teacher = teacherService.save(request)
        teacher.setFirstName("Jos3")

        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { teacherService.update(teacher) })

        Assertions.assertEquals(
            "The firstname can not contain numbers",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when firstname contains special character`() {
        var request =
            TeacherCreateRequestDTO.BuilderTeacherCreateDTO().withFirstName("German").withLastName("Greco")
                .withEmail("prueba@gmail.com").build()
        var teacher = teacherService.save(request)
        teacher.setFirstName("Jos@")

        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { teacherService.update(teacher) })

        Assertions.assertEquals(
            "The firstname can not contain special characters",
            thrown.message
        )
    }

    @Test
    fun `should update teacher lastname when lastname is valid`() {
        var request =
            TeacherCreateRequestDTO.BuilderTeacherCreateDTO().withFirstName("German").withLastName("Greco")
                .withEmail("prueba@gmail.com").build()
        var teacher = teacherService.save(request)
        teacher.setLastName("Perez")

        var teacherUpdated = teacherService.update(teacher)

        Assertions.assertTrue(teacherUpdated.getLastName() == teacher.getLastName())
    }


    @Test
    fun `should throw an exception when lastname is null`() {
        var request =
            TeacherCreateRequestDTO.BuilderTeacherCreateDTO().withFirstName("German").withLastName("Greco")
                .withEmail("prueba@gmail.com").build()
        var teacher = teacherService.save(request)
        teacher.setLastName(null)

        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { teacherService.update(teacher) })

        Assertions.assertEquals(
            "The lastname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when lastname is empty`() {
        var request =
            TeacherCreateRequestDTO.BuilderTeacherCreateDTO().withFirstName("German").withLastName("Greco")
                .withEmail("prueba@gmail.com").build()
        var teacher = teacherService.save(request)
        teacher.setLastName("")

        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { teacherService.update(teacher) })

        Assertions.assertEquals(
            "The lastname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when lastname contains numbers`() {
        var request =
            TeacherCreateRequestDTO.BuilderTeacherCreateDTO().withFirstName("German").withLastName("Greco")
                .withEmail("prueba@gmail.com").build()
        var teacher = teacherService.save(request)
        teacher.setFirstName("G3reco")

        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { teacherService.update(teacher) })

        Assertions.assertEquals(
            "The firstname can not contain numbers",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when lastname contains special character`() {
        var request =
            TeacherCreateRequestDTO.BuilderTeacherCreateDTO().withFirstName("German").withLastName("Greco")
                .withEmail("prueba@gmail.com").build()
        var teacher = teacherService.save(request)
        teacher.setLastName("Gre@")

        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { teacherService.update(teacher) })

        Assertions.assertEquals(
            "The lastname can not contain special characters",
            thrown.message
        )
    }

    @Test
    fun `should update teacher email when email is valid`() {
        var request =
            TeacherCreateRequestDTO.BuilderTeacherCreateDTO().withFirstName("German").withLastName("Greco")
                .withEmail("prueba@gmail.com").build()
        var teacher = teacherService.save(request)
        teacher.setEmail("juanPerez@gmail.com")
        var teacherRecovery = teacherService.update(teacher)

        Assertions.assertTrue(teacherRecovery.getEmail() == teacher.getEmail())
    }


    @Test
    fun `should the teacher if it exists`() {
        var teacher = teacherService.save(TeacherCreateRequestDTO("German", "Greco Ventura", "prueba@gmail.com"))
        teacher.getId()?.let { teacherService.deleteById(it) }
        Assertions.assertTrue(teacherService.count() == 0)
    }

    @Test
    fun `shouldnt throw an exception when deleting a non-existent teacher`() {
        var teachers = teacherService.count()
        teacherService.deleteById(-1)

        Assertions.assertTrue(teachers == teacherService.count())
    }


    @AfterEach
    fun tearDown() {
        teacherService.clearTeachers()
    }
}