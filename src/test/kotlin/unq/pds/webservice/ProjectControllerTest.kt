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
import unq.pds.model.builder.GroupBuilder.Companion.aGroup
import unq.pds.model.builder.ProjectBuilder.Companion.aProject
import unq.pds.services.*
import unq.pds.services.builder.BuilderAdminDTO.Companion.aAdminDTO
import unq.pds.services.builder.BuilderLoginDTO.Companion.aLoginDTO
import unq.pds.services.builder.BuilderProjectDTO.Companion.aProjectDTO
import unq.pds.services.builder.BuilderRepositoryDTO.Companion.aRepositoryDTO
import unq.pds.services.builder.BuilderStudentDTO.Companion.aStudentDTO
import unq.pds.services.builder.BuilderTeacherDTO.Companion.aTeacherDTO
import javax.servlet.http.Cookie

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

    private val mapper = ObjectMapper()

    @Autowired
    lateinit var initializer: Initializer

    private var token: String = ""

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
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to create project`() {
        val cookie = cookiesStudent()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aProjectDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to create project`() {
        val cookie = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aProjectDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to create project`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aProjectDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 400 status when the project has a null name`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aProjectDTO().withName(null).build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the project has a empty name`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aProjectDTO().withName("").build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `should throw a 401 status when trying to get a project and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to get project if exist`() {
        val cookie = cookiesStudent()
        val project = projectService.save(aProject().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects").accept(MediaType.APPLICATION_JSON)
                .param("id", project.getId().toString()).cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to get project if exist`() {
        val cookie = cookiesTeacher()
        val project = projectService.save(aProject().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects").accept(MediaType.APPLICATION_JSON)
                .param("id", project.getId().toString()).cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to get project if exist`() {
        val cookie = cookiesAdmin()
        val project = projectService.save(aProject().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects").accept(MediaType.APPLICATION_JSON)
                .param("id", project.getId().toString()).cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 404 status when a admin does have permissions to get project if not exist`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1").cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `should throw a 400 status when a admin trying to get project with id null`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects").accept(MediaType.APPLICATION_JSON)
                .param("id", null).cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `should throw a 401 status when trying to update a project and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aProject().build()))
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to update project`() {
        val cookie = cookiesTeacher()
        val project = projectService.save(aProject().build())

        project.name = "new name"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(project))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to update project`() {
        val cookie = cookiesStudent()
        val project = projectService.save(aProject().build())

        project.name = "new name"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(project))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to update their project`() {
        val cookie = cookiesStudent()
        val project = projectService.save(aProject().build())
        val student = studentService.findByEmail("german@gmail.com")
        studentService.addProject(student.getId()!!, project.getId()!!)

        project.name = "new name"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(project))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to update a project of a group to which he belongs`() {
        val cookie = cookiesStudent()
        val project = projectService.save(aProject().build())
        val student = studentService.findByEmail("german@gmail.com")
        val group = groupService.save(aGroup().build())
        groupService.addMember(group.getId()!!, student.getId()!!)
        groupService.addProject(group.getId()!!, project.getId()!!)

        project.name = "new name"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(project))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to update a project`() {
        val cookie = cookiesAdmin()
        val project = projectService.save(aProject().build())

        project.name = "new name"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(project))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 404 status when trying to update a project that does not exist`() {
        val cookie = cookiesAdmin()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aProject().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `should throw a 401 status when trying to delete a project and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/projects").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to delete project`() {
        val cookie = cookiesStudent()
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/projects").accept(MediaType.APPLICATION_JSON)
                .param("id", "1").cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to delete project`() {
        val cookie = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/projects").accept(MediaType.APPLICATION_JSON)
                .param("id", "1").cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to delete project`() {
        val cookie = cookiesAdmin()
        val project = projectService.save(aProject().build())

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/projects").accept(MediaType.APPLICATION_JSON)
                .param("id", project.getId().toString()).cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 404 status when a admin does have permissions to delete project if not exist`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/projects").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1").cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `should throw a 400 status when a admin trying to delete project with id null`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/projects").accept(MediaType.APPLICATION_JSON)
                .param("id", null).cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to get all projects`() {
        val cookie = cookiesStudent()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects/getAll").accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to get all projects`() {
        val cookie = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects/getAll").accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to get all projects`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects/getAll").accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 401 status when trying to get all projects and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/projects/getAll").accept(MediaType.APPLICATION_JSON)
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
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does have not permissions to add a repository to a project`() {
        val cookie = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/projects/addRepository/{projectId}/{repositoryId}",
                "1",
                "1"
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does have not permissions to add a repository to a project`() {
        val cookie = cookiesStudent()
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/projects/addRepository/{projectId}/{repositoryId}",
                "1",
                "1"
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to add a repository to his project`() {
        val cookie = cookiesStudent()
        val project = projectService.save(aProject().build())
        val student = studentService.findByEmail("german@gmail.com")
        studentService.addProject(student.getId()!!, project.getId()!!)
        val repository = repositoryService.save(aRepositoryDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/projects/addRepository/{projectId}/{repositoryId}",
                project.getId().toString(),
                repository.id.toString()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to add a repository to a project of a group to which he belongs`() {
        val cookie = cookiesStudent()
        val project = projectService.save(aProject().build())
        val student = studentService.findByEmail("german@gmail.com")
        val group = groupService.save(aGroup().build())
        groupService.addMember(group.getId()!!, student.getId()!!)
        groupService.addProject(group.getId()!!, project.getId()!!)
        val repository = repositoryService.save(aRepositoryDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/projects/addRepository/{projectId}/{repositoryId}",
                project.getId().toString(),
                repository.id.toString()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to add a repository to a project`() {
        val cookie = cookiesAdmin()
        val project = projectService.save(aProject().build())
        studentService.save(aStudentDTO().withTokenGithub(token).build())
        val repository = repositoryService.save(aRepositoryDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/projects/addRepository/{projectId}/{repositoryId}",
                project.getId().toString(),
                repository.id.toString()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 404 status when add a non-existent project`() {
        val cookie = cookiesAdmin()
        studentService.save(aStudentDTO().withTokenGithub(token).build())
        val repository = repositoryService.save(aRepositoryDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/projects/addRepository/{projectId}/{repositoryId}",
                "-1",
                repository.id.toString()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when add a non-existent repository`() {
        val cookie = cookiesAdmin()
        val project = projectService.save(aProject().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/projects/addRepository/{projectId}/{repositoryId}",
                project.getId().toString(),
                "-1"
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `should throw a 400 status when add a repository to a project and it has already been added`() {
        val cookie = cookiesAdmin()
        val project = projectService.save(aProject().build())
        studentService.save(aStudentDTO().withTokenGithub(token).build())
        val repository = repositoryService.save(aRepositoryDTO().build())
        projectService.addRepository(project.getId()!!, repository.id)

        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/projects/addRepository/{projectId}/{repositoryId}",
                project.getId().toString(),
                repository.id.toString()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    private fun cookiesTeacher(): Cookie? {
        val teacher = teacherService.save(aTeacherDTO().build())
        val login = aLoginDTO().withEmail(teacher.getEmail()).withPassword("funciona").withRole("TEACHER").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)

        return response.andReturn().response.cookies[0]
    }

    private fun cookiesStudent(): Cookie? {
        val student = studentService.save(aStudentDTO().withTokenGithub(token).build())
        val login = aLoginDTO().withEmail(student.getEmail()).withPassword("funciona").withRole("STUDENT").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)

        return response.andReturn().response.cookies[0]
    }

    private fun cookiesAdmin(): Cookie? {
        val admin = adminService.save(aAdminDTO().build())
        val login = aLoginDTO().withEmail(admin.getEmail()).withPassword("funciona").withRole("ADMIN").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)

        return response.andReturn().response.cookies[0]
    }
}