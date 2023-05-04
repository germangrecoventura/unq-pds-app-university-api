package unq.pds.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import unq.pds.Initializer
import unq.pds.services.builder.BuilderLoginDTO
import unq.pds.services.builder.BuilderStudentDTO.Companion.aStudentDTO
import unq.pds.services.builder.BuilderTeacherDTO.Companion.aTeacherDTO
import unq.pds.services.impl.StudentServiceImpl
import unq.pds.services.impl.TeacherServiceImpl
import javax.servlet.http.Cookie

@ExtendWith(SpringExtension::class)
@SpringBootTest
class TeacherControllerTest {
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var studentService: StudentServiceImpl

    @Autowired
    lateinit var teacherService: TeacherServiceImpl
    private val mapper = ObjectMapper()

    @Autowired
    lateinit var initializer: Initializer

    @BeforeEach
    fun setUp() {
        initializer.cleanDataBase()
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to create teacher`() {
        val cookie = cookiesStudent()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withEmail("prueba@gmail.com").build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a valid teacher is created`() {
        val cookies = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withEmail("germa@gmail.com").build()))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when if firstname is null to create`() {
        val cookies = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withFirstName(null).build()))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when if firstname is empty to create`() {
        val cookies = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withFirstName("").build()))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when if the firstname has any special characters to create`() {
        val cookies = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withFirstName("J@").build()))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }


    @Test
    fun `should throw a 404 status when if the firstname has any number to create`() {
        val cookies = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withFirstName("Jav1er").build()))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when if lastname is null to create`() {
        val cookies = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withLastName(null).build()))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when if lastname is empty to create`() {
        val cookies = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withLastName("").build()))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when if the lastname has any special characters to create`() {
        val cookies = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withLastName("J@").build()))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }


    @Test
    fun `should throw a 404 status when if the lastname has any number to create`() {
        val cookies = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withLastName("Jav1er").build()))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }


    @Test
    fun `should throw a 404 status when if email is null to create`() {
        val cookies = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withEmail(null).build()))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when if email is empty to create`() {
        val cookies = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withEmail("").build()))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when the email is already registered to create`() {
        val cookies = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().build()))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when the email is not valid to create`() {
        val cookies = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withEmail("juanPerezgmail.com").build()))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 200 status when you are looking for a teacher if it exists`() {
        val cookie = cookiesTeacher()
        var teacher = teacherService.save(aTeacherDTO().withEmail("juanPerez@gmail.com").build())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/teachers").accept(MediaType.APPLICATION_JSON)
                .param("id", teacher.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when you are looking for a teacher does not exist`() {
        val cookie = cookiesTeacher()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/teachers").accept(MediaType.APPLICATION_JSON)
                .param("id", (-1).toString()).cookie(cookie)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 200 status when update teacher firstname the teacher`() {
        val cookies = cookiesTeacher()
        var teacher = teacherService.findByEmail("german@gmail.com")
        teacher.setFirstName("Juan")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(teacher))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when update teacher lastname the teacher`() {
        val cookies = cookiesTeacher()
        var teacher = teacherService.findByEmail("german@gmail.com")
        teacher.setLastName("Perez")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(teacher))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when update teacher email the teacher`() {
        val cookies = cookiesTeacher()
        var teacher = teacherService.findByEmail("german@gmail.com")
        teacher.setEmail("perez@gmail.com")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(teacher))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when update teacher password the teacher`() {
        val cookies = cookiesTeacher()
        var teacher = teacherService.findByEmail("german@gmail.com")
        teacher.setPassword("Perez")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(teacher))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isOk)
    }


    @Test
    fun `should throw a 401 status when a teacher does not have permissions to update firstname to teacher`() {
        val cookie = cookiesTeacher()
        var request = aTeacherDTO().withEmail("prueba@gmail.com").build()
        val teacher = teacherService.save(request)
        teacher.setFirstName("Pablo")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(teacher))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to update lastname to teacher`() {
        val cookie = cookiesTeacher()
        var request = aTeacherDTO().withEmail("prueba@gmail.com").build()
        val teacher = teacherService.save(request)
        teacher.setLastName("Lopez")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(teacher))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to update email to teacher`() {
        val cookie = cookiesTeacher()
        var request = aTeacherDTO().withEmail("prueba@gmail.com").build()
        val teacher = teacherService.save(request)
        teacher.setEmail("jose@gmail.com")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(teacher))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to update password to teacher`() {
        val cookie = cookiesTeacher()
        var request = aTeacherDTO().withEmail("prueba@gmail.com").build()
        val teacher = teacherService.save(request)
        teacher.setPassword("Jose")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(teacher))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }


    @Test
    fun `should throw a 401 status when delete a teacher if it exists to student`() {
        val cookie = cookiesStudent()
        val teacher = teacherService.save(aTeacherDTO().withEmail("germanF@gmail.com").build())


        mockMvc.perform(
            MockMvcRequestBuilders.delete("/teachers").accept(MediaType.APPLICATION_JSON)
                .param("id", teacher.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when delete a teacher if it exists`() {
        val cookie = cookiesTeacher()
        val teacher = teacherService.save(aTeacherDTO().withEmail("germanF@gmail.com").build())


        mockMvc.perform(
            MockMvcRequestBuilders.delete("/teachers").accept(MediaType.APPLICATION_JSON)
                .param("id", teacher.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when deleting a non-existent teacher`() {
        val cookie = cookiesTeacher()

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/teachers").accept(MediaType.APPLICATION_JSON)
                .param("id", (-1).toString()).cookie(cookie)
        )
            .andExpect(status().isNotFound)
    }


    @Test
    fun `should throw a 200 status when recover an empty list of teachers when recover all and there is no persistence to student`() {
        val cookie = cookiesStudent()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teachers/getAll").accept(MediaType.APPLICATION_JSON)

                .cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when recover an empty list of teachers when recover all and there is no persistence`() {
        val cookie = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teachers/getAll").accept(MediaType.APPLICATION_JSON)

                .cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when recover a list with two teachers when recover all and there are exactly two persisted`() {
        val cookie = cookiesTeacher()
        teacherService.save(aTeacherDTO().withEmail("germanF@gmail.com").build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teachers/getAll").accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        )
            .andExpect(status().isOk)
    }
    /*



        @Test
        fun `should update teacher name when firstname is valid`() {
            var request = aTeacherDTO().withEmail("prueba@gmail.com").build()
            var teacher = teacherService.save(request)
            teacher.setFirstName("Juan")
            var studentUpdated = teacherService.update(teacher)
            Assertions.assertTrue(studentUpdated.getFirstName() == teacher.getFirstName())
        }


        @Test
        fun `should update teacher lastname when lastname is valid`() {
            var request = aTeacherDTO().withEmail("prueba@gmail.com").build()
            var teacher = teacherService.save(request)
            teacher.setLastName("Perez")

            var studentUpdated = teacherService.update(teacher)

            Assertions.assertTrue(studentUpdated.getLastName() == teacher.getLastName())
        }

        @Test
        fun `should update teacher email when email is valid`() {
            var request = aTeacherDTO().withEmail("prueba@gmail.com").build()
            var teacher = teacherService.save(request)
            teacher.setEmail("juanPerez@gmail.com")
            var teacherUpdated = teacherService.update(teacher)

            Assertions.assertTrue(teacherUpdated.getEmail() == teacher.getEmail())
        }

        @Test
        fun `should not update the teacher if the email already exists`() {
            var request = aTeacherDTO().withEmail("prueba@gmail.com").build()
            teacherService.save(request)
            var request2 = aTeacherDTO().withEmail("jose@gmail.com").build()
            var teacher2 = teacherService.save(request2)
            teacher2.setEmail("prueba@gmail.com")
            val thrown: RuntimeException =
                Assertions.assertThrows(RuntimeException::class.java) { teacherService.update(teacher2) }


            Assertions.assertEquals(
                "The email is already registered",
                thrown.message
            )
        }

        @Test
        fun `should throw an exception when update a non-existent teacher`() {
            var teacher = teacherService.save(aTeacherDTO().build())
            teacher.setId(-5)

            val thrown: RuntimeException =
                Assertions.assertThrows(RuntimeException::class.java) { teacherService.update(teacher) }


            Assertions.assertEquals(
                "Not found the teacher with id -5",
                thrown.message
            )
        }



        }
        */


    private fun cookiesStudent(): Cookie? {
        val student = studentService.save(aStudentDTO().withPassword("funciona").build())
        val login = BuilderLoginDTO().withRole("STUDENT").withEmail(student.getEmail()).withPassword("funciona").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(status().isOk)

        return response.andReturn().response.cookies[0]
    }

    private fun cookiesTeacher(): Cookie? {
        val teacher = teacherService.save(aTeacherDTO().withPassword("funciona").build())
        val login = BuilderLoginDTO().withRole("TEACHER").withEmail(teacher.getEmail()).withPassword("funciona").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(status().isOk)

        return response.andReturn().response.cookies[0]
    }
}