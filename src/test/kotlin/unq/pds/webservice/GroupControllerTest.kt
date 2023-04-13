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
import unq.pds.model.builder.GroupBuilder.Companion.aGroup
import unq.pds.services.GroupService
import unq.pds.services.StudentService
import unq.pds.services.builder.BuilderGroupDTO.Companion.aGroupDTO
import unq.pds.services.builder.BuilderStudentDTO.Companion.aStudentDTO

@ExtendWith(SpringExtension::class)
@SpringBootTest
class GroupControllerTest {
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var groupService: GroupService

    @Autowired
    lateinit var studentService: StudentService
    private val mapper = ObjectMapper()

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    fun `should throw a 200 status when a valid group is created`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aGroupDTO().build()))
                .accept("application/json")
        ).andExpect(status().isOk)
    }


    @Test
    fun `should throw a 400 status when the group has a null name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aGroupDTO().withName(null).build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the group has a empty name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aGroupDTO().withName("").build()))
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 200 status when saving groups with the same name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aGroupDTO().withName("The Pcs").build()))
                .accept("application/json")
        ).andExpect(status().isOk)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aGroupDTO().withName("BugFix").build()))
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when you are looking for a group if it exists`() {
        var group = groupService.save(aGroup().build())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", group.id.toString())
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when the id is null`() {
        groupService.save(aGroup().build())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", null)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when when the id is not empty`() {
        groupService.save(aGroup().build())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when you are looking for a group does not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "2")
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when the id is not the proper type`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "DOS")
        )
            .andExpect(status().isBadRequest)
    }


    @Test
    fun `should throw a 200 status when you update a group who exists and with valid credentials`() {
        var group = groupService.save(aGroup().build())

        group.name = "The Warriors"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(group))
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when you update a group that does not exist`() {
        var group = aGroup().build()
        group.id = 5
        mockMvc.perform(
            MockMvcRequestBuilders.put("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(group))
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }


    @Test
    fun `should throw a 404 status when you update a group with id null`() {
        var group = groupService.save(aGroup().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${null},\n" +
                            "  \"name\": \"${"Warrior"}\",\n" +
                            "  \"members\": ${group.members},\n" +
                            "  \"repository\": \"${group.repository}\"\n" +
                            "}"
                )
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 400 status when you update a group with id empty`() {
        var group = groupService.save(aGroup().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${""},\n" +
                            "  \"name\": \"${"Warrior"}\",\n" +
                            "  \"members\": ${group.members},\n" +
                            "  \"repository\": \"${group.repository}\"\n" +
                            "}"
                )
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a group with name empty`() {
        var group = groupService.save(aGroup().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${group.id},\n" +
                            "  \"name\": \"${""}\",\n" +
                            "  \"members\": \"${group.members}\",\n" +
                            "  \"repository\": \"${group.repository}\"\n" +
                            "}"
                )
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a group with member null`() {
        var group = groupService.save(aGroup().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${group.id},\n" +
                            "  \"name\": \"${"Warriors"}\",\n" +
                            "  \"members\": \"${null}\",\n" +
                            "  \"repository\": \"${group.repository}\"\n" +
                            "}"
                )
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 200 status when you want to delete an existing group`() {
        var group = groupService.save(aGroup().build())

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", group.id.toString())
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when the id is empty`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the id is not the proper type`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "ala")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status you want to delete a group that does not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "2")
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 200 status when add an existing student to a group`() {
        var group = groupService.save(aGroup().build())
        var student = studentService.save(aStudentDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addMember/{groupId}/{studentId}",
                group.id,
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when add a non-existent student`() {
        var group = groupService.save(aGroup().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addMember/{groupId}/{studentId}",
                group.id,
                5
            )
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when non-existent group`() {
        var student = studentService.save(aStudentDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addMember/{groupId}/{studentId}",
                6,
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 200 status when remove an existing student to a group`() {
        var group = groupService.save(aGroup().build())
        var student = studentService.save(aStudentDTO().build())
        groupService.addMember(group.id!!, student.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/removeMember/{groupId}/{studentId}",
                group.id,
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when remove a non-existent student`() {
        var group = groupService.save(aGroup().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/removeMember/{groupId}/{studentId}",
                group.id,
                5
            )
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when non-existent group to remove student`() {
        var student = studentService.save(aStudentDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addMember/{groupId}/{studentId}",
                6,
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @AfterEach
    fun tearDown() {
        groupService.clearGroups()
        studentService.clearStudents()
    }
}