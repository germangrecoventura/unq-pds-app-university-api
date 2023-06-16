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
import unq.pds.model.builder.MatterBuilder.Companion.aMatter
import unq.pds.model.builder.ProjectBuilder.Companion.aProject
import unq.pds.services.*
import unq.pds.services.builder.BuilderAdminDTO.Companion.aAdminDTO
import unq.pds.services.builder.BuilderGroupDTO.Companion.aGroupDTO
import unq.pds.services.builder.BuilderLoginDTO.Companion.aLoginDTO
import unq.pds.services.builder.BuilderRepositoryDTO.Companion.aRepositoryDTO
import unq.pds.services.builder.BuilderStudentDTO.Companion.aStudentDTO
import unq.pds.services.builder.BuilderTeacherDTO.Companion.aTeacherDTO

@ExtendWith(SpringExtension::class)
@SpringBootTest
class RepositoryControllerTest {
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var repositoryService: RepositoryService

    @Autowired
    lateinit var teacherService: TeacherService

    @Autowired
    lateinit var studentService: StudentService

    @Autowired
    lateinit var adminService: AdminService

    @Autowired
    lateinit var projectService: ProjectService

    @Autowired
    lateinit var groupService: GroupService

    @Autowired
    lateinit var matterService: MatterService

    @Autowired
    lateinit var commissionService: CommissionService

    private val mapper = ObjectMapper()

    @Autowired
    lateinit var initializer: Initializer

    @BeforeEach
    fun setUp() {
        initializer.cleanDataBase()
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }


