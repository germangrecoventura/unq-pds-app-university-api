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
import unq.pds.services.AdminService
import unq.pds.services.RepositoryService
import unq.pds.services.StudentService
import unq.pds.services.TeacherService
import unq.pds.services.builder.BuilderAdminDTO.Companion.aAdminDTO
import unq.pds.services.builder.BuilderLoginDTO.Companion.aLoginDTO
import unq.pds.services.builder.BuilderRepositoryDTO.Companion.aRepositoryDTO
import unq.pds.services.builder.BuilderStudentDTO.Companion.aStudentDTO
import unq.pds.services.builder.BuilderTeacherDTO.Companion.aTeacherDTO
import javax.servlet.http.Cookie

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
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to create repositories`() {
        val cookie = cookiesStudent()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to create repositories`() {
        val cookie = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to create repositories`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 400 status when the repository has a null name`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().withName(null).build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the repository has a empty name`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().withName("").build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the repository has a name with special character not valid`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().withName("App##").build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the repository has a null owner`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().withOwner(null).build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the repository has a empty owner`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().withOwner("").build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when a admin create a repository and it has already exist`() {
        val cookie = cookiesAdmin()
        repositoryService.save(aRepositoryDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.post("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }


    @Test
    fun `should throw a 401 status when trying to get a repository and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to get repository if exist`() {
        val cookie = cookiesStudent()
        val repository = repositoryService.save(aRepositoryDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", repository.id.toString()).cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to get repository if exist`() {
        val cookie = cookiesTeacher()
        val repository = repositoryService.save(aRepositoryDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", repository.id.toString()).cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to get repository if exist`() {
        val cookie = cookiesAdmin()
        val repository = repositoryService.save(aRepositoryDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", repository.id.toString()).cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 404 status when a admin does have permissions to get repository if not exist`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1").cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `should throw a 400 status when a admin are looking for a repository with id null`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", null)
                .cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `should throw a 401 status when trying to update a repository and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().build()))
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to update repositories`() {
        val cookie = cookiesStudent()
        repositoryService.save(aRepositoryDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to update repositories`() {
        val cookie = cookiesTeacher()
        repositoryService.save(aRepositoryDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to update repositories`() {
        val cookie = cookiesAdmin()
        repositoryService.save(aRepositoryDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 400 status when update a repository and it has a null name`() {
        val cookie = cookiesAdmin()
        repositoryService.save(aRepositoryDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().withName(null).build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when update a repository and it has a empty name`() {
        val cookie = cookiesAdmin()
        repositoryService.save(aRepositoryDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().withName("").build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when update a repository and it has a name with special character not valid`() {
        val cookie = cookiesAdmin()
        repositoryService.save(aRepositoryDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().withName("App###").build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when update a repository and it has a null owner`() {
        val cookie = cookiesAdmin()
        repositoryService.save(aRepositoryDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().withOwner(null).build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when update a repository and it has a empty owner`() {
        val cookie = cookiesAdmin()
        repositoryService.save(aRepositoryDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().withOwner("").build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when update a repository that does not exist`() {
        val cookie = cookiesAdmin()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
    }


    @Test
    fun `should throw a 400 status when update a repository and it not found`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().withName("el-repo").build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `should throw a 401 status when trying to delete a repository and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to delete repositories`() {
        val cookie = cookiesStudent()
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", "1").cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to delete repositories`() {
        val cookie = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", "1").cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to delete repositories`() {
        val cookie = cookiesAdmin()
        val repository = repositoryService.save(aRepositoryDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", repository.id.toString()).cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 404 status when a admin does have permissions to delete repository if not exist`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1").cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `should throw a 400 status when trying to delete a repository with id null`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", null)
                .cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to get all repositories`() {
        val cookie = cookiesStudent()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/getAll").accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to get all repositories`() {
        val cookie = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/getAll").accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to get all repositories`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/getAll").accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 401 status when trying to get all repositories and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/getAll").accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when querying the page count of commits`() {
        val cookie = cookiesAdmin()
        val repository = repositoryService.save(aRepositoryDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/lengthPagesPaginatedCommit")
                .accept(MediaType.APPLICATION_JSON)
                .param("name", repository.name).param("size", "0")
                .cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when querying the page count of issues`() {
        val cookie = cookiesAdmin()
        val repository = repositoryService.save(aRepositoryDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/lengthPagesPaginatedIssue")
                .accept(MediaType.APPLICATION_JSON)
                .param("name", repository.name).param("size", "0")
                .cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when querying the page count of pull request`() {
        val cookie = cookiesAdmin()
        val repository = repositoryService.save(aRepositoryDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/lengthPagesPaginatedPullRequest")
                .accept(MediaType.APPLICATION_JSON)
                .param("name", repository.name).param("size", "0")
                .cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when querying the page commits`() {
        val cookie = cookiesAdmin()
        val repository = repositoryService.save(aRepositoryDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/pageCommit").accept(MediaType.APPLICATION_JSON)
                .param("name", repository.name).param("page", "0").param("size", "5").cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when querying the page issue`() {
        val cookie = cookiesAdmin()
        val repository = repositoryService.save(aRepositoryDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/pageIssue").accept(MediaType.APPLICATION_JSON)
                .param("name", repository.name).param("page", "0").param("size", "5").cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 200 status when querying the page pull request`() {
        val cookie = cookiesAdmin()
        val repository = repositoryService.save(aRepositoryDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/pagePullRequest").accept(MediaType.APPLICATION_JSON)
                .param("name", repository.name).param("page", "0").param("size", "5").cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should throw a 401 status when trying to create a repository with null cookies`() {
        val cookie = Cookie("jwt", "")
        mockMvc.perform(
            MockMvcRequestBuilders.post("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().build()))
                .accept("application/json")
                .cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when trying to get a repository with null cookies`() {
        cookiesStudent()
        val cookie = Cookie("jwt", "")
        val repository = repositoryService.save(aRepositoryDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", repository.id.toString()).cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when trying to update a repository with null cookies`() {
        cookiesStudent()
        val cookie = Cookie("jwt", "")
        repositoryService.save(aRepositoryDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/repositories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aRepositoryDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when trying to delete a repository with null cookies`() {
        val cookie = Cookie("jwt", "")
        val repository = repositoryService.save(aRepositoryDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/repositories").accept(MediaType.APPLICATION_JSON)
                .param("id", repository.id.toString()).cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when trying to getAll repository with null cookies`() {
        val cookie = Cookie("jwt", "")
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/getAll").accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test

    fun `should throw a 401 status when trying to querying the page count of commits with null cookies`() {
        val cookie = Cookie("jwt", "")
        val repository = repositoryService.save(aRepositoryDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/lengthPagesPaginatedCommit")
                .accept(MediaType.APPLICATION_JSON)
                .param("name", repository.name).param("size", "0")
                .cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when trying to querying the page count of issues with null cookies`() {
        val cookie = Cookie("jwt", "")
        val repository = repositoryService.save(aRepositoryDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/lengthPagesPaginatedIssue")
                .accept(MediaType.APPLICATION_JSON)
                .param("name", repository.name).param("size", "0")
                .cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when trying to querying the page count of pull request with null cookies`() {
        val cookie = Cookie("jwt", "")
        val repository = repositoryService.save(aRepositoryDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/lengthPagesPaginatedPullRequest")
                .accept(MediaType.APPLICATION_JSON)
                .param("name", repository.name).param("size", "0")
                .cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when trying page commit with null cookies`() {
        val cookie = Cookie("jwt", "")
        val repository = repositoryService.save(aRepositoryDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/pageCommit").accept(MediaType.APPLICATION_JSON)
                .param("name", repository.name).param("page", "0").param("size", "5")
                .cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when trying page issue with null cookies`() {
        val cookie = Cookie("jwt", "")
        val repository = repositoryService.save(aRepositoryDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/pageIssue").accept(MediaType.APPLICATION_JSON)
                .param("name", repository.name).param("page", "0").param("size", "5")
                .cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when trying page pull request with null cookies`() {
        val cookie = Cookie("jwt", "")
        val repository = repositoryService.save(aRepositoryDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/repositories/pagePullRequest").accept(MediaType.APPLICATION_JSON)
                .param("name", repository.name).param("page", "0").param("size", "5")
                .cookie(cookie)
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }


    private fun cookiesTeacher(): Cookie? {
        val teacher = teacherService.save(aTeacherDTO().build())
        val login = aLoginDTO().withEmail(teacher.getEmail()).withPassword("funciona").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)

        return response.andReturn().response.cookies[0]
    }

    private fun cookiesStudent(): Cookie? {
        val student = studentService.save(aStudentDTO().build())
        val login = aLoginDTO().withEmail(student.getEmail()).withPassword("funciona").build()
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
        val login = aLoginDTO().withEmail(admin.getEmail()).withPassword("funciona").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk)

        return response.andReturn().response.cookies[0]
    }
}