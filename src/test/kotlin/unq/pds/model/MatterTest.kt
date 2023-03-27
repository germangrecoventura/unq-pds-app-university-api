package unq.pds.model

import org.junit.jupiter.api.*

class MatterTest {

    lateinit var invalidMatter: Matter

    @Test
    fun `matter cannot be created with an empty name`() {
        try {
            invalidMatter = Matter("")
        } catch (e: RuntimeException) {
            Assertions.assertEquals("Name cannot be empty", e.message)
        }
    }
}