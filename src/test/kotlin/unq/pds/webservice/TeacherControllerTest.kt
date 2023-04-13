package unq.pds.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
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
import unq.pds.model.builder.BuilderTeacher.Companion.aTeacher
import unq.pds.services.builder.BuilderTeacherDTO.Companion.aTeacherDTO
import unq.pds.services.impl.TeacherServiceImpl

@ExtendWith(SpringExtension::class)
@SpringBootTest
class TeacherControllerTest {
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var teacherService: TeacherServiceImpl
    private val mapper = ObjectMapper()

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    fun `should throw a 200 status when a valid teacher is created`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().build()))
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when the teacher has a null firstname`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withFirstName(null).build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the teacher has a empty first name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withFirstName("").build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the teacher has numbers in his first name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withFirstName("G3rman").build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the teacher has a special character in his first name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withFirstName("Germ@n").build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the teacher has a null last name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withLastName(null).build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the teacher has a empty last name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withLastName("").build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the teacher has numbers in his last name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withLastName("G3rman").build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the teacher has a special character in his last name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withLastName("Germ@n").build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the teacher has a null email`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withEmail(null).build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the teacher has a empty email`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withEmail("").build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the teacher has an invalid email`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().withEmail("german.com").build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the email is already registered`() {
        teacherService.save(aTeacherDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.post("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aTeacherDTO().build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }


    @Test
    fun `should throw a 200 status when you are looking for a teacher if it exists`() {
        var teacher = teacherService.save(aTeacherDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/teachers").accept(MediaType.APPLICATION_JSON)
                .param("id", teacher.getId().toString())
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when you are looking for a teacher does not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/teachers").accept(MediaType.APPLICATION_JSON)
                .param("id", 2.toString())
        )
            .andExpect(status().isNotFound)
    }


    @Test
    fun `should throw a 200 status when you update a teacher who exists and with valid credentials`() {
        var teacher = teacherService.save(aTeacherDTO().build())

        teacher.setFirstName("Jose")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(teacher))
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when you update a teacher that does not exist`() {
        var teacher = aTeacher().build()
        teacher.setId(5)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(teacher))
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }


    @Test
    fun `should throw a 400 status when you update a teacher with first name null`() {
        var teacher = teacherService.save(aTeacherDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${teacher.getId()},\n" +
                            "  \"email\": \"${teacher.getEmail()}\",\n" +
                            "  \"firstName\": ${null},\n" +
                            "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                            "}"
                )
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a teacher with first name empty`() {
        var teacher = teacherService.save(aTeacherDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${teacher.getId()},\n" +
                            "  \"email\": \"${teacher.getEmail()}\",\n" +
                            "  \"firstName\": \"${""}\",\n" +
                            "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                            "}"
                )
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a teacher with first name with numbers`() {
        var teacher = teacherService.save(aTeacherDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${teacher.getId()},\n" +
                            "  \"email\": \"${teacher.getEmail()}\",\n" +
                            "  \"firstName\": \"${"G2erman"}\",\n" +
                            "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                            "}"
                )
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a teacher with first name with special character`() {
        var teacher = teacherService.save(aTeacherDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${teacher.getId()},\n" +
                            "  \"email\": \"${teacher.getEmail()}\",\n" +
                            "  \"firstName\": \"${"G@rman"}\",\n" +
                            "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                            "}"
                )
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }


    @Test
    fun `should throw a 400 status when you update a teacher with last name null`() {
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
                            "}"
                )
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a teacher with last name empty`() {
        var teacher = teacherService.save(aTeacherDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${teacher.getId()},\n" +
                            "  \"email\": \"${teacher.getEmail()}\",\n" +
                            "  \"firstName\": \"${teacher.getFirstName()}\",\n" +
                            "  \"lastName\": \"${""}\"\n" +
                            "}"
                )
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a teacher with last name with numbers`() {
        var teacher = teacherService.save(aTeacherDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${teacher.getId()},\n" +
                            "  \"email\": \"${teacher.getEmail()}\",\n" +
                            "  \"firstName\": \"${"Ger"}\",\n" +
                            "  \"lastName\": \"${"G2erman"}\"\n" +
                            "}"
                )
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a teacher with last name with special character`() {
        var teacher = teacherService.save(aTeacherDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${teacher.getId()},\n" +
                            "  \"email\": \"${teacher.getEmail()}\",\n" +
                            "  \"firstName\": \"${"Ger"}\",\n" +
                            "  \"lastName\": \"${"G@rman"}\"\n" +
                            "}"
                )
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }


    @Test
    fun `should throw a 400 status when you update a teacher with mail null`() {
        var teacher = teacherService.save(aTeacherDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${teacher.getId()},\n" +
                            "  \"email\": \"${null}\",\n" +
                            "  \"firstName\": ${teacher.getFirstName()},\n" +
                            "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                            "}"
                )
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a teacher with mail empty`() {
        var teacher = teacherService.save(aTeacherDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${teacher.getId()},\n" +
                            "  \"email\": \"${""}\",\n" +
                            "  \"firstName\": \"${teacher.getFirstName()}\",\n" +
                            "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                            "}"
                )
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a teacher with an invalid email`() {
        var teacher = teacherService.save(aTeacherDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${teacher.getId()},\n" +
                            "  \"email\": \"${"germacom"}\",\n" +
                            "  \"firstName\": \"${teacher.getFirstName()}\",\n" +
                            "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                            "}"
                )
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a teacher with already registered email`() {
        var teacher = teacherService.save(aTeacherDTO().build())
        var teacher2 = teacherService.save(aTeacherDTO().withEmail("hola@gmail.com").build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${teacher2.getId()},\n" +
                            "  \"email\": \"${teacher.getEmail()}\",\n" +
                            "  \"firstName\": \"${teacher2.getFirstName()}\",\n" +
                            "  \"lastName\": \"${teacher2.getLastName()}\"\n" +
                            "}"
                )
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }


    @Test
    fun `should throw a 200 status when you want to delete an existing teacher`() {
        var teacher = teacherService.save(aTeacherDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/teachers").accept(MediaType.APPLICATION_JSON)
                .param("id", teacher.getId().toString())
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status you want to delete a teacher that does not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/teachers").accept(MediaType.APPLICATION_JSON)
                .param("id", 2.toString())
        )
            .andExpect(status().isNotFound)
    }

    @AfterEach
    fun tearDown() {
        teacherService.clearTeachers()
    }
}