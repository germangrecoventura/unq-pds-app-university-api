package unq.pds.services

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.model.builder.CommissionBuilder.Companion.aCommission

@SpringBootTest
class CommissionServiceTest {

    @Autowired lateinit var commissionService: CommissionService
    @Autowired lateinit var matterService: MatterService

    @Test
    fun `should be create a commission when it has valid credentials`() {
        val commission = commissionService.save(aCommission().build())
        Assertions.assertNotNull(commission.getId())
    }

    @Test
    fun `should recover a commission when it exists`() {
        val commission = commissionService.save(aCommission().build())
        val recoverCommission = commissionService.read(commission.getId()!!)
        Assertions.assertEquals(commission.getId(), recoverCommission.getId())
        Assertions.assertEquals(commission.getYear(), recoverCommission.getYear())
        Assertions.assertEquals(commission.getFourMonthPeriod(), recoverCommission.getFourMonthPeriod())
        Assertions.assertEquals(commission.getMatter().name, recoverCommission.getMatter().name)
    }

    @Test
    fun `should throw an exception when trying to recover a commission with an invalid id`() {
        try {
            commissionService.read(-1)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no commission with that id", e.message)
        }
    }

    @Test
    fun `should update a commission when it exists`() {
        val commission = commissionService.save(aCommission().build())
        val updatedCommission = commissionService.update(commission)
        Assertions.assertEquals(commission.getYear(), updatedCommission.getYear())
        Assertions.assertEquals(commission.getFourMonthPeriod(), updatedCommission.getFourMonthPeriod())
        Assertions.assertEquals(commission.getMatter().name, updatedCommission.getMatter().name)
    }

    @Test
    fun `should throw an exception when trying to update a commission without persisting`() {
        try {
            commissionService.update(aCommission().build())
        } catch (e:NoSuchElementException) {
            Assertions.assertEquals("Commission does not exist", e.message)
        }
    }

    @Test
    fun `should delete a commission when it exists`() {
        val commission = commissionService.save(aCommission().build())
        commissionService.delete(commission.getId()!!)
        Assertions.assertEquals(0, commissionService.count())
    }

    @Test
    fun `should throw an exception when trying to delete a commission with an invalid id`() {
        try {
            commissionService.delete(-1)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no commission with that id", e.message)
        }
    }

    @AfterEach
    fun tearDown() {
        commissionService.clearCommissions()
        matterService.clearMatters()
    }
}