package unq.pds.services

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.model.builder.ProjectBuilder.Companion.aProject
import unq.pds.model.exceptions.AlreadyRegisteredException
import unq.pds.services.builder.BuilderRepositoryDTO.Companion.aRepositoryDTO
import unq.pds.services.builder.BuilderStudentDTO.Companion.aStudentDTO
import javax.management.InvalidAttributeValueException

@SpringBootTest
class RepositoryServiceTest {

    @Autowired
    lateinit var repositoryService: RepositoryService

    @Autowired
    lateinit var studentService: StudentService

    @Autowired
    lateinit var projectService: ProjectService

    @Test
    fun `should be create a repository, exception when it has been added, update a repository, recover a repository, find by name, recover two, delete repository`() {
        // CREATE
        val project = projectService.save(aProject().build())
        val request = aRepositoryDTO().withProjectId(project.getId()!!).build()
        val repository = repositoryService.save(request)

        // HAS BEEN ADDED
        val thrown: AlreadyRegisteredException? =
            Assertions.assertThrows(AlreadyRegisteredException::class.java) { repositoryService.save(request) }

        Assertions.assertEquals(
            "The repository is already registered",
            thrown!!.message
        )

        // UPDATE
        assertDoesNotThrow { repositoryService.update(aRepositoryDTO().withProjectId(project.getId()!!).build()) }

        // RECOVER BY ID
        val repositoryRecovery = repositoryService.findById(repository.id)

        Assertions.assertTrue(repositoryRecovery.id == repository.id)

        // FIND BY NAME
        val repositoryRecovered = repositoryService.findByName(repository.name)

        Assertions.assertEquals(repository.id, repositoryRecovered.id)

        // RECOVER TWO
        val repository2 = repositoryService.save(aRepositoryDTO().withName("unq-pds-app-university-web")
            .withProjectId(project.getId()!!).build())
        val repositories = repositoryService.findByAll()

        Assertions.assertEquals(2, repositories.size)
        Assertions.assertTrue(repositories.any { it.name == "unq-pds-app-university-api" })
        Assertions.assertTrue(repositories.any { it.name == "unq-pds-app-university-web" })

        // DELETE
        repositoryService.deleteById(repository.id)
        repositoryService.deleteById(repository2.id)
        Assertions.assertTrue(repositoryService.count() == 0)
    }

