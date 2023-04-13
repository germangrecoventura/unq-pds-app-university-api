package unq.pds.services

import unq.pds.model.Commission

interface CommissionService {
    fun save(commission: Commission): Commission
    fun update(commission: Commission): Commission
    fun read(commissionId: Long): Commission
    fun delete(commissionId: Long)
    fun count(): Int
    fun clearCommissions()
}