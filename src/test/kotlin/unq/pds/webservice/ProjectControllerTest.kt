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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import unq.pds.Initializer
import unq.pds.model.builder.CommissionBuilder.Companion.aCommission
import unq.pds.model.builder.DeployInstanceBuilder.Companion.aDeployInstance
import unq.pds.model.builder.MatterBuilder.Companion.aMatter
import unq.pds.model.builder.ProjectBuilder.Companion.aProject
import unq.pds.services.*
import unq.pds.services.builder.BuilderAdminDTO.Companion.aAdminDTO
import unq.pds.services.builder.BuilderGroupDTO.Companion.aGroupDTO
import unq.pds.services.builder.BuilderLoginDTO.Companion.aLoginDTO
import unq.pds.services.builder.BuilderProjectDTO.Companion.aProjectDTO
import unq.pds.services.builder.BuilderRepositoryDTO.Companion.aRepositoryDTO
import unq.pds.services.builder.BuilderStudentDTO.Companion.aStudentDTO
import unq.pds.services.builder.BuilderTeacherDTO.Companion.aTeacherDTO

@ExtendWith(SpringExtension::class)
@SpringBootTest
class ProjectControllerTest {
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var projectService: ProjectService

    @Autowired
    lateinit var teacherService: TeacherService

    @Autowired
    lateinit var studentService: StudentService

    @Autowired
    lateinit var adminService: AdminService

    @Autowired
    lateinit var groupService: GroupService

    @Autowired
    lateinit var repositoryService: RepositoryService

    @Autowired
    lateinit var matterService: MatterService

    @Autowired
    lateinit var commissionService: CommissionService

    @Autowired
    lateinit var deployInstanceService: DeployInstanceService

    private val mapper = ObjectMapper()

    @Autowired
    lateinit var initializer: Initializer

    @BeforeEach
    fun setUp() {
        initializer.cleanDataBase()
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }


