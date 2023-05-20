package unq.pds.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import unq.pds.model.builder.BuilderRepository.Companion.aRepository
import unq.pds.model.builder.ProjectBuilder.Companion.aProject
import javax.management.InvalidAttributeValueException

class ProjectTest {

    @Test
    fun `should throw an exception when the project name is empty`() {
        try {
            aProject().withName("").build()
        } catch (e: InvalidAttributeValueException) {
            Assertions.assertEquals("Name cannot be empty", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to set an empty project name`() {
        val project = aProject().build()
        try {
            project.name = ""
        } catch (e: InvalidAttributeValueException) {
            Assertions.assertEquals("Name cannot be empty", e.message)
        }
    }

    @Test
    fun `should add a repository when it has not been added previously`() {
        val project = aProject().build()
        Assertions.assertEquals(0, project.repositories.size)
        project.addRepository(aRepository().build())
        Assertions.assertEquals(1, project.repositories.size)
    }

    @Test
    fun `should throw an exception when trying to add the same repository to the project twice`() {
        val project = aProject().build()
        project.addRepository(aRepository().build())
        try {
            project.addRepository(aRepository().build())
        } catch (e: CloneNotSupportedException) {
            Assertions.assertEquals("The repository is already in the project", e.message)
        }
    }
}