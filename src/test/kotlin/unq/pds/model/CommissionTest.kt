package unq.pds.model

import org.junit.jupiter.api.*
import unq.pds.model.builder.CommissionBuilder.Companion.aCommission
import javax.management.InvalidAttributeValueException

class CommissionTest {

    @Test
    fun `should throw an exception when the year of the commission is less than 2000`() {
        try {
            aCommission().withYear(1999).build()
        } catch (e: InvalidAttributeValueException) {
            Assertions.assertEquals("Year should be greater than or equal to 2000", e.message)
        }
    }
}