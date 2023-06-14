package unq.pds.services

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.Initializer
import unq.pds.model.builder.MatterBuilder.Companion.aMatter
import unq.pds.model.exceptions.AlreadyRegisteredException

@SpringBootTest
class MatterServiceTest {

    @Autowired lateinit var matterService: MatterService
    @Autowired lateinit var initializer: Initializer

    @BeforeEach
    fun tearDown() {
        initializer.cleanDataBase()
    }

    @Test
    fun `should be create a matter when it has valid credentials`() {
        val matter = matterService.save(aMatter().build())
        Assertions.assertNotNull(matter.getId())
    }

    @Test
    fun `should throw an exception when trying to save a matter with a name registered`() {
        matterService.save(aMatter().build())
        try {
            matterService.save(aMatter().build())
        } catch (e: AlreadyRegisteredException) {
            Assertions.assertEquals("The matter is already registered", e.message)
        }
    }

    @Test
    fun `should recover a matter when it exists`() {
        val matter = matterService.save(aMatter().build())
        val recoverMatter = matterService.read(matter.getId()!!)
        Assertions.assertEquals(matter.name, recoverMatter.name)
    }

    @Test
    fun `should throw an exception when trying to recover a matter with an invalid id`() {
        try {
            matterService.read(-1)
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
            Assertions.assertEquals("Matter does not exist", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to update a matter with a name registered`() {
        matterService.save(aMatter().build())
        val matterToUpdate = matterService.save(aMatter().withName("PDeS").build())
        matterToUpdate.name = "Software development practice"
        try {
            matterService.update(matterToUpdate)
        } catch (e:AlreadyRegisteredException) {
            Assertions.assertEquals("The matter is already registered", e.message)
        }
    }

    @Test
    fun `should delete a matter when it exists`() {
        val matter = matterService.save(aMatter().build())
        matterService.delete(matter.getId()!!)
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
        Assertions.assertEquals(matter.getId(), matterWithName.getId())
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

    @Test
    fun `should recover an empty list of matters when recover all and there is no persistence`() {
        Assertions.assertEquals(0, matterService.readAll().size)
    }

    @Test
    fun `should recover a list with two matters when recover all and there are exactly two persisted`() {
        matterService.save(aMatter().build())
        matterService.save(aMatter().withName("Applications development").build())
        val matters = matterService.readAll()

        Assertions.assertEquals(2, matters.size)
        Assertions.assertTrue(matters.any { it.name == "Software development practice" })
        Assertions.assertTrue(matters.any { it.name == "Applications development" })
    }
}