package unq.pds.services

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.Initializer
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

    @Autowired
    lateinit var initializer: Initializer

    @BeforeEach
    fun tearDown() {
        initializer.cleanDataBase()
    }

    @Test
    fun `should be create a repository when it has valid credentials`() {
        val project = projectService.save(aProject().build())
        assertDoesNotThrow { repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build()) }
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
    fun `should throw an exception if a save repository with an existing ID is added`() {
        val project = projectService.save(aProject().build())
        val request = aRepositoryDTO().withProjectId(project.getId()!!).build()
        repositoryService.save(request)
        val thrown: AlreadyRegisteredException? =
            Assertions.assertThrows(AlreadyRegisteredException::class.java) { repositoryService.save(request) }

        Assertions.assertEquals(
            "The repository is already registered",
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
    fun `should update the repository with a valid name`() {
        val project = projectService.save(aProject().build())
        repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        assertDoesNotThrow { repositoryService.update(aRepositoryDTO().withProjectId(project.getId()!!).build()) }
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
    fun `should return a repository when searched for by id`() {
        val project = projectService.save(aProject().build())
        var repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        var repositoryRecovery = repositoryService.findById(repository.id)

        Assertions.assertTrue(repositoryRecovery.id == repository.id)
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
    fun `should return a repository when searched for by name`() {
        val project = projectService.save(aProject().build())
        val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        val repositoryRecovered = repositoryService.findByName(repository.name)

        Assertions.assertEquals(repository.id, repositoryRecovered.id)
    }

    @Test
    fun `should throw an exception if the repository with a name does not exist`() {
        val thrown: NoSuchElementException =
            Assertions.assertThrows(NoSuchElementException::class.java) { repositoryService.findByName("un repo") }

        Assertions.assertEquals("Not found the repository with name un repo", thrown.message)
    }

    @Test
    fun `should delete a repository if it exists`() {
        val project = projectService.save(aProject().build())
        var repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        repositoryService.deleteById(repository.id)
        Assertions.assertTrue(repositoryService.count() == 0)
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
    fun `should recover a list with two repositories when recover all and there are exactly two persisted`() {
        val project = projectService.save(aProject().build())
        repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        repositoryService.save(aRepositoryDTO().withName("unq-pds-app-university-web")
            .withProjectId(project.getId()!!).build())
        val repositories = repositoryService.findByAll()

        Assertions.assertEquals(2, repositories.size)
        Assertions.assertTrue(repositories.any { it.name == "unq-pds-app-university-api" })
        Assertions.assertTrue(repositories.any { it.name == "unq-pds-app-university-web" })
    }

    @Test
    fun `should not throw an exception when querying the page count of commits`() {
        val project = projectService.save(aProject().build())
        repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        val repository = repositoryService.save(aRepositoryDTO()
            .withName("unq-pds-app-university-web")
            .withProjectId(project.getId()!!).build())
        assertDoesNotThrow { repositoryService.lengthPagesPaginatedCommit(repository.name, 0) }
    }

    @Test
    fun `should not throw an exception when querying the page count of pull request`() {
        val project = projectService.save(aProject().build())
        repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        val repository = repositoryService.save(aRepositoryDTO()
            .withName("unq-pds-app-university-web")
            .withProjectId(project.getId()!!).build())
        assertDoesNotThrow { repositoryService.lengthPagesPaginatedPullRequest(repository.name, 0) }
    }

    @Test
    fun `should not throw an exception when querying the page count of issue`() {
        val project = projectService.save(aProject().build())
        repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        val repository = repositoryService.save(aRepositoryDTO()
            .withName("unq-pds-app-university-web")
            .withProjectId(project.getId()!!).build())
        assertDoesNotThrow { repositoryService.lengthPagesPaginatedIssue(repository.name, 0) }
    }

    @Test
    fun `should not throw an exception when paging commits is requested`() {
        val project = projectService.save(aProject().build())
        studentService.save(aStudentDTO().build())
        repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        val repository = repositoryService.save(aRepositoryDTO()
            .withName("unq-pds-app-university-web")
            .withProjectId(project.getId()!!).build())
        assertDoesNotThrow { repositoryService.findPaginatedCommit(repository.name, 0, 5) }
    }

    @Test
    fun `should not throw an exception when paging pull request is requested`() {
        val project = projectService.save(aProject().build())
        studentService.save(aStudentDTO().build())
        repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        val repository = repositoryService.save(aRepositoryDTO()
            .withName("unq-pds-app-university-web")
            .withProjectId(project.getId()!!).build())
        assertDoesNotThrow { repositoryService.findPaginatedPullRequest(repository.name, 0, 5) }
    }

    @Test
    fun `should not throw an exception when paging issue is requested`() {
        val project = projectService.save(aProject().build())
        studentService.save(aStudentDTO().build())
        repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        val repository = repositoryService.save(aRepositoryDTO()
            .withName("unq-pds-app-university-web")
            .withProjectId(project.getId()!!).build())
        assertDoesNotThrow { repositoryService.findPaginatedIssue(repository.name, 0, 5) }
    }
}