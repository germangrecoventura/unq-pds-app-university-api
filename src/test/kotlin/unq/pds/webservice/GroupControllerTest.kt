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
import unq.pds.model.builder.CommissionBuilder.Companion.aCommission
import unq.pds.model.builder.GroupBuilder.Companion.aGroup
import unq.pds.model.builder.MatterBuilder.Companion.aMatter
import unq.pds.model.builder.ProjectBuilder.Companion.aProject
import unq.pds.services.*
import unq.pds.services.builder.BuilderAdminDTO.Companion.aAdminDTO
import unq.pds.services.builder.BuilderGroupDTO.Companion.aGroupDTO
import unq.pds.services.builder.BuilderLoginDTO.Companion.aLoginDTO
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
    lateinit var matterService: MatterService

    @Autowired
    lateinit var commissionService: CommissionService

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
    fun `should throw a 401 status when trying to create a group and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aGroupDTO().build()))
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
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
    fun `should throw a 401 status when trying to get a group and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
        ).andExpect(status().isUnauthorized)
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
    fun `should throw a 400 status when trying to get a group and the id is not the proper type`() {
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
    fun `should throw a 401 status when trying to update a group and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aGroup().build()))
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

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
    fun `should throw a 200 status when a admin does have permissions to update group`() {
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
        val group = groupService.save(aGroup().build())
        val groupUpdated = groupService.addMember(group.getId()!!, student.getId()!!)
        groupUpdated.name = "New name"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(groupUpdated))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to update group`() {
        val cookie = cookiesTeacher()
        val teacher = teacherService.findByEmail("docente@gmail.com")
        val group = groupService.save(aGroup().build())
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        commissionService.addGroup(commission.getId()!!, group.getId()!!)
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
    fun `should throw a 401 status when trying to delete a group and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
        ).andExpect(status().isUnauthorized)
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
    fun `should throw a 200 status when a admin does have permissions to delete groups`() {
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
    fun `should throw a 400 status when trying to delete a group and the id is not the proper type`() {
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
    fun `should throw a 200 status when a student does have permissions to get all groups`() {
        val cookie = cookiesStudent()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups/getAll").accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to get all groups`() {
        val cookie = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups/getAll").accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to get all groups`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups/getAll").accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 401 status when trying to get all groups and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups/getAll").accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when trying to add member to a group and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addMember/{groupId}/{studentId}",
                1, 1
            )
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
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
        groupService.addMember(group.getId()!!, studentFind.getId()!!)

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
    fun `should throw a 200 status when a teacher does have permissions to add member to group except yourself`() {
        val cookie = cookiesTeacher()
        val teacher = teacherService.findByEmail("docente@gmail.com")
        val group = groupService.save(aGroup().build())
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        commissionService.addGroup(commission.getId()!!, group.getId()!!)
        val student = studentService.save(aStudentDTO().build())
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
    fun `should throw a 404 status when add a non-existent group`() {
        val cookie = cookiesAdmin()
        var student = studentService.save(aStudentDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addMember/{groupId}/{studentId}",
                -1,
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isNotFound)
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
    fun `should throw a 401 status when trying to remove member of a group and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/removeMember/{groupId}/{studentId}",
                1, 1
            )
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
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
    fun `should throw a 200 status when a student does have permissions to remove member to group except yourself`() {
        val cookie = cookiesStudent()
        val group = groupService.save(aGroup().build())
        val student = studentService.findByEmail("german@gmail.com")
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
    fun `should throw a 401 status when a teacher does have not permissions to remove member to group except yourself`() {
        val cookie = cookiesTeacher()
        val group = groupService.save(aGroup().build())
        val student = studentService.save(aStudentDTO().build())
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
    fun `should throw a 200 status when a teacher does have permissions to remove member to group except yourself`() {
        val cookie = cookiesTeacher()
        val teacher = teacherService.findByEmail("docente@gmail.com")
        val group = groupService.save(aGroup().build())
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        commissionService.addGroup(commission.getId()!!, group.getId()!!)
        val student = studentService.save(aStudentDTO().build())
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
    fun `should throw a 401 status when trying to add project to a group and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addProject/{groupId}/{projectId}",
                1, 1
            )
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
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
    fun `should throw a 200 status when a teacher does have permissions to add project to group except yourself`() {
        val cookie = cookiesTeacher()
        val project = projectService.save(aProject().build())
        val teacher = teacherService.findByEmail("docente@gmail.com")
        val group = groupService.save(aGroup().build())
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        commissionService.addGroup(commission.getId()!!, group.getId()!!)
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
    fun `should throw a 404 status when trying to add a project in a non-existent group`() {
        val cookie = cookiesAdmin()
        val project = projectService.save(aProject().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addProject/{groupId}/{projectId}",
                -5,
                project.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isNotFound)
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


    @Test
    fun `should throw a 401 status when a create group with cookie empty`() {
        val cookie = Cookie("jwt", "")
        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aGroupDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when looking for a group with cookies empty`() {
        val cookie = Cookie("jwt", "")
        var group = groupService.save(aGroup().build())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", group.getId().toString())
                .cookie(cookie)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a update group with cookie empty`() {
        val cookie = Cookie("jwt", "")
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
    fun `should throw a 401 status when a delete groups with cookie empty`() {
        val cookie = Cookie("jwt", "")
        val group = groupService.save(aGroup().build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", group.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when add member to group with cookie empty`() {
        cookiesStudent()
        val cookie = Cookie("jwt", "")
        var group = groupService.save(aGroup().build())
        val studentFind = studentService.findByEmail("german@gmail.com")
        groupService.addMember(group.getId()!!, studentFind.getId()!!)

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
    fun `should throw a 401 status when remove member to group with cookie empty`() {
        cookiesStudent()
        val cookie = Cookie("jwt", "")
        val group = groupService.save(aGroup().build())
        val student = studentService.findByEmail("german@gmail.com")
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
    fun `should throw a 401 status when add project to group with cookie empty`() {
        cookiesTeacher()
        val cookie = Cookie("jwt", "")
        val project = projectService.save(aProject().build())
        val teacher = teacherService.findByEmail("docente@gmail.com")
        val group = groupService.save(aGroup().build())
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        commissionService.addGroup(commission.getId()!!, group.getId()!!)
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

    private fun cookiesTeacher(): Cookie? {
        val teacher = teacherService.save(aTeacherDTO().withEmail("docente@gmail.com").build())
        val login = aLoginDTO().withEmail(teacher.getEmail()).withPassword("funciona").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(status().isOk)

        return response.andReturn().response.cookies[0]
    }

    private fun cookiesAdmin(): Cookie? {
        val admin = adminService.save(aAdminDTO().withEmail("prueba@gmail.com").build())
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