    @Test
    fun `should throw a 401 status when trying to create a project and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aProjectDTO().build()))
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to create project`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aProjectDTO().withGroupId(-1).build()))
                .header("Authorization", headerStudent())
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to create project`() {
        val header = headerStudent()
        val group = groupService.save(aGroupDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aProjectDTO().withGroupId(group.getId()).build()))
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to create project`() {
        val header = headerTeacher()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val teacher = teacherService.findByEmail("german@gmail.com")
        val student = studentService.save(aStudentDTO().withEmail("lucas@gmail.com").build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("lucas@gmail.com")).build())
        commissionService.addStudent(commission.getId()!!, student.getId()!!)
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        commissionService.addGroup(commission.getId()!!, group.getId()!!)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aProjectDTO().withGroupId(group.getId()).build()))
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to create project`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aProjectDTO().build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 400 status when the project has a null name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aProjectDTO().withName(null).build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the project has a empty name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aProjectDTO().withName("").build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `should throw a 401 status when trying to get a project and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
                .header("Authorization", "")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to get project if exist`() {
        val project = projectService.save(aProject().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects").accept(MediaType.APPLICATION_JSON)
                .param("id", project.getId().toString())
                .header("Authorization", headerStudent())
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to get project if exist`() {
        val project = projectService.save(aProject().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects").accept(MediaType.APPLICATION_JSON)
                .param("id", project.getId().toString())
                .header("Authorization", headerTeacher())
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to get project if exist`() {
        val project = projectService.save(aProject().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects").accept(MediaType.APPLICATION_JSON)
                .param("id", project.getId().toString())
                .header("Authorization", headerAdmin())
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 404 status when a admin does have permissions to get project if not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1")
                .header("Authorization", headerAdmin())
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `should throw a 400 status when a admin trying to get project with id null`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects").accept(MediaType.APPLICATION_JSON)
                .param("id", null)
                .header("Authorization", headerAdmin())
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `should throw a 401 status when trying to update a project and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aProject().build()))
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to update project`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aProjectDTO().withId(-1).build()))
                .header("Authorization", headerTeacher())
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to update project`() {
        val header = headerTeacher()
        matterService.save(aMatter().build())
        val teacher = teacherService.findByEmail("german@gmail.com")
        val student2 = studentService.save(aStudentDTO().withEmail("test@gmail.com").build())
        val commission = commissionService.save(aCommission().build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("test@gmail.com")).build())

        commissionService.addStudent(commission.getId()!!, student2.getId()!!)
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        commissionService.addGroup(commission.getId()!!, group.getId()!!)

        val project = group.projects.elementAt(0)
        project.name = "new name"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(project))
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to update project`() {
        val header = headerStudent()
        matterService.save(aMatter().build())
        val student = studentService.findByEmail("german@gmail.com")
        val student2 = studentService.save(aStudentDTO().withEmail("test@gmail.com").build())
        val commission = commissionService.save(aCommission().build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("german@gmail.com", "test@gmail.com")).build())

        commissionService.addStudent(commission.getId()!!, student.getId()!!)
        commissionService.addStudent(commission.getId()!!, student2.getId()!!)
        commissionService.addGroup(commission.getId()!!, group.getId()!!)

        val project = group.projects.elementAt(0)

        project.name = "new name"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(project))
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to update a project of a group to which he belongs`() {
        val header = headerStudent()
        val project = projectService.save(aProject().build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("german@gmail.com")).build())
        groupService.addProject(group.getId()!!, project.getId()!!)

        project.name = "new name"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(project))
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to update a project`() {
        val project = projectService.save(aProject().build())

        project.name = "new name"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(project))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 404 status when trying to update a project that does not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aProject().build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `should throw a 401 status when trying to delete a project and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/projects").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
                .header("Authorization", "")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to delete project`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/projects").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
                .header("Authorization", headerStudent())
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to delete project`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/projects").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
                .header("Authorization", headerTeacher())
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to delete project`() {
        val project = projectService.save(aProject().build())

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/projects").accept(MediaType.APPLICATION_JSON)
                .param("id", project.getId().toString())
                .header("Authorization", headerAdmin())
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 404 status when a admin does have permissions to delete project if not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/projects").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1")
                .header("Authorization", headerAdmin())
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `should throw a 400 status when a admin trying to delete project with id null`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/projects").accept(MediaType.APPLICATION_JSON)
                .param("id", null)
                .header("Authorization", headerAdmin())
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to get all projects`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects/getAll").accept(MediaType.APPLICATION_JSON)
                .header("Authorization", headerStudent())
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to get all projects`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects/getAll").accept(MediaType.APPLICATION_JSON)
                .header("Authorization", headerTeacher())
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to get all projects`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects/getAll").accept(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 401 status when trying to get all projects and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects/getAll").accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when trying to add a repository to a project and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/projects/addRepository/{projectId}/{repositoryId}",
                "1",
                "1"
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to add a repository to a project`() {
        val project = projectService.save(aProject().build())
        val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/projects/addRepository/{projectId}/{repositoryId}",
                "1",
                repository.id.toString()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerTeacher())
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to add a repository to a project`() {
        val header = headerTeacher()
        matterService.save(aMatter().build())
        val teacher = teacherService.findByEmail("german@gmail.com")
        val student2 = studentService.save(aStudentDTO().withEmail("test@gmail.com").build())
        val commission = commissionService.save(aCommission().build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("test@gmail.com")).build())

        commissionService.addStudent(commission.getId()!!, student2.getId()!!)
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        commissionService.addGroup(commission.getId()!!, group.getId()!!)

        val project = group.projects.elementAt(0)
        val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/projects/addRepository/{projectId}/{repositoryId}",
                project.getId().toString(),
                repository.id.toString()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to add a repository to a project`() {
        val header = headerStudent()
        matterService.save(aMatter().build())
        val student = studentService.findByEmail("german@gmail.com")
        val student2 = studentService.save(aStudentDTO().withEmail("test@gmail.com").build())
        val commission = commissionService.save(aCommission().build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("german@gmail.com", "test@gmail.com")).build())

        commissionService.addStudent(commission.getId()!!, student.getId()!!)
        commissionService.addStudent(commission.getId()!!, student2.getId()!!)
        commissionService.addGroup(commission.getId()!!, group.getId()!!)

        val project = group.projects.elementAt(0)
        val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/projects/addRepository/{projectId}/{repositoryId}",
                project.getId().toString(),
                repository.id.toString()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to add a repository to a project of a group to which he belongs`() {
        val header = headerStudent()
        val project = projectService.save(aProject().build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("german@gmail.com")).build())
        groupService.addProject(group.getId()!!, project.getId()!!)
        val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())

        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/projects/addRepository/{projectId}/{repositoryId}",
                project.getId().toString(),
                repository.id.toString()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to add a repository to a project`() {
        val project = projectService.save(aProject().build())
        studentService.save(aStudentDTO().build())
        val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())

        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/projects/addRepository/{projectId}/{repositoryId}",
                project.getId().toString(),
                repository.id.toString()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 404 status when add a non-existent project`() {
        studentService.save(aStudentDTO().build())
        val project = projectService.save(aProject().build())
        val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())

        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/projects/addRepository/{projectId}/{repositoryId}",
                "-1",
                repository.id.toString()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when add a non-existent repository`() {
        val project = projectService.save(aProject().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/projects/addRepository/{projectId}/{repositoryId}",
                project.getId().toString(),
                "-1"
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `should throw a 400 status when add a repository to a project and it has already been added`() {
        val project = projectService.save(aProject().build())
        studentService.save(aStudentDTO().build())
        val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        projectService.addRepository(project.getId()!!, repository.id)

        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/projects/addRepository/{projectId}/{repositoryId}",
                project.getId().toString(),
                repository.id.toString()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `should throw a 401 status when trying to add a deploy instance to a project and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/projects/addDeployInstance/{projectId}/{deployInstanceId}",
                "1",
                "1"
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to add a deploy instance to a project`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/projects/addDeployInstance/{projectId}/{deployInstanceId}",
                "1",
                "1"
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerTeacher())
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to add a deploy instance to a project`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/projects/addDeployInstance/{projectId}/{deployInstanceId}",
                "1",
                "1"
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerStudent())
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to add a deploy instance to a project`() {
        val header = headerStudent()
        val group = groupService.save(aGroupDTO().withMembers(listOf("german@gmail.com")).build())
        val project = group.projects.elementAt(0)
        val deployInstance = deployInstanceService.save(aDeployInstance().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/projects/addDeployInstance/{projectId}/{deployInstanceId}",
                project.getId().toString(),
                deployInstance.getId().toString()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to add a deploy instance to a project`() {
        val project = projectService.save(aProject().build())
        val deployInstance = deployInstanceService.save(aDeployInstance().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/projects/addDeployInstance/{projectId}/{deployInstanceId}",
                project.getId().toString(),
                deployInstance.getId().toString()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 404 status when trying to add a deploy instance to a non-existent project`() {
        val deployInstance = deployInstanceService.save(aDeployInstance().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/projects/addDeployInstance/{projectId}/{deployInstanceId}",
                "-1",
                deployInstance.getId().toString()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when trying to add a non-existent deploy instance to a project`() {
        val project = projectService.save(aProject().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/projects/addDeployInstance/{projectId}/{deployInstanceId}",
                project.getId().toString(),
                "-1"
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `should throw a 400 status when add a deploy instance to a project and it has already been added`() {
        val project = projectService.save(aProject().build())
        val deployInstance = deployInstanceService.save(aDeployInstance().build())
        projectService.addDeployInstance(project.getId()!!, deployInstance.getId()!!)

        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/projects/addDeployInstance/{projectId}/{deployInstanceId}",
                project.getId().toString(),
                deployInstance.getId().toString()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }


    private fun headerTeacher(): String {
        val teacher = teacherService.save(aTeacherDTO().build())
        val login = aLoginDTO().withEmail(teacher.getEmail()).withPassword("funciona").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)

        val stringToken = response.andReturn().response.contentAsString
        return "Bearer ${stringToken.substring(10, stringToken.length - 2)}"
    }

    private fun headerStudent(): String {
        val student = studentService.save(aStudentDTO().build())
        val login = aLoginDTO().withEmail(student.getEmail()).withPassword("funciona").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)

        val stringToken = response.andReturn().response.contentAsString
        return "Bearer ${stringToken.substring(10, stringToken.length - 2)}"
    }

    private fun headerAdmin(): String {
        val admin = adminService.save(aAdminDTO().build())
        val login = aLoginDTO().withEmail(admin.getEmail()).withPassword("funciona").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)

        val stringToken = response.andReturn().response.contentAsString
        return "Bearer ${stringToken.substring(10, stringToken.length - 2)}"
    }
}