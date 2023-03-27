package unq.pds.services

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.api.dtos.StudentCreateRequestDTO
import unq.pds.services.impl.StudentServiceImpl

@SpringBootTest
class StudentServiceTest {
    @Autowired
    lateinit var studentService: StudentServiceImpl

    @BeforeEach
    fun setUp() {

    }

    @Test
    fun `should be create a student when when it has valid credentials`() {
        var request =
            StudentCreateRequestDTO.BuilderStudentCreateDTO().withFirstName("German").withLastName("Greco Ventura")
                .withEmail("prueba@gmail.com").build()
        var student = studentService.save(request)
        Assertions.assertTrue(student.getId() != null)
    }

    @Test
    fun `should throw an exception if firstname is null`() {
        var request = StudentCreateRequestDTO.BuilderStudentCreateDTO().withFirstName(null).build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { studentService.save(request) })

        Assertions.assertEquals(
            "The firstname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if firstname is empty`() {
        var request = StudentCreateRequestDTO.BuilderStudentCreateDTO().withFirstName("").build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { studentService.save(request) })

        Assertions.assertEquals(
            "The firstname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if the firstname has any special characters`() {
        var request = StudentCreateRequestDTO.BuilderStudentCreateDTO().withFirstName("J@").build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { studentService.save(request) })