    @Test
    fun `should throw an exception if name is null`() {
        val project = projectService.save(aProject().build())
        var request = aRepositoryDTO().withName(null).withProjectId(project.getId()!!).build()
        Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.save(request) }
    }

    @Test
    fun `should throw an exception if owner is empty`() {
        val project = projectService.save(aProject().withOwnerGithub("").build())
        val request = aRepositoryDTO().withProjectId(project.getId()!!).build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.save(request) }

        Assertions.assertEquals(
            "Repository owner cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if name is empty`() {
        val project = projectService.save(aProject().build())
        var request = aRepositoryDTO().withName("").withProjectId(project.getId()!!).build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.save(request) }

        Assertions.assertEquals(
            "Repository name cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if name has special character not valid`() {
        val project = projectService.save(aProject().build())
        var request = aRepositoryDTO().withName("Ap#").withProjectId(project.getId()!!).build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.save(request) }

        Assertions.assertEquals(
            "The repository name cannot contain special characters except - and _",
            thrown!!.message
        )
    }

    @Test
    fun `should throw exception when token is empty`() {
        val project = projectService.save(aProject().withTokenGithub("").build())
        val request = aRepositoryDTO().withProjectId(project.getId()!!).build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.save(request) }

        Assertions.assertEquals(
            "Repository token cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw exception when repository not found`() {
        val project = projectService.save(aProject().build())
        var request = aRepositoryDTO().withName("joselito").withProjectId(project.getId()!!).build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.save(request) }

        Assertions.assertEquals(
            "Owner or repository not found",
            thrown!!.message
        )
    }

    @Test
    fun `should throw exception when the user is not authenticated`() {
        val project = projectService.save(aProject().withTokenGithub("djasdjdas").build())
        val request = aRepositoryDTO().withProjectId(project.getId()!!).build()
        val thrown: RuntimeException? =
            Assertions.assertThrows(RuntimeException::class.java) { repositoryService.save(request) }

        Assertions.assertEquals(
            "Not authenticated",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if that updates a repository and the name is null`() {
        val project = projectService.save(aProject().build())
        var request = aRepositoryDTO().withName(null).withProjectId(project.getId()!!).build()
        Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.update(request) }
    }

    @Test
    fun `should throw an exception if that updates a repository and the owner is empty`() {
        val project = projectService.save(aProject().withOwnerGithub("").build())
        val request = aRepositoryDTO().withProjectId(project.getId()!!).build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.update(request) }

        Assertions.assertEquals(
            "Repository owner cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if that updates a repository and the name is empty`() {
        val project = projectService.save(aProject().build())
        var request = aRepositoryDTO().withName("").withProjectId(project.getId()!!).build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.update(request) }

        Assertions.assertEquals(
            "Repository name cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if that updates a repository and the name has special character not valid`() {
        val project = projectService.save(aProject().build())
        var request = aRepositoryDTO().withName("Ap#").withProjectId(project.getId()!!).build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.update(request) }

        Assertions.assertEquals(
            "The repository name cannot contain special characters except - and _",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if that updates a repository that does not exist`() {
        val project = projectService.save(aProject().build())
        val thrown: NoSuchElementException? =
            Assertions.assertThrows(NoSuchElementException::class.java) { repositoryService.update(aRepositoryDTO()
                .withProjectId(project.getId()!!).build()) }

        Assertions.assertEquals(
            "Repository does not exist",
            thrown!!.message
        )
    }

    @Test
    fun `should throw exception when trying to update a repository and the owner not found`() {
        val project = projectService.save(aProject().build())
        var request = aRepositoryDTO().withProjectId(project.getId()!!).build()
        val thrown: NoSuchElementException? =
            Assertions.assertThrows(NoSuchElementException::class.java) { repositoryService.update(request) }

        Assertions.assertEquals(
            "Repository does not exist",
            thrown!!.message
        )
    }

    @Test
    fun `should throw exception when trying to update a repository and it not found`() {
        val project = projectService.save(aProject().build())
        var request = aRepositoryDTO().withName("joselito").withProjectId(project.getId()!!).build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.update(request) }

        Assertions.assertEquals(
            "Owner or repository not found",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if the repository does not exist`() {
        val thrown: NoSuchElementException =
            Assertions.assertThrows(NoSuchElementException::class.java) { repositoryService.findById(-1) }

        Assertions.assertEquals(
            "Not found the repository with id -1",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception if the repository with a name does not exist`() {
        val thrown: NoSuchElementException =
            Assertions.assertThrows(NoSuchElementException::class.java) { repositoryService.findByName("un repo") }

        Assertions.assertEquals("Not found the repository with name un repo", thrown.message)
    }

    @Test
    fun `should throw an exception when deleting a non-existent repository`() {
        val thrown: NoSuchElementException =
            Assertions.assertThrows(NoSuchElementException::class.java) { repositoryService.deleteById(-1) }

        Assertions.assertEquals(
            "The repository with id -1 is not registered",
            thrown.message
        )
    }

    @Test
    fun `should recover an empty list of repositories when recover all and there is no persistence`() {
        Assertions.assertEquals(0, repositoryService.findByAll().size)
    }

    @Test
    fun `should not throw an exception when querying the page count of commits, pull requests and issues, when paging commits, pull request, and issue is requested`() {
        // PAGE COUNT
        val project = projectService.save(aProject().build())
        val repository = repositoryService.save(aRepositoryDTO()
            .withName("unq-pds-app-university-web")
            .withProjectId(project.getId()!!).build())
        assertDoesNotThrow { repositoryService.lengthPagesPaginatedCommit(repository.name, 0) }
        assertDoesNotThrow { repositoryService.lengthPagesPaginatedPullRequest(repository.name, 0) }
        assertDoesNotThrow { repositoryService.lengthPagesPaginatedIssue(repository.name, 0) }

        // PAGING IS REQUESTED
        assertDoesNotThrow { repositoryService.findPaginatedCommit(repository.name, 0, 5) }
        assertDoesNotThrow { repositoryService.findPaginatedPullRequest(repository.name, 0, 5) }
        assertDoesNotThrow { repositoryService.findPaginatedIssue(repository.name, 0, 5) }
    }

    @AfterEach
    fun tearDown() {
        studentService.clearStudents()
        projectService.clearProjects()
        repositoryService.clearRepositories()
    }
}