    @Test
    fun `should throw a 401 status when trying to create a repository and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().build()))
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to create repositories`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().build()))
                .header("Authorization", headerStudent())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to create repositories`() {
        val header = headerStudent()
        matterService.save(aMatter().build())
        val student = studentService.findByEmail("german@gmail.com")
        val commission = commissionService.save(aCommission().build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("german@gmail.com")).build())

        commissionService.addStudent(commission.getId()!!, student.getId()!!)
        commissionService.addGroup(commission.getId()!!, group.getId()!!)

        val project = group.projects.elementAt(0)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().withProjectId(project.getId()!!).build()))
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to create repositories`() {
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
        mockMvc.perform(
            MockMvcRequestBuilders.post("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().withProjectId(project.getId()!!).build()))
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to create repositories`() {
        val project = projectService.save(aProject().build())
        mockMvc.perform(
            MockMvcRequestBuilders.post("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().withProjectId(project.getId()!!).build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when the repository has a null name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().withName(null).build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the repository has a empty name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().withName("").build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the repository has a name with special character not valid`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().withName("App##").build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the repository has a null owner`() {
        val project = projectService.save(aProject().withOwnerGithub(null).build())
        mockMvc.perform(
            MockMvcRequestBuilders.post("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().withProjectId(project.getId()!!).build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the repository has a empty owner`() {
        val project = projectService.save(aProject().withOwnerGithub("").build())
        mockMvc.perform(
            MockMvcRequestBuilders.post("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().withProjectId(project.getId()!!).build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when a admin create a repository and it has already exist`() {
        val project = projectService.save(aProject().build())
        repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        mockMvc.perform(
            MockMvcRequestBuilders.post("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().withProjectId(project.getId()!!).build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 401 status when trying to get a repository and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
                .header("Authorization", "")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to get repository if exist`() {
        val project = projectService.save(aProject().build())
        val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", repository.id.toString())
                .header("Authorization", headerStudent())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to get repository if exist`() {
        val project = projectService.save(aProject().build())
        val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", repository.id.toString())
                .header("Authorization", headerTeacher())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to get repository if exist`() {
        val project = projectService.save(aProject().build())
        val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", repository.id.toString())
                .header("Authorization", headerAdmin())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when a admin does have permissions to get repository if not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1")
                .header("Authorization", headerAdmin())
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 400 status when a admin are looking for a repository with id null`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", null)
                .header("Authorization", headerAdmin())
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 401 status when trying to update a repository and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().build()))
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to update repositories`() {
        val project = projectService.save(aProject().build())
        val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        projectService.addRepository(project.getId()!!, repository.id)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().withProjectId(project.getId()!!).build()))
                .header("Authorization", headerStudent())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to update repositories`() {
        val header = headerStudent()
        matterService.save(aMatter().build())
        val student = studentService.findByEmail("german@gmail.com")
        val commission = commissionService.save(aCommission().build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("german@gmail.com")).build())

        commissionService.addStudent(commission.getId()!!, student.getId()!!)
        commissionService.addGroup(commission.getId()!!, group.getId()!!)

        val project = group.projects.elementAt(0)
        val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        projectService.addRepository(project.getId()!!, repository.id)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().withProjectId(project.getId()!!).build()))
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to update repositories`() {
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
        projectService.addRepository(project.getId()!!, repository.id)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().withProjectId(project.getId()!!).build()))
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to update repositories`() {
        val project = projectService.save(aProject().build())
        val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        projectService.addRepository(project.getId()!!, repository.id)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().withProjectId(project.getId()!!).build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when update a repository and it has a null name`() {
        val project = projectService.save(aProject().build())
        repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        aRepositoryDTO().withName(null)
                            .withProjectId(project.getId()!!).build()
                    )
                )
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when update a repository and it has a empty name`() {
        val project = projectService.save(aProject().build())
        repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        aRepositoryDTO().withName("")
                            .withProjectId(project.getId()!!).build()
                    )
                )
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when update a repository and it has a name with special character not valid`() {
        val project = projectService.save(aProject().build())
        repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        aRepositoryDTO().withName("App###")
                            .withProjectId(project.getId()!!).build()
                    )
                )
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when update a repository that does not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 400 status when update a repository and it not found`() {
        val project = projectService.save(aProject().build())
        mockMvc.perform(
            MockMvcRequestBuilders.post("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        aRepositoryDTO().withName("el-repo")
                            .withProjectId(project.getId()!!).build()
                    )
                )
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 401 status when trying to delete a repository and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
                .header("Authorization", "")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to delete repositories`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
                .header("Authorization", headerStudent())
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to delete repositories`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
                .header("Authorization", headerTeacher())
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to delete repositories`() {
        val project = projectService.save(aProject().build())
        val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", repository.id.toString())
                .header("Authorization", headerAdmin())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when a admin does have permissions to delete repository if not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1")
                .header("Authorization", headerAdmin())
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 400 status when trying to delete a repository with id null`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", null)
                .header("Authorization", headerAdmin())
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to get all repositories`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/getAll").accept(MediaType.APPLICATION_JSON)
                .header("Authorization", headerStudent())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to get all repositories`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/getAll").accept(MediaType.APPLICATION_JSON)
                .header("Authorization", headerTeacher())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to get all repositories`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/getAll").accept(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 401 status when trying to get all repositories and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/getAll").accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when querying the page count of commits`() {
        val project = projectService.save(aProject().build())
        val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/lengthPagesPaginatedCommit")
                .accept(MediaType.APPLICATION_JSON)
                .param("name", repository.name)
                .param("size", "0")
                .header("Authorization", headerAdmin())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when querying the page count of issues`() {
        val project = projectService.save(aProject().build())
        val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/lengthPagesPaginatedIssue")
                .accept(MediaType.APPLICATION_JSON)
                .param("name", repository.name)
                .param("size", "0")
                .header("Authorization", headerAdmin())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when querying the page count of pull request`() {
        val project = projectService.save(aProject().build())
        val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/lengthPagesPaginatedPullRequest")
                .accept(MediaType.APPLICATION_JSON)
                .param("name", repository.name)
                .param("size", "0")
                .header("Authorization", headerAdmin())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when querying the page commits`() {
        val project = projectService.save(aProject().build())
        val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/pageCommit")
                .accept(MediaType.APPLICATION_JSON)
                .param("name", repository.name)
                .param("page", "0")
                .param("size", "5")
                .header("Authorization", headerAdmin())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when querying the page issue`() {
        val project = projectService.save(aProject().build())
        val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/pageIssue")
                .accept(MediaType.APPLICATION_JSON)
                .param("name", repository.name)
                .param("page", "0")
                .param("size", "5")
                .header("Authorization", headerAdmin())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when querying the page pull request`() {
        val project = projectService.save(aProject().build())
        val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/pagePullRequest")
                .accept(MediaType.APPLICATION_JSON)
                .param("name", repository.name)
                .param("page", "0")
                .param("size", "5")
                .header("Authorization", headerAdmin())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 401 status when trying to create a repository with null header`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().build()))
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when trying to get a repository with null header`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
                .header("Authorization", "")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when trying to update a repository with null header`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().build()))
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when trying to delete a repository with null header`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
                .header("Authorization", "")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when trying to getAll repository with null header`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/getAll").accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when trying to querying the page count of commits with null header`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/lengthPagesPaginatedCommit")
                .accept(MediaType.APPLICATION_JSON)
                .param("name", "unq-pds-app-university-api")
                .param("size", "0")
                .header("Authorization", "")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when trying to querying the page count of issues with null header`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/lengthPagesPaginatedIssue")
                .accept(MediaType.APPLICATION_JSON)
                .param("name", "unq-pds-app-university-api")
                .param("size", "0")
                .header("Authorization", "")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when trying to querying the page count of pull request with null header`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/lengthPagesPaginatedPullRequest")
                .accept(MediaType.APPLICATION_JSON)
                .param("name", "unq-pds-app-university-api")
                .param("size", "0")
                .header("Authorization", "")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when trying page commit with null header`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/pageCommit").accept(MediaType.APPLICATION_JSON)
                .param("name", "unq-pds-app-university-api")
                .param("page", "0")
                .param("size", "5")
                .header("Authorization", "")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when trying page issue with null header`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/pageIssue").accept(MediaType.APPLICATION_JSON)
                .param("name", "unq-pds-app-university-api")
                .param("page", "0")
                .param("size", "5")
                .header("Authorization", "")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when trying page pull request with null header`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/pagePullRequest").accept(MediaType.APPLICATION_JSON)
                .param("name", "unq-pds-app-university-api")
                .param("page", "0")
                .param("size", "5")
                .header("Authorization", "")
        ).andExpect(status().isUnauthorized)
    }


    private fun headerTeacher(): String {
        val teacher = teacherService.save(aTeacherDTO().build())
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

    private fun headerAdmin(): String {
        val admin = adminService.save(aAdminDTO().build())
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
}