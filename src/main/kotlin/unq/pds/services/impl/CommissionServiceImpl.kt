package unq.pds.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.model.Commission
import unq.pds.persistence.CommissionDAO
import unq.pds.services.*

@Service
@Transactional
open class CommissionServiceImpl : CommissionService {

    @Autowired private lateinit var commissionDAO: CommissionDAO
    @Autowired private lateinit var matterService: MatterService
    @Autowired private lateinit var studentService: StudentService
    @Autowired private lateinit var teacherService: TeacherService
    @Autowired private lateinit var groupService: GroupService

    override fun save(commission: Commission): Commission {
        commission.setMatter(matterService.findByName(commission.getMatter().name))
        return commissionDAO.save(commission)
    }

    override fun update(commission: Commission): Commission {
        if (commission.getId() != null && commissionDAO.existsById(commission.getId()!!)) return commissionDAO.save(commission)
         else throw NoSuchElementException("Commission does not exist")
    }

    override fun read(commissionId: Long): Commission {
        return commissionDAO.findById(commissionId).orElseThrow { NoSuchElementException("There is no commission with that id") }
    }

    override fun delete(commissionId: Long) {
        if (commissionDAO.existsById(commissionId)) commissionDAO.deleteById(commissionId)
         else throw NoSuchElementException("There is no commission with that id")
    }

    override fun addStudent(commissionId: Long, studentId: Long): Commission {
        val commission = this.read(commissionId)
        val student = studentService.findById(studentId)
        commission.addStudent(student)

        return this.update(commission)
    }

    override fun removeStudent(commissionId: Long, studentId: Long): Commission {
        val commission = this.read(commissionId)
        val student = studentService.findById(studentId)
        commission.removeStudent(student)

        return this.update(commission)
    }

    override fun addTeacher(commissionId: Long, teacherId: Long): Commission {
        val commission = this.read(commissionId)
        val teacher = teacherService.findById(teacherId)
        commission.addTeacher(teacher)

        return this.update(commission)
    }

    override fun removeTeacher(commissionId: Long, teacherId: Long): Commission {
        val commission = this.read(commissionId)
        val teacher = teacherService.findById(teacherId)
        commission.removeTeacher(teacher)

        return this.update(commission)
    }

    override fun addGroup(commissionId: Long, groupId: Long): Commission {
        val commission = this.read(commissionId)
        val group = groupService.read(groupId)
        commission.addGroup(group)

        return this.update(commission)
    }

    override fun removeGroup(commissionId: Long, groupId: Long): Commission {
        val commission = this.read(commissionId)
        val group = groupService.read(groupId)
        commission.removeGroup(group)

        return this.update(commission)
    }

    override fun hasATeacherWithEmail(commissionId: Long, email: String): Boolean {
        return commissionDAO.hasATeacherWithEmail(commissionId, email)
    }

    override fun readAll(): List<Commission> {
        return commissionDAO.findAll().toList()
    }

    override fun count(): Int {
        return commissionDAO.count().toInt()
    }

    override fun clearCommissions() {
        commissionDAO.deleteAll()
    }
}