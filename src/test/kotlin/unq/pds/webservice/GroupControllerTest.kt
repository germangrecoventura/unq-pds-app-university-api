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
import unq.pds.model.builder.GroupBuilder.Companion.aGroup
import unq.pds.model.builder.ProjectBuilder.Companion.aProject
import unq.pds.services.*
import unq.pds.services.builder.BuilderAdminDTO
import unq.pds.services.builder.BuilderGroupDTO.Companion.aGroupDTO
import unq.pds.services.builder.BuilderLoginDTO
import unq.pds.services.builder.BuilderStudentDTO.Companion.aStudentDTO
import unq.pds.services.builder.BuilderTeacherDTO.Companion.aTeacherDTO
import javax.servlet.http.Cookie

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

    @Autowired
    lateinit var teacherService: TeacherService

    @Autowired
    lateinit var adminService: AdminService

    @Autowired
    lateinit var projectService: ProjectService

    @Autowired
    lateinit var initializer: Initializer

    private val mapper = ObjectMapper()

    @BeforeEach
    fun setUp() {
        initializer.cleanDataBase()
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to create group`() {
        val cookie = cookiesStudent()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aGroupDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to create group`() {
        val cookie = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aGroupDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to create group`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aGroupDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when the group has a null name`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aGroupDTO().withName(null).build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the group has a empty name`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aGroupDTO().withName("").build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 200 status when saving groups with the same name`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aGroupDTO().withName("The Pcs").build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aGroupDTO().withName("BugFix").build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }


    // TODO GET

    @Test
    fun `should throw a 200 status when student looking for a group if it exists`() {
        val cookie = cookiesStudent()
        var group = groupService.save(aGroup().build())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", group.getId().toString())
                .cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when teacher looking for a group if it exists`() {
        val cookie = cookiesTeacher()
        var group = groupService.save(aGroup().build())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", group.getId().toString())
                .cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when admin looking for a group if it exists`() {
        val cookie = cookiesAdmin()
        var group = groupService.save(aGroup().build())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", group.getId().toString())
                .cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when the id is null`() {
        val cookie = cookiesAdmin()
        groupService.save(aGroup().build())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", null)
                .cookie(cookie)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when when the id is not empty`() {
        val cookie = cookiesAdmin()

        groupService.save(aGroup().build())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "")
                .cookie(cookie)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when you are looking for a group does not exist`() {
        val cookie = cookiesAdmin()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "2")
                .cookie(cookie)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when the id is not the proper type`() {
        val cookie = cookiesAdmin()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "DOS")
                .cookie(cookie)
        )
            .andExpect(status().isBadRequest)
    }

    // TODO GET

    @Test
    fun `should throw a 401 status when a student does not have permissions to update group except yourself`() {
        val cookie = cookiesStudent()
        val group = groupService.save(aGroup().build())

        group.name = "New name"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(group))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to update group except yourself`() {
        val cookie = cookiesTeacher()
        val group = groupService.save(aGroup().build())

        group.name = "New name"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(group))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to update group `() {
        val cookie = cookiesAdmin()
        val group = groupService.save(aGroup().build())

        group.name = "New name"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(group))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to update group`() {
        val cookie = cookiesStudent()
        val student = studentService.findByEmail("german@gmail.com")
        var group = groupService.save(aGroup().build())
        group.addMember(student)
        groupService.save(group)
        group.name = "New name"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(group))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to delete groups`() {
        val cookie = cookiesStudent()
        val group = groupService.save(aGroup().build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", group.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to delete groups`() {
        val cookie = cookiesTeacher()
        val group = groupService.save(aGroup().build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", group.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a admin does have permissions to delete groups`() {
        val cookie = cookiesAdmin()
        val group = groupService.save(aGroup().build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", group.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when the id is empty`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "")
                .cookie(cookie)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the id is not the proper type`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "ala")
                .cookie(cookie)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status you want to delete a group that does not exist`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "2")
                .cookie(cookie)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to get all students`() {
        val cookie = cookiesStudent()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups/getAll").accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to get all students`() {
        val cookie = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups/getAll").accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to get all students`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups/getAll").accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        )
            .andExpect(status().isOk)
    }


    @Test
    fun `should throw a 401 status when a student does have not permissions to add member to group except yourself`() {
        val cookie = cookiesStudent()
        var group = groupService.save(aGroup().build())
        var student = studentService.save(aStudentDTO().withEmail("prueba@gmail.com").withOwnerGithub("prueba").build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addMember/{groupId}/{studentId}",
                group.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to add member to group except yourself`() {
        val cookie = cookiesStudent()
        var group = groupService.save(aGroup().build())
        val studentFind = studentService.findByEmail("german@gmail.com")
        group.addMember(studentFind)
        groupService.save(group)

        var student = studentService.save(aStudentDTO().withEmail("prueba@gmail.com").withOwnerGithub("prueba").build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addMember/{groupId}/{studentId}",
                group.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }


    @Test
    fun `should throw a 401 status when a teacher does have not permissions to add member to group except yourself`() {
        val cookie = cookiesTeacher()
        var group = groupService.save(aGroup().build())
        var student = studentService.save(aStudentDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addMember/{groupId}/{studentId}",
                group.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to add member to group`() {
        val cookie = cookiesAdmin()
        var group = groupService.save(aGroup().build())
        var student = studentService.save(aStudentDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addMember/{groupId}/{studentId}",
                group.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to add member to group `() {
        val cookie = cookiesAdmin()
        var group = groupService.save(aGroup().build())
        var student = studentService.save(aStudentDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addMember/{groupId}/{studentId}",
                group.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when add a non-existent student`() {
        val cookie = cookiesAdmin()
        var group = groupService.save(aGroup().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addMember/{groupId}/{studentId}",
                group.getId(),
                -5
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 401 status when a student does have not permissions to remove member to group except yourself`() {
        val cookie = cookiesStudent()
        var group = groupService.save(aGroup().build())
        var student = studentService.save(aStudentDTO().withEmail("prueba@gmail.com").withOwnerGithub("prueba").build())
        groupService.addMember(group.getId()!!, student.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/removeMember/{groupId}/{studentId}",
                group.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when admin remove an existing student to a group`() {
        val cookie = cookiesAdmin()
        var group = groupService.save(aGroup().build())
        var student = studentService.save(aStudentDTO().build())
        groupService.addMember(group.getId()!!, student.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/removeMember/{groupId}/{studentId}",
                group.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when remove a non-existent student`() {
        val cookie = cookiesAdmin()
        var group = groupService.save(aGroup().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/removeMember/{groupId}/{studentId}",
                group.getId(),
                -5
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when non-existent group to remove student`() {
        var student = studentService.save(aStudentDTO().build())
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/removeMember/{groupId}/{studentId}",
                -8,
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }


    @Test
    fun `should throw a 401 status when a student does have not permissions to add project to group except yourself`() {
        val cookie = cookiesStudent()
        val project = projectService.save(aProject().build())
        var group = groupService.save(aGroup().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addProject/{groupId}/{projectId}",
                group.getId(),
                project.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to add project to group except yourself`() {
        val cookie = cookiesStudent()
        val project = projectService.save(aProject().build())
        var group = groupService.save(aGroup().build())
        val studentFind = studentService.findByEmail("german@gmail.com")
        groupService.addMember(group.getId()!!, studentFind.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addProject/{groupId}/{projectId}",
                group.getId(),
                project.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }


    @Test
    fun `should throw a 401 status when a teacher does have not permissions to add project to group except yourself`() {
        val cookie = cookiesTeacher()
        val project = projectService.save(aProject().build())
        var group = groupService.save(aGroup().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addProject/{groupId}/{projectId}",
                group.getId(),
                project.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to add project to group`() {
        val cookie = cookiesAdmin()
        val project = projectService.save(aProject().build())
        var group = groupService.save(aGroup().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addProject/{groupId}/{projectId}",
                group.getId(),
                project.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when add a non-existent project`() {
        val cookie = cookiesAdmin()
        var group = groupService.save(aGroup().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addProject/{groupId}/{projectId}",
                group.getId(),
                -5
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    private fun cookiesStudent(): Cookie? {
        val student = studentService.save(aStudentDTO().build())
        val login = BuilderLoginDTO().withEmail(student.getEmail()).withPassword("funciona").withRole("STUDENT").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(status().isOk)

        return response.andReturn().response.cookies[0]
    }

    private fun cookiesTeacher(): Cookie? {
        val teacher = teacherService.save(aTeacherDTO().build())
        val login = BuilderLoginDTO().withEmail(teacher.getEmail()).withPassword("funciona").withRole("TEACHER").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(status().isOk)

        return response.andReturn().response.cookies[0]
    }

    private fun cookiesAdmin(): Cookie? {
        val admin = adminService.save(BuilderAdminDTO.aAdminDTO().build())
        val login = BuilderLoginDTO().withEmail(admin.getEmail()).withPassword("funciona").withRole("ADMIN").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(status().isOk)

        return response.andReturn().response.cookies[0]
    }
}