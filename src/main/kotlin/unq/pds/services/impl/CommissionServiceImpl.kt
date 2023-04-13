package unq.pds.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.model.Commission
import unq.pds.persistence.CommissionDAO
import unq.pds.services.CommissionService
import unq.pds.services.MatterService

@Service
@Transactional
open class CommissionServiceImpl : CommissionService {

    @Autowired private lateinit var commissionDAO: CommissionDAO
    @Autowired private lateinit var matterService: MatterService

    override fun save(commission: Commission): Commission {
        matterService.save(commission.getMatter())
        return commissionDAO.save(commission)
    }

    override fun update(commission: Commission): Commission {
        if (commission.id != null && commissionDAO.existsById(commission.id!!)) return commissionDAO.save(commission)
         else throw NoSuchElementException("Commission does not exist")
    }

    override fun read(commissionId: Long): Commission {
        return commissionDAO.findById(commissionId).orElseThrow { NoSuchElementException("There is no commission with that id") }
    }

    override fun delete(commissionId: Long) {
        if (commissionDAO.existsById(commissionId)) commissionDAO.deleteById(commissionId)
         else throw NoSuchElementException("There is no commission with that id")
    }

    override fun count(): Int {
        return commissionDAO.count().toInt()
    }

    override fun clearCommissions() {
        commissionDAO.deleteAll()
    }
}