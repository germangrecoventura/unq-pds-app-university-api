package unq.pds.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.model.Group
import unq.pds.persistence.GroupDAO
import unq.pds.services.GroupService
import unq.pds.services.StudentService

@Service
@Transactional
open class GroupServiceImpl : GroupService {

    @Autowired private lateinit var groupDAO: GroupDAO
    @Autowired private lateinit var studentService: StudentService

    override fun save(group: Group): Group {
        return groupDAO.save(group)
    }

    override fun update(group: Group): Group {
        if (group.getId() != null && groupDAO.existsById(group.getId()!!)) return groupDAO.save(group)
         else throw NoSuchElementException("Group does not exists")
    }

    override fun read(groupId: Long): Group {
        return groupDAO.findById(groupId).orElseThrow { NoSuchElementException("There is no group with that id") }
    }

    override fun delete(groupId: Long) {
        if (groupDAO.existsById(groupId)) groupDAO.deleteById(groupId)
         else throw NoSuchElementException("There is no group with that id")
    }

    override fun addMember(groupId: Long, studentId: Long): Group {
        val group = this.read(groupId)
        val student = studentService.findById(studentId)
        group.addMember(student)

        return this.update(group)
    }

    override fun removeMember(groupId: Long, studentId: Long): Group {
        val group = this.read(groupId)
        val student = studentService.findById(studentId)
        group.removeMember(student)

        return this.update(group)
    }

    override fun readAll(): List<Group> {
        return groupDAO.findAll().toList()
    }

    override fun count(): Int {
        return groupDAO.count().toInt()
    }

    override fun clearGroups() {
        groupDAO.deleteAll()
    }
}