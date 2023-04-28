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
import unq.pds.services.builder.BuilderStudentDTO.Companion.aStudentDTO
import unq.pds.services.impl.StudentServiceImpl

@ExtendWith(SpringExtension::class)
@SpringBootTest
class StudentControllerTest {
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var studentService: StudentServiceImpl
    private val mapper = ObjectMapper()

    @Autowired lateinit var initializer: Initializer

    @BeforeEach
    fun setUp() {
        initializer.cleanDataBase()
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    fun `should throw a 200 status when a valid student is created`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().build()))
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when the student has a null firstname`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withFirstName(null).build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a empty first name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withFirstName("").build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has numbers in his first name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withFirstName("G3rman").build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a special character in his first name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withFirstName("Germ@n").build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a null last name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withLastName(null).build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a empty last name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withLastName("").build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has numbers in his last name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withLastName("G3rman").build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a special character in his last name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withLastName("Germ@n").build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a null email`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withEmail(null).build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a empty email`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withEmail("").build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a null password`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withPassword(null).build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a empty password`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withPassword("").build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has an invalid email`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withEmail("german.com").build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the email is already registered`() {
        studentService.save(aStudentDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }


    @Test
    fun `should throw a 200 status when you are looking for a student if it exists`() {
        var student = studentService.save(aStudentDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", student.getId().toString())
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when you are looking for a student does not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", 2.toString())
        )
            .andExpect(status().isNotFound)
    }


    @Test
    fun `should throw a 200 status when you update a student who exists and with valid credentials`() {
        var student = studentService.save(aStudentDTO().build())

        student.setFirstName("Jose")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(student))
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when you update a student that does not exist`() {
        var student = aStudent().build()
        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(student))
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }


    @Test
    fun `should throw a 400 status when you update a student with first name null`() {
        var student = studentService.save(aStudentDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${student.getId()},\n" +
                            "  \"email\": \"${student.getEmail()}\",\n" +
                            "  \"projects\": \"${student.projects}\",\n" +
                            "  \"firstName\": ${null},\n" +
                            "  \"lastName\": \"${student.getLastName()}\"\n" +
                            "}"
                )
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with first name empty`() {
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
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with first name with numbers`() {
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
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with first name with special character`() {
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
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }


    @Test
    fun `should throw a 400 status when you update a student with last name null`() {
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
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with last name empty`() {
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
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with last name with numbers`() {
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
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with last name with special character`() {
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
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }


    @Test
    fun `should throw a 400 status when you update a student with mail null`() {
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
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with mail empty`() {
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
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with an invalid email`() {
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
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with already registered email`() {
        var student = studentService.save(aStudentDTO().build())
        var student2 = studentService.save(aStudentDTO().withEmail("hola@gmail.com").build())

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
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }


    @Test
    fun `should throw a 200 status when you want to delete an existing student`() {
        var student = studentService.save(aStudentDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", student.getId().toString())
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status you want to delete a student that does not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", 2.toString())
        )
            .andExpect(status().isNotFound)
    }
}