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

        return commissionDAO.save(commission)
    }

    override fun removeStudent(commissionId: Long, studentId: Long): Commission {
        val commission = this.read(commissionId)
        val student = studentService.findById(studentId)
        commission.removeStudent(student)

        return commissionDAO.save(commission)
    }

    override fun addTeacher(commissionId: Long, teacherId: Long): Commission {
        val commission = this.read(commissionId)
        val teacher = teacherService.findById(teacherId)
        commission.addTeacher(teacher)

        return commissionDAO.save(commission)
    }

    override fun removeTeacher(commissionId: Long, teacherId: Long): Commission {
        val commission = this.read(commissionId)
        val teacher = teacherService.findById(teacherId)
        commission.removeTeacher(teacher)

        return commissionDAO.save(commission)
    }

    override fun addGroup(commissionId: Long, groupId: Long): Commission {
        val commission = this.read(commissionId)
        val group = groupService.read(groupId)
        commission.addGroup(group)

        return commissionDAO.save(commission)
    }

    override fun removeGroup(commissionId: Long, groupId: Long): Commission {
        val commission = this.read(commissionId)
        val group = groupService.read(groupId)
        commission.removeGroup(group)

        return commissionDAO.save(commission)
    }

    override fun hasATeacherWithEmail(commissionId: Long, email: String): Boolean {
        return commissionDAO.hasATeacherWithEmail(commissionId, email)
    }

    override fun thereIsACommissionWithATeacherWithEmailAndGroupWithId(email: String, groupId: Long): Boolean {
        return commissionDAO.thereIsACommissionWithATeacherWithEmailAndGroupWithId(email, groupId)
    }

    override fun thereIsACommissionWithATeacherWithEmailAndStudentWithId(email: String, studentId: Long): Boolean {
        return commissionDAO.thereIsACommissionWithATeacherWithEmailAndGroupWithId(email, studentId)
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