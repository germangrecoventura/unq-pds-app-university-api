package unq.pds.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import unq.pds.model.builder.DeployInstanceBuilder.Companion.aDeployInstance
import javax.management.InvalidAttributeValueException

class DeployInstanceTest {

    @Test
    fun `should throw an exception when deploy instance name is empty`() {
        try {
            aDeployInstance().withName("").build()
        } catch (e: InvalidAttributeValueException) {
            Assertions.assertEquals("Name cannot be empty", e.message)
        }
    }

    @Test
    fun `should throw an exception when deploy instance name contain special characters`() {
        try {
            aDeployInstance().withName("R#|lway$").build()
        } catch (e: InvalidAttributeValueException) {
            Assertions.assertEquals("Name cannot contain special characters", e.message)
        }
    }

    @Test
    fun `should throw an exception when deploy instance url is empty`() {
        try {
            aDeployInstance().withUrl("").build()
        } catch (e: InvalidAttributeValueException) {
            Assertions.assertEquals("Url cannot be empty", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to set an empty deploy instance name`() {
        val deployInstance = aDeployInstance().build()
        try {
            deployInstance.name = ""
        } catch (e: InvalidAttributeValueException) {
            Assertions.assertEquals("Name cannot be empty", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to set an deploy instance name with some special character`() {
        val deployInstance = aDeployInstance().build()
        try {
            deployInstance.name = "R#|lway$"
        } catch (e: InvalidAttributeValueException) {
            Assertions.assertEquals("Name cannot contain special characters", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to set an empty deploy instance url`() {
        val deployInstance = aDeployInstance().build()
        try {
            deployInstance.url = ""
        } catch (e: InvalidAttributeValueException) {
            Assertions.assertEquals("Url cannot be empty", e.message)
        }
    }

    @Test
    fun `should add a comment in a deploy instance correctly`() {
        val deployInstance = aDeployInstance().build()
        Assertions.assertEquals(0, deployInstance.comments.size)
        deployInstance.addComment(Comment("A deploy instance"))
        Assertions.assertEquals(1, deployInstance.comments.size)
    }

}