        Assertions.assertEquals(
            "The firstname can not contain special characters",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if the firstname has any number`() {
        var request = StudentCreateRequestDTO.BuilderStudentCreateDTO().withFirstName("Jav1er").build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { studentService.save(request) })

        Assertions.assertEquals(
            "The firstname can not contain numbers",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if lastname is null`() {
        var request =
            StudentCreateRequestDTO.BuilderStudentCreateDTO().withFirstName("Javier").withLastName(null).build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { studentService.save(request) })

        Assertions.assertEquals(
            "The lastname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if lastname is empty`() {
        var request =
            StudentCreateRequestDTO.BuilderStudentCreateDTO().withFirstName("Javier").withLastName("").build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { studentService.save(request) })

        Assertions.assertEquals(
            "The lastname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if the lastname has any special characters`() {
        assertThrows<Throwable> {
            var request =
                StudentCreateRequestDTO.BuilderStudentCreateDTO().withFirstName("Javier").withLastName("Gr#co")
                    .build()
            val thrown: RuntimeException =
                Assertions.assertThrows(RuntimeException::class.java, { studentService.save(request) })

            Assertions.assertEquals(
                "The firstname cannot be empty",
                thrown.message
            )
        }
    }

    @Test
    fun `should throw an exception if the lastname has any number`() {
        var request =
            StudentCreateRequestDTO.BuilderStudentCreateDTO().withFirstName("Javier").withLastName("Gr3c0").build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { studentService.save(request) })

        Assertions.assertEquals(
            "The lastname can not contain numbers",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if email is null`() {
        var request =
            StudentCreateRequestDTO.BuilderStudentCreateDTO().withFirstName("Javier").withLastName("Greco")
                .withEmail(null).build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { studentService.save(request) })

        Assertions.assertEquals(
            "The email cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if email is empty`() {
        var request =
            StudentCreateRequestDTO.BuilderStudentCreateDTO().withFirstName("Javier").withLastName("Greco")
                .withEmail("").build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { studentService.save(request) })

        Assertions.assertEquals(
            "The email cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when the email is already registered`() {
        var request1 =
            StudentCreateRequestDTO.BuilderStudentCreateDTO().withFirstName("Javier").withLastName("Ventura")
                .withEmail("repetido@gmail.com").build()
        var request2 =
            StudentCreateRequestDTO.BuilderStudentCreateDTO().withFirstName("German").withLastName("Greco")
                .withEmail("repetido@gmail.com").build()

        studentService.save(request1)
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { studentService.save(request2) })

        Assertions.assertEquals(
            "The email is already registered",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when the email is not valid`() {
        var request =
            StudentCreateRequestDTO.BuilderStudentCreateDTO().withFirstName("Javier").withLastName("Greco")
                .withEmail("juanPerezgmail.com").build()
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { studentService.save(request) })

        Assertions.assertEquals(
            "The email is not valid",
            thrown.message
        )
    }


    @Test
    fun `should update student name when firstname is valid`() {
        var request =
            StudentCreateRequestDTO.BuilderStudentCreateDTO().withFirstName("German").withLastName("Greco")
                .withEmail("prueba@gmail.com").build()
        var student = studentService.save(request)
        student.setFirstName("Juan")
        var studentUpdated = studentService.update(student)
        Assertions.assertTrue(studentUpdated.getFirstName() == student.getFirstName())
    }



    // TODO: DE ACA PARA ABAJO ROMPEN PORQUE EN REALIDAD SON TEST DE MODELO Y NO DE SERVICES
    // TODO: ARREGLAR MAÑANA. TAMBIEN FALTA UN TEST DE ACTUALIZAR CUANDO NO SE ENCUENTRA EL ESTUDIANTE












    @Test
    fun `should throw an exception when firstname is null`() {
        var request =
            StudentCreateRequestDTO.BuilderStudentCreateDTO().withFirstName("German").withLastName("Greco")
                .withEmail("prueba@gmail.com").build()
        var student = studentService.save(request)
        student.setFirstName(null)

        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { studentService.update(student) })

        Assertions.assertEquals(
            "The firstname cannot be empty",
            thrown.message
        )

    }

    @Test
    fun `should throw an exception when firstname is empty`() {
        var request =
            StudentCreateRequestDTO.BuilderStudentCreateDTO().withFirstName("German").withLastName("Greco")
                .withEmail("prueba@gmail.com").build()
        var student = studentService.save(request)
        student.setFirstName("")

        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { studentService.update(student) })

        Assertions.assertEquals(
            "The firstname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when firstname contains numbers`() {
        var request =
            StudentCreateRequestDTO.BuilderStudentCreateDTO().withFirstName("German").withLastName("Greco")
                .withEmail("prueba@gmail.com").build()
        var student = studentService.save(request)
        student.setFirstName("Jos3")

        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { studentService.update(student) })

        Assertions.assertEquals(
            "The firstname can not contain numbers",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when firstname contains special character`() {
        var request =
            StudentCreateRequestDTO.BuilderStudentCreateDTO().withFirstName("German").withLastName("Greco")
                .withEmail("prueba@gmail.com").build()
        var student = studentService.save(request)
        student.setFirstName("Jos@")

        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { studentService.update(student) })

        Assertions.assertEquals(
            "The firstname can not contain special characters",
            thrown.message
        )
    }

    @Test
    fun `should update student lastname when lastname is valid`() {
        var request =
            StudentCreateRequestDTO.BuilderStudentCreateDTO().withFirstName("German").withLastName("Greco")
                .withEmail("prueba@gmail.com").build()
        var student = studentService.save(request)
        student.setLastName("Perez")

        var studentUpdated = studentService.update(student)

        Assertions.assertTrue(studentUpdated.getLastName() == student.getLastName())
    }


    @Test
    fun `should throw an exception when lastname is null`() {
        var request =
            StudentCreateRequestDTO.BuilderStudentCreateDTO().withFirstName("German").withLastName("Greco")
                .withEmail("prueba@gmail.com").build()
        var student = studentService.save(request)
        student.setLastName(null)

        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { studentService.update(student) })

        Assertions.assertEquals(
            "The lastname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when lastname is empty`() {
        var request =
            StudentCreateRequestDTO.BuilderStudentCreateDTO().withFirstName("German").withLastName("Greco")
                .withEmail("prueba@gmail.com").build()
        var student = studentService.save(request)
        student.setLastName("")

        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { studentService.update(student) })

        Assertions.assertEquals(
            "The lastname cannot be empty",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when lastname contains numbers`() {
        var request =
            StudentCreateRequestDTO.BuilderStudentCreateDTO().withFirstName("German").withLastName("Greco")
                .withEmail("prueba@gmail.com").build()
        var student = studentService.save(request)
        student.setFirstName("G3reco")

        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { studentService.update(student) })

        Assertions.assertEquals(
            "The firstname can not contain numbers",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when lastname contains special character`() {
        var request =
            StudentCreateRequestDTO.BuilderStudentCreateDTO().withFirstName("German").withLastName("Greco")
                .withEmail("prueba@gmail.com").build()
        var student = studentService.save(request)
        student.setLastName("Gre@")

        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java, { studentService.update(student) })

        Assertions.assertEquals(
            "The lastname can not contain special characters",
            thrown.message
        )
    }

    @Test
    fun `should update student email when email is valid`() {
        var request =
            StudentCreateRequestDTO.BuilderStudentCreateDTO().withFirstName("German").withLastName("Greco")
                .withEmail("prueba@gmail.com").build()
        var student = studentService.save(request)
        student.setEmail("juanPerez@gmail.com")
        var studentUpdated = studentService.update(student)

        Assertions.assertTrue(studentUpdated.getEmail() == student.getEmail())
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
            Assertions.assertThrows(RuntimeException::class.java, { studentService.deleteById(-1) })

        Assertions.assertEquals(
            "The student with id -1 is not registered",
            thrown.message
        )
    }

    @AfterEach
    fun tearDown() {
        studentService.clearStudents()
    }
}