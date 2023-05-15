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
import unq.pds.model.builder.BuilderStudent.Companion.aStudent
import unq.pds.model.builder.ProjectBuilder.Companion.aProject
import unq.pds.services.AdminService
import unq.pds.services.ProjectService
import unq.pds.services.builder.BuilderAdminDTO.Companion.aAdminDTO
import unq.pds.services.builder.BuilderLoginDTO.Companion.aLoginDTO
import unq.pds.services.builder.BuilderStudentDTO.Companion.aStudentDTO
import unq.pds.services.builder.BuilderTeacherDTO.Companion.aTeacherDTO
import unq.pds.services.impl.StudentServiceImpl
import unq.pds.services.impl.TeacherServiceImpl
import javax.servlet.http.Cookie

@ExtendWith(SpringExtension::class)
@SpringBootTest
class StudentControllerTest {
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var studentService: StudentServiceImpl

    @Autowired
    lateinit var teacherService: TeacherServiceImpl

    @Autowired
    lateinit var adminService: AdminService

    @Autowired
    lateinit var projectService: ProjectService

    private val mapper = ObjectMapper()

    @Autowired
    lateinit var initializer: Initializer

    @BeforeEach
    fun setUp() {
        initializer.cleanDataBase()
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    fun `should throw a 401 status when trying to create a student and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withEmail("prueba@gmail.com").build()))
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to create students`() {
        val cookie = cookiesStudent()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withEmail("prueba@gmail.com").build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to create students`() {
        val cookie = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to create students`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when the student has a null firstname`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withFirstName(null).build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a empty first name`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withFirstName("").build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has numbers in his first name`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withFirstName("G3rman").build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a special character in his first name`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withFirstName("Germ@n").build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a null last name`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withLastName(null).build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a empty last name`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withLastName("").build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has numbers in his last name`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withLastName("G3rman").build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a special character in his last name`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withLastName("Germ@n").build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a null email`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withEmail(null).build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a empty email`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withEmail("").build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a null password`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withPassword(null).build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a empty password`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withPassword("").build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has an invalid email`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withEmail("german.com").build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the email is already registered`() {
        val cookie = cookiesAdmin()
        studentService.save(aStudentDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }


    @Test
    fun `should throw a 400 status when you save a student with owner is already registered`() {
        val cookie = cookiesAdmin()
        studentService.save(aStudentDTO().build())
        var student2 = aStudentDTO().withEmail("prueba@gmail.com").build()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(student2)
                )
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 401 status when trying to get a student and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to get student if exist`() {
        val cookie = cookiesStudent()
        var student = studentService.save(aStudentDTO().withEmail("prueba@gmail.com").withOwnerGithub("prueba").build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", student.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to get student if exist`() {
        val cookie = cookiesTeacher()
        var student = studentService.save(aStudentDTO().withEmail("pruebaa@gmail.com").build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", student.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to get student if exist`() {
        val cookie = cookiesAdmin()
        var student = studentService.save(aStudentDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", student.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when a student are looking for a student with id null`() {
        val cookie = cookiesStudent()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", null).cookie(cookie)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when a teacher are looking for a student with id null`() {
        val cookie = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", null).cookie(cookie)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when a admin are looking for a student with id null`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", null).cookie(cookie)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when a student does have permissions to get student if not exist`() {
        val cookie = cookiesStudent()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1").cookie(cookie)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when a teacher does have permissions to get student if not exist`() {
        val cookie = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1").cookie(cookie)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when a admin does have permissions to get student if not exist`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1").cookie(cookie)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 401 status when trying to update a student and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudent().build()))
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to update students except yourself`() {
        val cookie = cookiesStudent()
        var student2 = studentService.save(aStudentDTO().withEmail("jose@gmail.com").withOwnerGithub("prueba").build())

        student2.setFirstName("Jose")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(student2))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to update students`() {
        val cookie = cookiesTeacher()
        var student2 = studentService.save(aStudentDTO().withEmail("jose@gmail.com").withOwnerGithub("prueba").build())

        student2.setFirstName("Jose")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(student2))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to update students`() {
        val cookie = cookiesAdmin()
        var student2 = studentService.save(aStudentDTO().withEmail("jose@gmail.com").withOwnerGithub("prueba").build())

        student2.setFirstName("Jose")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(student2))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when you update a student that does not exist`() {
        val cookie = cookiesAdmin()
        var student = aStudent().build()
        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(student))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 400 status when you update a student with first name null`() {
        val cookie = cookiesAdmin()

        var student = studentService.save(aStudentDTO().build())
        val studentToUpdate = aStudentDTO().withId(student.getId()).build()
        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${studentToUpdate.id},\n" +
                            "  \"email\": \"${studentToUpdate.email}\",\n" +
                            "  \"firstName\": ${null},\n" +
                            "  \"lastName\": \"${studentToUpdate.lastName}\"\n" +
                            "}"
                ).cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with first name empty`() {
        val cookie = cookiesAdmin()

        var student = studentService.save(aStudentDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${student.getId()},\n" +
                            "  \"email\": \"${student.getEmail()}\",\n" +
                            "  \"projects\": \"${student.projects}\",\n" +
                            "  \"firstName\": \"${""}\",\n" +
                            "  \"lastName\": \"${student.getLastName()}\"\n" +
                            "}"
                )
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with first name with numbers`() {
        val cookie = cookiesAdmin()
        var student = studentService.save(aStudentDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${student.getId()},\n" +
                            "  \"email\": \"${student.getEmail()}\",\n" +
                            "  \"projects\": \"${student.projects}\",\n" +
                            "  \"firstName\": \"${"G2erman"}\",\n" +
                            "  \"lastName\": \"${student.getLastName()}\"\n" +
                            "}"
                )
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with first name with special character`() {
        val cookie = cookiesAdmin()
        var student = studentService.save(aStudentDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${student.getId()},\n" +
                            "  \"email\": \"${student.getEmail()}\",\n" +
                            "  \"projects\": \"${student.projects}\",\n" +
                            "  \"firstName\": \"${"G@rman"}\",\n" +
                            "  \"lastName\": \"${student.getLastName()}\"\n" +
                            "}"
                )
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }


    @Test
    fun `should throw a 400 status when you update a student with last name null`() {
        val cookie = cookiesAdmin()
        var student = studentService.save(aStudentDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${student.getId()},\n" +
                            "  \"email\": \"${student.getEmail()}\",\n" +
                            "  \"projects\": \"${student.projects}\",\n" +
                            "  \"firstName\": ${student.getFirstName()},\n" +
                            "  \"lastName\": \"${null}\"\n" +
                            "}"
                )
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with last name empty`() {
        val cookie = cookiesAdmin()
        var student = studentService.save(aStudentDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${student.getId()},\n" +
                            "  \"email\": \"${student.getEmail()}\",\n" +
                            "  \"projects\": \"${student.projects}\",\n" +
                            "  \"firstName\": \"${student.getFirstName()}\",\n" +
                            "  \"lastName\": \"${""}\"\n" +
                            "}"
                )
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with last name with numbers`() {
        val cookie = cookiesAdmin()
        var student = studentService.save(aStudentDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${student.getId()},\n" +
                            "  \"email\": \"${student.getEmail()}\",\n" +
                            "  \"projects\": \"${student.projects}\",\n" +
                            "  \"firstName\": \"${"Ger"}\",\n" +
                            "  \"lastName\": \"${"G2erman"}\"\n" +
                            "}"
                )
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with last name with special character`() {
        val cookie = cookiesAdmin()
        var student = studentService.save(aStudentDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${student.getId()},\n" +
                            "  \"email\": \"${student.getEmail()}\",\n" +
                            "  \"projects\": \"${student.projects}\",\n" +
                            "  \"firstName\": \"${"Ger"}\",\n" +
                            "  \"lastName\": \"${"G@rman"}\"\n" +
                            "}"
                )
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }


    @Test
    fun `should throw a 400 status when you update a student with mail null`() {
        val cookie = cookiesAdmin()
        var student = studentService.save(aStudentDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${student.getId()},\n" +
                            "  \"email\": \"${null}\",\n" +
                            "  \"projects\": \"${student.projects}\",\n" +
                            "  \"firstName\": ${student.getFirstName()},\n" +
                            "  \"lastName\": \"${student.getLastName()}\"\n" +
                            "}"
                )
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with mail empty`() {
        val cookie = cookiesAdmin()
        var student = studentService.save(aStudentDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${student.getId()},\n" +
                            "  \"email\": \"${""}\",\n" +
                            "  \"projects\": \"${student.projects}\",\n" +
                            "  \"firstName\": \"${student.getFirstName()}\",\n" +
                            "  \"lastName\": \"${student.getLastName()}\"\n" +
                            "}"
                )
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with an invalid email`() {
        val cookie = cookiesAdmin()
        var student = studentService.save(aStudentDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${student.getId()},\n" +
                            "  \"email\": \"${"germacom"}\",\n" +
                            "  \"projects\": \"${student.projects}\",\n" +
                            "  \"firstName\": \"${student.getFirstName()}\",\n" +
                            "  \"lastName\": \"${student.getLastName()}\"\n" +
                            "}"
                )
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with already registered email`() {
        val cookie = cookiesAdmin()
        var student = studentService.save(aStudentDTO().build())
        var student2 = studentService.save(aStudentDTO().withEmail("hola@gmail.com").withOwnerGithub("prueba").build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${student2.getId()},\n" +
                            "  \"email\": \"${student.getEmail()}\",\n" +
                            "  \"projects\": \"${student2.projects}\",\n" +
                            "  \"firstName\": \"${student2.getFirstName()}\",\n" +
                            "  \"lastName\": \"${student2.getLastName()}\"\n" +
                            "}"
                )
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }


    @Test
    fun `should throw a 400 status when you update a student with owner is already registered`() {
        val cookie = cookiesAdmin()
        var student = studentService.save(aStudentDTO().build())
        var student2 =
            studentService.save(aStudentDTO().withEmail("prueba@gmail.com").withOwnerGithub("prueba").build())
        student2.setOwnerGithub(student.getOwnerGithub())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(student2)
                )
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 401 status when trying to delete a student and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to delete students`() {
        val cookie = cookiesStudent()
        var student2 = studentService.save(aStudentDTO().withEmail("jose@gmail.com").withOwnerGithub("prueba").build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", student2.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to delete students`() {
        val cookie = cookiesTeacher()
        var student2 = studentService.save(aStudentDTO().withEmail("jose@gmail.com").withOwnerGithub("prueba").build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", student2.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isUnauthorized)
    }


    @Test
    fun `should throw a 200 status when a admin does have permissions to delete students`() {
        val cookie = cookiesAdmin()
        var student = studentService.save(aStudentDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", student.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when admin deleting a student with id null`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", null).cookie(cookie)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status you want to delete a student that does not exist`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", "2").cookie(cookie)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to add project`() {
        val cookie = cookiesTeacher()
        val project = projectService.save(aProject().build())
        var student = studentService.save(aStudentDTO().withEmail("jose@gmail.com").withOwnerGithub("prueba").build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/students/addProject/${student.getId().toString()}/${
                    project.getId().toString()
                }"
            ).accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to add project except yourself`() {
        val cookie = cookiesStudent()
        val project = projectService.save(aProject().build())
        var student = studentService.save(aStudentDTO().withEmail("jose@gmail.com").withOwnerGithub("prueba").build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/students/addProject/${student.getId().toString()}/${
                    project.getId().toString()
                }"
            ).accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to add project`() {
        val cookie = cookiesAdmin()
        val project = projectService.save(aProject().build())
        var student = studentService.save(aStudentDTO().withEmail("jose@gmail.com").withOwnerGithub("prueba").build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/students/addProject/${student.getId().toString()}/${
                    project.getId().toString()
                }"
            ).accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to get all students`() {
        val cookie = cookiesStudent()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students/getAll").accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to get all students`() {
        val cookie = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students/getAll").accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to get all students`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students/getAll").accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 401 status when trying to get all students and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students/getAll").accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized)
    }

    private fun cookiesTeacher(): Cookie? {
        val teacher = teacherService.save(aTeacherDTO().build())
        val login = aLoginDTO().withEmail(teacher.getEmail()).withPassword("funciona").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(status().isOk)

        return response.andReturn().response.cookies[0]
    }

    private fun cookiesStudent(): Cookie? {
        val student = studentService.save(aStudentDTO().build())
        val login = aLoginDTO().withEmail(student.getEmail()).withPassword("funciona").build()
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
        val login = aLoginDTO().withEmail(admin.getEmail()).withPassword("funciona").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(status().isOk)

        return response.andReturn().response.cookies[0]
    }
}