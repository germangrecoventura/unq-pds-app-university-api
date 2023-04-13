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
import unq.pds.model.builder.MatterBuilder.Companion.aMatter
import unq.pds.services.MatterService
import unq.pds.services.StudentService
import unq.pds.services.builder.BuilderMatterDTO.Companion.aMatterDTO

@ExtendWith(SpringExtension::class)
@SpringBootTest
class MatterControllerTest {
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var matterService: MatterService

    @Autowired
    lateinit var studentService: StudentService
    private val mapper = ObjectMapper()

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    fun `should throw a 200 status when a valid matter is created`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aMatterDTO().build()))
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when the matter has a null name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aMatterDTO().withName(null).build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }


    @Test
    fun `should throw a 400 status when the matter has a empty name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aMatterDTO().withName("").build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 200 status when you are looking for a matter if it exists`() {
        var group = matterService.save(aMatter().build())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", group.id.toString())
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when the id is null`() {
        matterService.save(aMatter().build())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", null)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the id is empty`() {
        matterService.save(aMatter().build())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", "")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when you are looking for a matter does not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", "2")
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when the id is not the proper type`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", "DOS")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 200 status when you update a matter who exists and with valid credentials`() {
        var matter = matterService.save(aMatter().build())

        matter.name = "Lengua"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(matter))
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when you update a matter that does not exist`() {
        var matter = aMatter().build()
        matter.id = 5
        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(matter))
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 400 status when you update a matter with id null`() {
        var matter = matterService.save(aMatter().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${null},\n" +
                            "  \"name\": \"${matter.name}\",\n" +
                            "}"
                )
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a matter with id empty`() {
        var matter = matterService.save(aMatter().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${""},\n" +
                            "  \"name\": \"${matter.name}\",\n" +
                            "}"
                )
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a matter with name null`() {
        var matter = matterService.save(aMatter().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${matter.id},\n" +
                            "  \"name\": \"${null}\",\n" +
                            "}"
                )
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a matter with name empty`() {
        var matter = matterService.save(aMatter().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${matter.id},\n" +
                            "  \"name\": \"${""}\",\n" +
                            "}"
                )
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }


    @Test
    fun `should throw a 200 status when you want to delete an existing matter`() {
        var group = matterService.save(aMatter().build())

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", group.id.toString())
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when the matter id is empty`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", "")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the id is not the proper type`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", "ala")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status you want to delete a matter that does not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "2")
        )
            .andExpect(status().isNotFound)
    }

    @AfterEach
    fun tearDown() {
        matterService.clearMatters()
    }
}