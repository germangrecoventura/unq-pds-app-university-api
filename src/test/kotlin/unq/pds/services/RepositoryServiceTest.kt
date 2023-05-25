package unq.pds.services

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.Initializer
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
    lateinit var initializer: Initializer

    @BeforeEach
    fun tearDown() {
        initializer.cleanDataBase()
    }

    @Test
    fun `should be create a repository when it has valid credentials`() {
        assertDoesNotThrow { repositoryService.save(aRepositoryDTO().build()) }
    }

    @Test
    fun `should throw an exception if name is null`() {
        var request = aRepositoryDTO().withName(null).build()
        Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.save(request) }
    }

    @Test
    fun `should throw an exception if owner is empty`() {
        var request = aRepositoryDTO().build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.save(request) }

        Assertions.assertEquals(
            "Repository owner cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if name is empty`() {
        var request = aRepositoryDTO().withName("").build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.save(request) }

        Assertions.assertEquals(
            "Repository name cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if name has special character not valid`() {
        var request = aRepositoryDTO().withName("Ap#").build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.save(request) }

        Assertions.assertEquals(
            "The repository name cannot contain special characters except - and _",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if a save repository with an existing ID is added`() {
        val request = aRepositoryDTO().build()
        repositoryService.save(request)
        val thrown: AlreadyRegisteredException? =
            Assertions.assertThrows(AlreadyRegisteredException::class.java) { repositoryService.save(request) }

        Assertions.assertEquals(
            "The repository is already registered",
            thrown!!.message
        )
    }

    @Test
    fun `should throw exception when token is null`() {
        var request = aRepositoryDTO().build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.save(request) }

        Assertions.assertEquals(
            "Repository token cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw exception when token is empty`() {
        var request = aRepositoryDTO().build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.save(request) }

        Assertions.assertEquals(
            "Repository token cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw exception when repository not found`() {
        var request = aRepositoryDTO().withName("joselito").build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.save(request) }

        Assertions.assertEquals(
            "Owner or repository not found",
            thrown!!.message
        )
    }

    @Test
    fun `should throw exception when the user is not authenticated`() {
        var request = aRepositoryDTO().build()
        val thrown: RuntimeException? =
            Assertions.assertThrows(RuntimeException::class.java) { repositoryService.save(request) }

        Assertions.assertEquals(
            "Not authenticated",
            thrown!!.message
        )
    }

    @Test
    fun `should update the repository with a valid name`() {
        repositoryService.save(aRepositoryDTO().build())
        assertDoesNotThrow { repositoryService.update(aRepositoryDTO().build()) }
    }

    @Test
    fun `should throw an exception if that updates a repository and the name is null`() {
        var request = aRepositoryDTO().withName(null).build()
        Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.update(request) }
    }

    @Test
    fun `should throw an exception if that updates a repository and the owner is empty`() {
        var request = aRepositoryDTO().build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.update(request) }

        Assertions.assertEquals(
            "Repository owner cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if that updates a repository and the name is empty`() {
        var request = aRepositoryDTO().withName("").build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.update(request) }

        Assertions.assertEquals(
            "Repository name cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if that updates a repository and the name has special character not valid`() {
        var request = aRepositoryDTO().withName("Ap#").build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.update(request) }

        Assertions.assertEquals(
            "The repository name cannot contain special characters except - and _",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if that updates a repository that does not exist`() {
        val thrown: NoSuchElementException? =
            Assertions.assertThrows(NoSuchElementException::class.java) { repositoryService.update(aRepositoryDTO().build()) }

        Assertions.assertEquals(
            "Repository does not exist",
            thrown!!.message
        )
    }

    @Test
    fun `should throw exception when trying to update a repository and the owner not found`() {
        var request = aRepositoryDTO().build()
        val thrown: NoSuchElementException? =
            Assertions.assertThrows(NoSuchElementException::class.java) { repositoryService.update(request) }

        Assertions.assertEquals(
            "Repository does not exist",
            thrown!!.message
        )
    }

    @Test
    fun `should throw exception when trying to update a repository and it not found`() {
        var request = aRepositoryDTO().withName("joselito").build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.update(request) }

        Assertions.assertEquals(
            "Owner or repository not found",
            thrown!!.message
        )
    }

    @Test
    fun `should return a repository when searched for by id`() {
        var repository = repositoryService.save(aRepositoryDTO().build())
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
        val repository = repositoryService.save(aRepositoryDTO().build())
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
        var repository = repositoryService.save(aRepositoryDTO().build())
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
        repositoryService.save(aRepositoryDTO().build())
        repositoryService.save(aRepositoryDTO().withName("unq-pds-app-university-web").build())
        val repositories = repositoryService.findByAll()

        Assertions.assertEquals(2, repositories.size)
        Assertions.assertTrue(repositories.any { it.name == "unq-pds-app-university-api" })
        Assertions.assertTrue(repositories.any { it.name == "unq-pds-app-university-web" })
    }

    @Test
    fun `should not throw an exception when querying the page count of commits`() {
        repositoryService.save(aRepositoryDTO().build())
        val repository = repositoryService.save(aRepositoryDTO().withName("unq-pds-app-university-web").build())
        assertDoesNotThrow { repositoryService.lengthPagesPaginatedCommit(repository.name, 0) }
    }

    @Test
    fun `should not throw an exception when querying the page count of pull request`() {
        repositoryService.save(aRepositoryDTO().build())
        val repository = repositoryService.save(aRepositoryDTO().withName("unq-pds-app-university-web").build())
        assertDoesNotThrow { repositoryService.lengthPagesPaginatedPullRequest(repository.name, 0) }
    }

    @Test
    fun `should not throw an exception when querying the page count of issue`() {
        repositoryService.save(aRepositoryDTO().build())
        val repository = repositoryService.save(aRepositoryDTO().withName("unq-pds-app-university-web").build())
        assertDoesNotThrow { repositoryService.lengthPagesPaginatedIssue(repository.name, 0) }
    }

    @Test
    fun `should not throw an exception when paging commits is requested`() {
        studentService.save(aStudentDTO().build())
        repositoryService.save(aRepositoryDTO().build())
        val repository = repositoryService.save(aRepositoryDTO().withName("unq-pds-app-university-web").build())
        assertDoesNotThrow { repositoryService.findPaginatedCommit(repository.name, 0, 5) }
    }

    @Test
    fun `should not throw an exception when paging pull request is requested`() {
        studentService.save(aStudentDTO().build())
        repositoryService.save(aRepositoryDTO().build())
        val repository = repositoryService.save(aRepositoryDTO().withName("unq-pds-app-university-web").build())
        assertDoesNotThrow { repositoryService.findPaginatedPullRequest(repository.name, 0, 5) }
    }

    @Test
    fun `should not throw an exception when paging issue is requested`() {
        studentService.save(aStudentDTO().build())
        repositoryService.save(aRepositoryDTO().build())
        val repository = repositoryService.save(aRepositoryDTO().withName("unq-pds-app-university-web").build())
        assertDoesNotThrow { repositoryService.findPaginatedIssue(repository.name, 0, 5) }
    }
}