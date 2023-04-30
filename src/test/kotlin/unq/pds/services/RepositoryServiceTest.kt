package unq.pds.services

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.Initializer
import unq.pds.model.builder.BuilderRepository.Companion.aRepository
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

    private var token: String = "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"

    @Autowired
    lateinit var initializer: Initializer

    @BeforeEach
    fun tearDown() {
        initializer.cleanDataBase()
    }

    @Test
    fun `should be create a repository when it has valid credentials`() {
        studentService.save(aStudentDTO().withTokenGithub(token).build())
        assertDoesNotThrow { repositoryService.save(aRepositoryDTO().build()) }
    }

    @Test
    fun `should throw an exception if name is null`() {
        var request = aRepositoryDTO().withName(null).build()
        Assertions.assertThrows(RuntimeException::class.java) { repositoryService.save(request) }
    }

    @Test
    fun `should throw an exception if name is empty`() {
        var request = aRepositoryDTO().withName("").build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.save(request) }

        Assertions.assertEquals(
            "Name repository cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if name has special character not valid`() {
        var request = aRepositoryDTO().withName("Ap#").build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.save(request) }

        Assertions.assertEquals(
            "The name repository cannot contain special characters except - and _",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if a save repository with an existing ID is added`() {
        studentService.save(aStudentDTO().withTokenGithub(token).build())
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
    fun `should throw an exception if that updates a repository that does not exist`() {
        studentService.save(aStudentDTO().withTokenGithub(token).build())
        val thrown: NoSuchElementException? =
            Assertions.assertThrows(NoSuchElementException::class.java) { repositoryService.update(aRepositoryDTO().build()) }

        Assertions.assertEquals(
            "Repository does not exist",
            thrown!!.message
        )
    }

    @Test
    fun `should update the repository with a valid name`() {
        studentService.save(aStudentDTO().withTokenGithub(token).build())

        repositoryService.save(aRepositoryDTO().build())

        assertDoesNotThrow { repositoryService.update(aRepositoryDTO().build()) }

    }


    @Test
    fun `should return a repository when searched for by id`() {
        studentService.save(aStudentDTO().withTokenGithub(token).build())
        var repository = repositoryService.save(aRepositoryDTO().build())
        var repositoryRecovery = repositoryService.findById(repository.id)

        Assertions.assertTrue(repositoryRecovery.id == repository.id)
    }

    @Test
    fun `should throw an exception if the repository does not exist`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) { repositoryService.findById(-1) }

        Assertions.assertEquals(
            "Not found the repository with id -1",
            thrown.message
        )
    }


    @Test
    fun `should delete a repository if it exists`() {
        studentService.save(aStudentDTO().withTokenGithub(token).build())

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
    fun `should throw exception when repository owner not found`() {
        var request = aRepositoryDTO().build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.save(request) }

        Assertions.assertEquals(
            "The student with owner germangrecoventura is not registered",
            thrown!!.message
        )
    }

    @Test
    fun `should throw exception when repository not found`() {
        studentService.save(aStudentDTO().withTokenGithub(token).build())
        var request = aRepositoryDTO().withName("joselito").build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.save(request) }

        Assertions.assertEquals(
            "Owner or repository not found",
            thrown!!.message
        )
    }

    @Test
    fun `should throw exception when trying to update a repository and the owner not found`() {
        var request = aRepositoryDTO().build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.update(request) }

        Assertions.assertEquals(
            "The student with owner germangrecoventura is not registered",
            thrown!!.message
        )
    }

    @Test
    fun `should throw exception when trying to update a repository and it not found`() {
        studentService.save(aStudentDTO().withTokenGithub(token).build())
        var request = aRepositoryDTO().withName("joselito").build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { repositoryService.update(request) }

        Assertions.assertEquals(
            "Owner or repository not found",
            thrown!!.message
        )
    }
}