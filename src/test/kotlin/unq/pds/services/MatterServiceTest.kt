package unq.pds.services

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.model.builder.MatterBuilder.Companion.aMatter
import unq.pds.model.exceptions.MatterNameAlreadyRegisteredException

@SpringBootTest
class MatterServiceTest {

    @Autowired lateinit var matterService: MatterService

    @Test
    fun `should be create a matter when it has valid credentials`() {
        val matter = matterService.save(aMatter().build())
        Assertions.assertNotNull(matter.id)
    }

    @Test
    fun `should throw an exception when trying to save a matter with a name registered`() {
        matterService.save(aMatter().build())
        try {
            matterService.save(aMatter().build())
        } catch (e: MatterNameAlreadyRegisteredException) {
            Assertions.assertEquals("The matter name is already registered", e.message)
        }
    }

    @Test
    fun `should recover a matter when it exists`() {
        val matter = matterService.save(aMatter().build())
        val recoverMatter = matterService.recover(matter.id!!)
        Assertions.assertEquals(matter.name, recoverMatter.name)
    }

    @Test
    fun `should throw an exception when trying to recover a matter with an invalid id`() {
        try {
            matterService.recover(-1)
        } catch (e:NoSuchElementException) {
            Assertions.assertEquals("There is no matter with that id", e.message)
        }
    }

    @Test
    fun `should update a matter when it exists`() {
        val matter = matterService.save(aMatter().build())
        matter.name = "PDeS"
        val updatedMatter = matterService.update(matter)
        Assertions.assertEquals(matter.name, updatedMatter.name)
    }

    @Test
    fun `should throw an exception when trying to update a matter without persisting`() {
        try {
            matterService.update(aMatter().build())
        } catch (e:NoSuchElementException) {
            Assertions.assertEquals("Matter does not exists", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to update a matter with a name registered`() {
        matterService.save(aMatter().build())
        val matterToUpdate = matterService.save(aMatter().withName("PDeS").build())
        matterToUpdate.name = "Practica de Desarrollo de Software"
        try {
            matterService.update(matterToUpdate)
        } catch (e:MatterNameAlreadyRegisteredException) {
            Assertions.assertEquals("The matter name is already registered", e.message)
        }
    }

    @Test
    fun `should delete a matter when it exists`() {
        val matter = matterService.save(aMatter().build())
        matterService.delete(matter.id!!)
        Assertions.assertEquals(0, matterService.count())
    }

    @Test
    fun `should throw an exception when trying to delete a matter with an invalid id`() {
        try {
            matterService.delete(-1)
        } catch (e:NoSuchElementException) {
            Assertions.assertEquals("There is no matter with that id", e.message)
        }
    }

    @Test
    fun `should return a matter when searched with a name registered`() {
        val matter = matterService.save(aMatter().build())
        val matterWithName = matterService.findByName(matter.name)
        Assertions.assertEquals(matter.id, matterWithName.id)
        Assertions.assertEquals(matter.name, matterWithName.name)
    }

    @Test
    fun `should throw an exception when trying to return a matter with a name unregistered`() {
        try {
            matterService.findByName("PDeS")
        } catch (e:NoSuchElementException) {
            Assertions.assertEquals("There is no matter with that name", e.message)
        }
    }

    @AfterEach
    fun tearDown() {
        matterService.clearMatters()
    }
}