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
import unq.pds.services.AdminService
import unq.pds.services.builder.BuilderAdminDTO.Companion.aAdminDTO
import unq.pds.services.builder.BuilderLoginDTO.Companion.aLoginDTO
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

    @Autowired
    lateinit var adminService: AdminService

    private val mapper = ObjectMapper()

    @Autowired
    lateinit var initializer: Initializer

    @BeforeEach
    fun setUp() {
        initializer.cleanDataBase()
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    fun `should throw a 401 status when trying to create a teacher and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withEmail("prueba@gmail.com").build()))
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
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
    fun `should throw a 401 status when a teacher does not have permissions to create teacher`() {
        val cookie = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withEmail("prueba@gmail.com").build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to create teacher`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withEmail("prueba@gmail.com").build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when if firstname is null to create`() {
        val cookies = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withFirstName(null).build()))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when if firstname is empty to create`() {
        val cookies = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withFirstName("").build()))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when if the firstname has any special characters to create`() {
        val cookies = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withFirstName("J@").build()))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }


    @Test
    fun `should throw a 400 status when if the firstname has any number to create`() {
        val cookies = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withFirstName("Jav1er").build()))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when if lastname is null to create`() {
        val cookies = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withLastName(null).build()))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when if lastname is empty to create`() {
        val cookies = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withLastName("").build()))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when if the lastname has any special characters to create`() {
        val cookies = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withLastName("J@").build()))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }


    @Test
    fun `should throw a 400 status when if the lastname has any number to create`() {
        val cookies = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withLastName("Jav1er").build()))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }


    @Test
    fun `should throw a 400 status when if email is null to create`() {
        val cookies = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withEmail(null).build()))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when if email is empty to create`() {
        val cookies = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withEmail("").build()))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the email is already registered to create`() {
        val cookies = cookiesAdmin()
        teacherService.save(aTeacherDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().build()))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the email is not valid to create`() {
        val cookies = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withEmail("juanPerezgmail.com").build()))
                .cookie(cookies)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to get student if exist`() {
        val cookie = cookiesStudent()
        var teacher = teacherService.save(aTeacherDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teachers").accept(MediaType.APPLICATION_JSON)
                .param("id", teacher.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to get student if exist`() {
        val cookie = cookiesTeacher()
        var teacher = teacherService.save(aTeacherDTO().withEmail("prueba@gmail.com").build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teachers").accept(MediaType.APPLICATION_JSON)
                .param("id", teacher.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to get student if exist`() {
        val cookie = cookiesAdmin()
        var teacher = teacherService.save(aTeacherDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teachers").accept(MediaType.APPLICATION_JSON)
                .param("id", teacher.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 401 status when trying to get a teacher and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teachers").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 400 status when a student are looking for a teacher with id null`() {
        val cookie = cookiesStudent()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/teachers").accept(MediaType.APPLICATION_JSON)
                .param("id", null).cookie(cookie)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when a teacher are looking for a teacher with id null`() {
        val cookie = cookiesTeacher()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/teachers").accept(MediaType.APPLICATION_JSON)
                .param("id", null).cookie(cookie)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when a admin are looking for a teacher with id null`() {
        val cookie = cookiesAdmin()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/teachers").accept(MediaType.APPLICATION_JSON)
                .param("id", null).cookie(cookie)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when a student are looking for a teacher does not exist`() {
        val cookie = cookiesStudent()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/teachers").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1").cookie(cookie)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when a teacher are looking for a teacher does not exist`() {
        val cookie = cookiesTeacher()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/teachers").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1").cookie(cookie)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when a admin are looking for a teacher does not exist`() {
        val cookie = cookiesAdmin()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/teachers").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1").cookie(cookie)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 401 status when trying to update a teacher and is not authenticated`() {
        val teacher = teacherService.save(aTeacherDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(teacher))
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to update teacher`() {
        val cookie = cookiesStudent()
        var teacher = teacherService.save(aTeacherDTO().build())

        teacher.setFirstName("Jose")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(teacher))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to update teacher except yourself`() {
        val cookie = cookiesTeacher()
        var teacher = teacherService.save(aTeacherDTO().withEmail("newTeacher@gmail.com").build())

        teacher.setFirstName("Jose")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(teacher))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to update teachers`() {
        val cookie = cookiesAdmin()
        var teacher = teacherService.save(aTeacherDTO().build())

        teacher.setFirstName("Pepe")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(teacher))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when admin update a teacher with id null`() {
        val cookie = cookiesAdmin()
        var teacher = teacherService.save(aTeacherDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${null},\n" +
                            "  \"email\": \"${teacher.getEmail()}\",\n" +
                            "  \"firstName\": ${teacher.getFirstName()},\n" +
                            "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                            "  \"password\": \"${teacher.getPassword()}\"\n" +
                            "}"
                ).cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when admin update a teacher with id empty`() {
        val cookie = cookiesAdmin()
        var teacher = teacherService.save(aTeacherDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${""},\n" +
                            "  \"email\": \"${teacher.getEmail()}\",\n" +
                            "  \"firstName\": ${teacher.getFirstName()},\n" +
                            "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                            "  \"password\": \"${teacher.getPassword()}\"\n" +
                            "}"
                ).cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when admin update a teacher with first name null`() {
        val cookie = cookiesAdmin()
        var teacher = teacherService.save(aTeacherDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${teacher.getId()},\n" +
                            "  \"email\": \"${teacher.getEmail()}\",\n" +
                            "  \"firstName\": ${null},\n" +
                            "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                            "  \"password\": \"${teacher.getPassword()}\"\n" +
                            "}"
                ).cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when admin update a teacher with first name empty`() {
        val cookie = cookiesAdmin()
        var teacher = teacherService.save(aTeacherDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${teacher.getId()},\n" +
                            "  \"email\": \"${teacher.getEmail()}\",\n" +
                            "  \"firstName\": ${""},\n" +
                            "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                            "  \"password\": \"${teacher.getPassword()}\"\n" +
                            "}"
                ).cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when admin update a teacher with first name with number`() {
        val cookie = cookiesAdmin()
        var teacher = teacherService.save(aTeacherDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${teacher.getId()},\n" +
                            "  \"email\": \"${teacher.getEmail()}\",\n" +
                            "  \"firstName\": ${"55"},\n" +
                            "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                            "  \"password\": \"${teacher.getPassword()}\"\n" +
                            "}"
                ).cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when admin update a teacher with first name with special character `() {
        val cookie = cookiesAdmin()
        var teacher = teacherService.save(aTeacherDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${teacher.getId()},\n" +
                            "  \"email\": \"${teacher.getEmail()}\",\n" +
                            "  \"firstName\": ${"Ger@n"},\n" +
                            "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                            "  \"password\": \"${teacher.getPassword()}\"\n" +
                            "}"
                ).cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }


    @Test
    fun `should throw a 400 status when admin update a teacher with last name null`() {
        val cookie = cookiesAdmin()
        var teacher = teacherService.save(aTeacherDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${teacher.getId()},\n" +
                            "  \"email\": \"${teacher.getEmail()}\",\n" +
                            "  \"firstName\": ${teacher.getFirstName()},\n" +
                            "  \"lastName\": \"${null}\"\n" +
                            "  \"password\": \"${teacher.getPassword()}\"\n" +
                            "}"
                ).cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when admin update a teacher with last name empty`() {
        val cookie = cookiesAdmin()
        var teacher = teacherService.save(aTeacherDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${teacher.getId()},\n" +
                            "  \"email\": \"${teacher.getEmail()}\",\n" +
                            "  \"firstName\": ${teacher.getFirstName()},\n" +
                            "  \"lastName\": \"${""}\"\n" +
                            "  \"password\": \"${teacher.getPassword()}\"\n" +
                            "}"
                ).cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when admin update a teacher with last name with number`() {
        val cookie = cookiesAdmin()
        var teacher = teacherService.save(aTeacherDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${teacher.getId()},\n" +
                            "  \"email\": \"${teacher.getEmail()}\",\n" +
                            "  \"firstName\": ${teacher.getFirstName()},\n" +
                            "  \"lastName\": \"${"P3rez"}\"\n" +
                            "  \"password\": \"${teacher.getPassword()}\"\n" +
                            "}"
                ).cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when admin update a teacher with last name with special character `() {
        val cookie = cookiesAdmin()
        var teacher = teacherService.save(aTeacherDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${teacher.getId()},\n" +
                            "  \"email\": \"${teacher.getEmail()}\",\n" +
                            "  \"firstName\": ${teacher.getFirstName()},\n" +
                            "  \"lastName\": \"${"#"}\"\n" +
                            "  \"password\": \"${teacher.getPassword()}\"\n" +
                            "}"
                ).cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when admin update a teacher with password null`() {
        val cookie = cookiesAdmin()
        var teacher = teacherService.save(aTeacherDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${teacher.getId()},\n" +
                            "  \"email\": \"${teacher.getEmail()}\",\n" +
                            "  \"firstName\": ${teacher.getFirstName()},\n" +
                            "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                            "  \"password\": \"${null}\"\n" +
                            "}"
                ).cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when admin update a teacher with password empty`() {
        val cookie = cookiesAdmin()
        var teacher = teacherService.save(aTeacherDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${teacher.getId()},\n" +
                            "  \"email\": \"${teacher.getEmail()}\",\n" +
                            "  \"firstName\": ${teacher.getFirstName()},\n" +
                            "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                            "  \"password\": \"${""}\"\n" +
                            "}"
                ).cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with mail null`() {
        val cookie = cookiesAdmin()
        var teacher = teacherService.save(aTeacherDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${teacher.getId()},\n" +
                            "  \"email\": \"${null}\",\n" +
                            "  \"password\": \"${teacher.getPassword()}\",\n" +
                            "  \"firstName\": ${teacher.getFirstName()},\n" +
                            "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                            "}"
                )
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with mail empty`() {
        val cookie = cookiesAdmin()
        var teacher = teacherService.save(aTeacherDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${teacher.getId()},\n" +
                            "  \"email\": \"${""}\",\n" +
                            "  \"password\": \"${teacher.getPassword()}\",\n" +
                            "  \"firstName\": \"${teacher.getFirstName()}\",\n" +
                            "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                            "}"
                )
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with an invalid email`() {
        val cookie = cookiesAdmin()
        var teacher = teacherService.save(aTeacherDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${teacher.getId()},\n" +
                            "  \"email\": \"${"germacom"}\",\n" +
                            "  \"password\": \"${teacher.getPassword()}\",\n" +
                            "  \"firstName\": \"${teacher.getFirstName()}\",\n" +
                            "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                            "}"
                )
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when admin update a teacher with already registered email`() {
        val cookie = cookiesAdmin()
        var teacher = teacherService.save(aTeacherDTO().build())
        var teacher2 = teacherService.save(aTeacherDTO().withEmail("hola@gmail.com").build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${teacher2.getId()},\n" +
                            "  \"email\": \"${teacher.getEmail()}\",\n" +
                            "  \"password\": \"${teacher2.getPassword()}\",\n" +
                            "  \"firstName\": \"${teacher2.getFirstName()}\",\n" +
                            "  \"lastName\": \"${teacher2.getLastName()}\"\n" +
                            "}"
                )
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when admin update a teacher if not exists`() {
        val cookie = cookiesAdmin()
        var teacher = teacherService.save(aTeacherDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${"-20"},\n" +
                            "  \"email\": \"${teacher.getEmail()}\",\n" +
                            "  \"password\": \"${teacher.getPassword()}\",\n" +
                            "  \"firstName\": \"${teacher.getFirstName()}\",\n" +
                            "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                            "}"
                )
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 401 status when trying to delete a teacher and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/teachers").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to delete teachers`() {
        val cookie = cookiesStudent()
        val teacher = teacherService.save(aTeacherDTO().withEmail("germanF@gmail.com").build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/teachers").accept(MediaType.APPLICATION_JSON)
                .param("id", teacher.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to delete teachers`() {
        val cookie = cookiesTeacher()
        val teacher = teacherService.save(aTeacherDTO().withEmail("germanF@gmail.com").build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/teachers").accept(MediaType.APPLICATION_JSON)
                .param("id", teacher.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to delete teachers`() {
        val cookie = cookiesAdmin()
        val teacher = teacherService.save(aTeacherDTO().withEmail("germanF@gmail.com").build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/teachers").accept(MediaType.APPLICATION_JSON)
                .param("id", teacher.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when admin deleting a teacher with id null`() {
        val cookie = cookiesAdmin()

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/teachers").accept(MediaType.APPLICATION_JSON)
                .param("id", null).cookie(cookie)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when admin deleting a non-existent teacher`() {
        val cookie = cookiesAdmin()

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/teachers").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1").cookie(cookie)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 200 status when student recover an empty list of teachers when recover all and there is no persistence to student`() {
        val cookie = cookiesStudent()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teachers/getAll").accept(MediaType.APPLICATION_JSON)

                .cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when teacher recover an empty list of teachers when recover all and there is no persistence to student`() {
        val cookie = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teachers/getAll").accept(MediaType.APPLICATION_JSON)

                .cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when admin recover an empty list of teachers when recover all and there is no persistence to student`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teachers/getAll").accept(MediaType.APPLICATION_JSON)

                .cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 401 status when trying to recover all teachers and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teachers/getAll").accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized)
    }

    private fun cookiesTeacher(): Cookie? {
        teacherService.save(aTeacherDTO().build())
        val login = aLoginDTO().build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(status().isOk)

        return response.andReturn().response.cookies[0]
    }

    private fun cookiesStudent(): Cookie? {
        studentService.save(aStudentDTO().build())
        val login = aLoginDTO().withRole("STUDENT").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(status().isOk)

        return response.andReturn().response.cookies[0]
    }

    private fun cookiesAdmin(): Cookie? {
        val admin = adminService.save(aAdminDTO().build())
        val login = aLoginDTO().withEmail(admin.getEmail()).withRole("ADMIN").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(status().isOk)

        return response.andReturn().response.cookies[0]
    }
}