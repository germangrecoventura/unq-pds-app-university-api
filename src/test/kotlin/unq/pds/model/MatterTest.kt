package unq.pds.model

import org.junit.jupiter.api.*
import unq.pds.model.builder.MatterBuilder.Companion.aMatter

class MatterTest {

    @Test
    fun `should throw an exception when matter name is empty`() {
        try {
            aMatter().withName("").build()
        } catch (e: RuntimeException) {
            Assertions.assertEquals("Name cannot be empty", e.message)
        }
    }

    @Test
    fun `should throw an exception when matter name contain special characters`() {
        try {
            aMatter().withName("M@tematica").build()
        } catch (e: RuntimeException) {
            Assertions.assertEquals("Name cannot contain special characters", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to set an empty matter name`() {
        val matter = aMatter().build()
        try {
            matter.name = ""
        } catch (e: RuntimeException) {
            Assertions.assertEquals("Name cannot be empty", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to set an matter name with some special character`() {
        val matter = aMatter().build()
        try {
            matter.name = "M@tematica"
        } catch (e: RuntimeException) {
            Assertions.assertEquals("Name cannot contain special characters", e.message)
        }
    }
}