package unq.pds.services

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.Initializer
import unq.pds.api.dtos.DeployInstanceDTO
import unq.pds.model.builder.DeployInstanceBuilder.Companion.aDeployInstance
import unq.pds.services.builder.BuilderDeployInstanceDTO.Companion.aDeployInstanceDTO

@SpringBootTest
class DeployInstanceServiceTest {

    @Autowired lateinit var deployInstanceService: DeployInstanceService
    @Autowired lateinit var initializer: Initializer

    @BeforeEach
    fun tearDown() {
        initializer.cleanDataBase()
    }

    @Test
    fun `should be create a deploy instance when it has valid credentials`() {
        val deployInstance = deployInstanceService.save(aDeployInstance().build())
        Assertions.assertNotNull(deployInstance.getId())
    }

    @Test
    fun `should recover a deploy instance when it exists`() {
        val deployInstance = deployInstanceService.save(aDeployInstance().build())
        val recoverDeployInstance = deployInstanceService.read(deployInstance.getId()!!)
        Assertions.assertEquals(deployInstance.name, recoverDeployInstance.name)
        Assertions.assertEquals(deployInstance.url, recoverDeployInstance.url)
    }

    @Test
    fun `should throw an exception when trying to recover a deploy instance with an invalid id`() {
        try {
            deployInstanceService.read(-1)
        } catch (e:NoSuchElementException) {
            Assertions.assertEquals("There is no deploy instance with that id", e.message)
        }
    }

    @Test
    fun `should update a deploy instance when it exists`() {
        val deployInstance = deployInstanceService.save(aDeployInstance().build())
        val deployInstanceDTO = DeployInstanceDTO()
        deployInstanceDTO.id = deployInstance.getId()
        deployInstanceDTO.name = "Heroku"
        deployInstanceDTO.url = deployInstance.url
        val updatedDeployInstance = deployInstanceService.update(deployInstanceDTO)
        Assertions.assertEquals(deployInstance.name, updatedDeployInstance.name)
    }

    @Test
    fun `should throw an exception when trying to update a deploy instance without persisting`() {
        try {
            deployInstanceService.update(aDeployInstanceDTO().build())
        } catch (e:NoSuchElementException) {
            Assertions.assertEquals("Deploy instance does not exist", e.message)
        }
    }

    @Test
    fun `should delete a deploy instance when it exists`() {
        val deployInstance = deployInstanceService.save(aDeployInstance().build())
        deployInstanceService.delete(deployInstance.getId()!!)
        Assertions.assertEquals(0, deployInstanceService.count())
    }

    @Test
    fun `should throw an exception when trying to delete a deploy instance with an invalid id`() {
        try {
            deployInstanceService.delete(-1)
        } catch (e:NoSuchElementException) {
            Assertions.assertEquals("There is no deploy instance with that id", e.message)
        }
    }

    @Test
    fun `should recover an empty list of deploy instances when recover all and there is no persistence`() {
        Assertions.assertEquals(0, deployInstanceService.readAll().size)
    }

    @Test
    fun `should recover a list with two deploy instances when recover all and there are exactly two persisted`() {
        deployInstanceService.save(aDeployInstance().build())
        deployInstanceService.save(aDeployInstance().withName("Heroku").build())
        val deployInstances = deployInstanceService.readAll()

        Assertions.assertEquals(2, deployInstances.size)
        Assertions.assertTrue(deployInstances.any { it.name == "Railway" })
        Assertions.assertTrue(deployInstances.any { it.name == "Heroku" })
    }
}