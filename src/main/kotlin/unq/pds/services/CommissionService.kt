package unq.pds.services

import unq.pds.model.Commission

interface CommissionService {
    fun save(commission: Commission): Commission
    fun read(commissionId: Long): Commission
    fun delete(commissionId: Long)
    fun addStudent(commissionId: Long, studentId: Long): Commission
    fun removeStudent(commissionId: Long, studentId: Long): Commission
    fun addTeacher(commissionId: Long, teacherId: Long): Commission
    fun removeTeacher(commissionId: Long, teacherId: Long): Commission
    fun addGroup(commissionId: Long, groupId: Long): Commission
    fun removeGroup(commissionId: Long, groupId: Long): Commission
    fun hasATeacherWithEmail(commissionId: Long, email: String): Boolean
    fun thereIsACommissionWithATeacherWithEmailAndGroupWithId(email: String, groupId: Long): Boolean
    fun readAll(): List<Commission>
    fun count(): Int
    fun clearCommissions()
}