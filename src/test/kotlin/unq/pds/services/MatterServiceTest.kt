package unq.pds.services

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.model.Matter

@SpringBootTest
class MatterServiceTest {

    @Autowired lateinit var matterService: MatterService

    lateinit var matter: Matter

    @BeforeEach
    fun setUp() {
        matter = Matter("Practica de Desarrollo de Software")
        matterService.save(matter)
    }

    @Test
    fun `recover a matter`() {
        val recoverMatter = matterService.recover(matter.id!!)
        Assertions.assertEquals(matter.name, recoverMatter.name)
    }

    @Test
    fun `exception is thrown when trying to recover a matter with an invalid id`() {
        Assertions.assertThrows(RuntimeException::class.java) { matterService.recover(-1) }
    }

    @Test
    fun `update a matter`() {
        matter.name = "PDeS"
        val updatedMatter = matterService.update(matter)
        Assertions.assertEquals(matter.name, updatedMatter.name)
    }

    @Test
    fun `exception is thrown when trying to update a matter without persisting`() {
        Assertions.assertThrows(RuntimeException::class.java) { matterService.update(Matter("Estrategias de Persistencia")) }
    }

    @Test
    fun `delete a matter`() {
        matterService.delete(matter.id!!)
        Assertions.assertEquals(0, matterService.count())
    }

    @Test
    fun `exception is thrown when trying to delete a matter with an invalid id`() {
        Assertions.assertThrows(RuntimeException::class.java) { matterService.delete(-1) }
    }

    @AfterEach
    fun tearDown() {
        matterService.clearMatters()
    }
}