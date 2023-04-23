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
import javax.management.InvalidAttributeValueException

@SpringBootTest
class RepositoryServiceTest {

    @Autowired
    lateinit var repositoryService: RepositoryService

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
    fun `should throw an exception if id is null`() {
        var request = aRepositoryDTO().withId(null).build()
        Assertions.assertThrows(RuntimeException::class.java) { repositoryService.save(request) }
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

        repositoryService.save(aRepositoryDTO().withId(4).build())
        val request = aRepositoryDTO().withId(4).withName("Backend").build()
        val thrown: AlreadyRegisteredException? =
            Assertions.assertThrows(AlreadyRegisteredException::class.java) { repositoryService.save(request) }

        Assertions.assertEquals(
            "The repository is already registered",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if a save repository with an existing name is added`() {

        repositoryService.save(aRepositoryDTO().withId(4).build())
        val request = aRepositoryDTO().withId(5).build()
        val thrown: CloneNotSupportedException? =
            Assertions.assertThrows(CloneNotSupportedException::class.java) { repositoryService.save(request) }

        Assertions.assertEquals(
            "The name unq-pds-app-university-api is already registered",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if that updates a repository that does not exist`() {
        val request = aRepository().build()
        val thrown: NoSuchElementException? =
            Assertions.assertThrows(NoSuchElementException::class.java) { repositoryService.update(request) }

        Assertions.assertEquals(
            "Not found the repository with id 5",
            thrown!!.message
        )
    }

    @Test
    fun `should update the repository with a valid name`() {
        val repository = repositoryService.save(aRepositoryDTO().build())
        repository.name = "German project"

        assertDoesNotThrow { repositoryService.update(repository) }

    }


    //
    //TODO UPDATE
    //


    @Test
    fun `should return a repository when searched for by id`() {
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

    //TODO FALTA HACER UN TEST DEL CASO QUE CUANDO NO SE ENCUENTRA EL REPO O EL TOKEN DE GITHUB
}