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
import unq.pds.api.dtos.GroupUpdateDTO
import unq.pds.model.builder.CommissionBuilder.Companion.aCommission
import unq.pds.model.builder.MatterBuilder.Companion.aMatter
import unq.pds.model.builder.ProjectBuilder.Companion.aProject
import unq.pds.services.*
import unq.pds.services.builder.BuilderAdminDTO.Companion.aAdminDTO
import unq.pds.services.builder.BuilderGroupDTO.Companion.aGroupDTO
import unq.pds.services.builder.BuilderLoginDTO.Companion.aLoginDTO
import unq.pds.services.builder.BuilderStudentDTO.Companion.aStudentDTO
import unq.pds.services.builder.BuilderTeacherDTO.Companion.aTeacherDTO

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

    private val mapper = ObjectMapper()

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }


    @Test
    fun `should throw a 400 status when a student trying to create a group and the student is not a member`() {
        studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())

        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        aGroupDTO().withMembers(listOf("prueba@gmail.com")).build()
                    )
                )
                .header("Authorization", headerStudent())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to create group`() {
        studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())

        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        aGroupDTO().withMembers(listOf("german@gmail.com", "prueba@gmail.com")).build()
                    )
                )
                .header("Authorization", headerStudent())
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to create group`() {
        studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())
        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        aGroupDTO().withMembers(listOf("prueba@gmail.com")).build()
                    )
                )
                .header("Authorization", headerTeacher())
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to create group`() {
        studentService.save(aStudentDTO().withEmail("student@gmail.com").build())

        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        aGroupDTO().withMembers(listOf("student@gmail.com")).build()
                    )
                )
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 401 status when trying to create a group and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        aGroupDTO().build()
                    )
                )
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 400 status when the group has a null name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aGroupDTO().withName(null).build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the group has a empty name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aGroupDTO().withName("").build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 200 status when student looking for a group if it exists`() {
        studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("prueba@gmail.com")).build())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", group.getId().toString())
                .header("Authorization", headerStudent())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when teacher looking for a group if it exists`() {
        studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("prueba@gmail.com")).build())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", group.getId().toString())
                .header("Authorization", headerTeacher())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when admin looking for a group if it exists`() {
        studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("prueba@gmail.com")).build())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", group.getId().toString())
                .header("Authorization", headerAdmin())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 401 status when trying to get a group and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
                .header("Authorization", "")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 400 status when the id is null`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", null)
                .header("Authorization", headerAdmin())
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when trying to get a group and the id is empty`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "")
                .header("Authorization", headerAdmin())
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when you are looking for a group does not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "2")
                .header("Authorization", headerAdmin())
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 400 status when trying to get a group and the id is not the proper type`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "DOS")
                .header("Authorization", headerAdmin())
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 401 status when trying to update a group and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(GroupUpdateDTO()))
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to update group except yourself`() {
        studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("prueba@gmail.com")).build())
        val groupDTO = GroupUpdateDTO()
        groupDTO.id = group.getId()
        groupDTO.name = "New name"

        mockMvc.perform(
            MockMvcRequestBuilders.put("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(groupDTO))
                .header("Authorization", headerStudent())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to update group except yourself`() {
        studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("prueba@gmail.com")).build())
        val groupDTO = GroupUpdateDTO()
        groupDTO.id = group.getId()
        groupDTO.name = "New name"

        mockMvc.perform(
            MockMvcRequestBuilders.put("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(groupDTO))
                .header("Authorization", headerTeacher())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to update group`() {
        studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("prueba@gmail.com")).build())
        val groupDTO = GroupUpdateDTO()
        groupDTO.id = group.getId()
        groupDTO.name = "New name"

        mockMvc.perform(
            MockMvcRequestBuilders.put("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(groupDTO))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to update group`() {
        val header = headerStudent()
        val group = groupService.save(aGroupDTO().withMembers(listOf("german@gmail.com")).build())
        val groupDTO = GroupUpdateDTO()
        groupDTO.id = group.getId()
        groupDTO.name = "New name"

        mockMvc.perform(
            MockMvcRequestBuilders.put("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(groupDTO))
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to update group`() {
        val header = headerTeacher()
        val teacher = teacherService.findByEmail("docente@gmail.com")
        val student = studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("prueba@gmail.com")).build())
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        commissionService.addStudent(commission.getId()!!, student.getId()!!)
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        commissionService.addGroup(commission.getId()!!, group.getId()!!)
        val groupDTO = GroupUpdateDTO()
        groupDTO.id = group.getId()
        groupDTO.name = "New name"

        mockMvc.perform(
            MockMvcRequestBuilders.put("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(groupDTO))
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 401 status when trying to delete a group and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
                .header("Authorization", "")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to delete groups`() {
        val header = headerStudent()
        val group = groupService.save(aGroupDTO().withMembers(listOf("german@gmail.com")).build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", group.getId().toString())
                .header("Authorization", header)
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to delete groups`() {
        studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("prueba@gmail.com")).build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", group.getId().toString())
                .header("Authorization", headerTeacher())
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to delete groups`() {
        studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("prueba@gmail.com")).build())

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", group.getId().toString())
                .header("Authorization", headerAdmin())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when trying to delete a group and the id is empty`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "")
                .header("Authorization", headerAdmin())
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when trying to delete a group and the id is not the proper type`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "ala")
                .header("Authorization", headerAdmin())
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status you want to delete a group that does not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "2")
                .header("Authorization", headerAdmin())
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to get all groups`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups/getAll").accept(MediaType.APPLICATION_JSON)
                .header("Authorization", headerStudent())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to get all groups`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups/getAll").accept(MediaType.APPLICATION_JSON)
                .header("Authorization", headerTeacher())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to get all groups`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups/getAll").accept(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 401 status when trying to get all groups and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups/getAll").accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "")
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
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does have not permissions to add member to group except yourself`() {
        val header = headerStudent()
        val student = studentService.findByEmail("german@gmail.com")
        studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("prueba@gmail.com")).build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addMember/{groupId}/{studentId}",
                group.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to add member to group except yourself`() {
        val header = headerStudent()
        val student2 = studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("german@gmail.com")).build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addMember/{groupId}/{studentId}",
                group.getId(),
                student2.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 401 status when a teacher does have not permissions to add member to group except yourself`() {
        studentService.save(aStudentDTO().build())
        val student2 = studentService.save(aStudentDTO().withEmail("ja@gmail.com").build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("german@gmail.com")).build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addMember/{groupId}/{studentId}",
                group.getId(),
                student2.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerTeacher())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to add member to group except yourself`() {
        val header = headerTeacher()
        val teacher = teacherService.findByEmail("docente@gmail.com")
        val student = studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())
        val student2 = studentService.save(aStudentDTO().withEmail("prueba1@gmail.com").build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("prueba@gmail.com")).build())
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        commissionService.addStudent(commission.getId()!!, student.getId()!!)
        commissionService.addStudent(commission.getId()!!, student2.getId()!!)
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        commissionService.addGroup(commission.getId()!!, group.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addMember/{groupId}/{studentId}",
                group.getId(),
                student2.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to add member to group`() {
        studentService.save(aStudentDTO().build())
        val student2 = studentService.save(aStudentDTO().withEmail("student@gmail.com").build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("german@gmail.com")).build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addMember/{groupId}/{studentId}",
                group.getId(),
                student2.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when add a non-existent group`() {
        val student = studentService.save(aStudentDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addMember/{groupId}/{studentId}",
                -1,
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when add a non-existent student`() {
        studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("german@gmail.com")).build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addMember/{groupId}/{studentId}",
                group.getId(),
                -5
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
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
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does have not permissions to remove member to group except yourself`() {
        val header = headerStudent()
        val student = studentService.findByEmail("german@gmail.com")
        studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("prueba@gmail.com")).build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/removeMember/{groupId}/{studentId}",
                group.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to remove member to group except yourself`() {
        val header = headerStudent()
        val student2 = studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())
        val group = groupService.save(
            aGroupDTO().withMembers(listOf("german@gmail.com", "prueba@gmail.com")).build()
        )
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/removeMember/{groupId}/{studentId}",
                group.getId(),
                student2.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 401 status when a teacher does have not permissions to remove member to group except yourself`() {
        val student = studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("german@gmail.com")).build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/removeMember/{groupId}/{studentId}",
                group.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerTeacher())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to remove member to group except yourself`() {
        val header = headerTeacher()
        val teacher = teacherService.findByEmail("docente@gmail.com")
        val student = studentService.save(aStudentDTO().build())
        val student2 = studentService.save(aStudentDTO().withEmail("test@gmail.com").build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("german@gmail.com","test@gmail.com")).build())
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        commissionService.addStudent(commission.getId()!!, student.getId()!!)
        commissionService.addStudent(commission.getId()!!, student2.getId()!!)
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        commissionService.addGroup(commission.getId()!!, group.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/removeMember/{groupId}/{studentId}",
                group.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when admin remove an existing student to a group`() {
        val student = studentService.save(aStudentDTO().build())
        val student2 = studentService.save(aStudentDTO().withEmail("test@gmail.com").build())
        val group = groupService.save(aGroupDTO().withMembers(listOf(student.getEmail()!!,student2.getEmail()!!)).build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/removeMember/{groupId}/{studentId}",
                group.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when trying to remove the only member of a group`() {
        val student = studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().withMembers(listOf(student.getEmail()!!)).build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/removeMember/{groupId}/{studentId}",
                group.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when remove a non-existent student`() {
        studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("german@gmail.com")).build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/removeMember/{groupId}/{studentId}",
                group.getId(),
                -5
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when non-existent group to remove student`() {
        val student = studentService.save(aStudentDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/removeMember/{groupId}/{studentId}",
                -8,
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
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
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does have not permissions to add project to group except yourself`() {
        val project = projectService.save(aProject().build())
        studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("prueba@gmail.com")).build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addProject/{groupId}/{projectId}",
                group.getId(),
                project.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerStudent())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to add project to group except yourself`() {
        val header = headerStudent()
        val project = projectService.save(aProject().build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("german@gmail.com")).build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addProject/{groupId}/{projectId}",
                group.getId(),
                project.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 401 status when a teacher does have not permissions to add project to group except yourself`() {
        val project = projectService.save(aProject().build())
        studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("german@gmail.com")).build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addProject/{groupId}/{projectId}",
                group.getId(),
                project.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerTeacher())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to add project to group except yourself`() {
        val header = headerTeacher()
        val project = projectService.save(aProject().build())
        val teacher = teacherService.findByEmail("docente@gmail.com")
        val student = studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("german@gmail.com")).build())
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        commissionService.addStudent(commission.getId()!!, student.getId()!!)
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        commissionService.addGroup(commission.getId()!!, group.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addProject/{groupId}/{projectId}",
                group.getId(),
                project.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to add project to group`() {
        val project = projectService.save(aProject().build())
        studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("german@gmail.com")).build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addProject/{groupId}/{projectId}",
                group.getId(),
                project.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when trying to add a project in a non-existent group`() {
        val project = projectService.save(aProject().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addProject/{groupId}/{projectId}",
                -5,
                project.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when add a non-existent project`() {
        studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("german@gmail.com")).build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addProject/{groupId}/{projectId}",
                group.getId(),
                -5
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 401 status when a create group with header empty`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        aGroupDTO().build()
                    )
                )
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when looking for a group with header empty`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "2")
                .header("Authorization", "")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a update group with header empty`() {
        val group = GroupUpdateDTO()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(group))
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a delete groups with header empty`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
                .header("Authorization", "")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when add member to group with header empty`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addMember/{groupId}/{studentId}",
                1,
                1
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when remove member to group with header empty`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/removeMember/{groupId}/{studentId}",
                2,
                -1
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when add project to group with header empty`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/groups/addProject/{groupId}/{projectId}",
                "1",
                "1"
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }


    private fun headerStudent(): String {
        val student = studentService.save(aStudentDTO().build())
        val login = aLoginDTO().withEmail(student.getEmail()).withPassword("funciona").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(status().isOk)

        val stringToken = response.andReturn().response.contentAsString
        return "Bearer ${stringToken.substring(10, stringToken.length - 2)}"
    }

    private fun headerTeacher(): String {
        val teacher = teacherService.save(aTeacherDTO().withEmail("docente@gmail.com").build())
        val login = aLoginDTO().withEmail(teacher.getEmail()).withPassword("funciona").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(status().isOk)

        val stringToken = response.andReturn().response.contentAsString
        return "Bearer ${stringToken.substring(10, stringToken.length - 2)}"
    }

    private fun headerAdmin(): String {
        val admin = adminService.save(aAdminDTO().withEmail("admin@gmail.com").build())
        val login = aLoginDTO().withEmail(admin.getEmail()).withPassword("funciona").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(status().isOk)

        val stringToken = response.andReturn().response.contentAsString
        return "Bearer ${stringToken.substring(10, stringToken.length - 2)}"
    }

    @AfterEach
    fun tearDown() {
        commissionService.clearCommissions()
        groupService.clearGroups()
        studentService.clearStudents()
        teacherService.clearTeachers()
        matterService.clearMatters()
        projectService.clearProjects()
        adminService.clearAdmins()
    }
}