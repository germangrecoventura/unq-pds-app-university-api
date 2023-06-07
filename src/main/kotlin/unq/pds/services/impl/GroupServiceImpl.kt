package unq.pds.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.api.dtos.GroupDTO
import unq.pds.api.dtos.GroupUpdateDTO
import unq.pds.model.Group
import unq.pds.model.Project
import unq.pds.model.exceptions.GroupWithEmptyMemberException
import unq.pds.model.exceptions.ProjectAlreadyHasAnOwnerException
import unq.pds.persistence.GroupDAO
import unq.pds.services.GroupService
import unq.pds.services.ProjectService
import unq.pds.services.StudentService
import javax.management.InvalidAttributeValueException

@Service
@Transactional
open class GroupServiceImpl : GroupService {

    @Autowired
    private lateinit var groupDAO: GroupDAO

    @Autowired
    private lateinit var studentService: StudentService

    @Autowired
    private lateinit var projectService: ProjectService

    override fun save(groupDTO: GroupDTO): Group {
        if (groupDTO.members!!.isEmpty()) throw NoSuchElementException("There must be at least one member")
        if (groupDTO.nameProject.isNullOrBlank()) throw InvalidAttributeValueException("Name project cannot be empty")
        if (groupDTO.name.isNullOrBlank()) throw InvalidAttributeValueException("Name group cannot be empty")
        if (groupDTO.members!![0].isNullOrBlank()) throw InvalidAttributeValueException("There must be at least one member")
        val group = groupDTO.fromDTOToModel()
        for (member in groupDTO.members!!) {
            if (!member.isNullOrBlank()) {
                group.addMember(studentService.findByEmail(member))
            }
        }
        val project = Project(groupDTO.nameProject!!, groupDTO.ownerGithub, groupDTO.tokenGithub)
        group.addProject(projectService.save(project))
        return groupDAO.save(group)
    }

    override fun update(groupUpdateDTO: GroupUpdateDTO): Group {
        if (groupUpdateDTO.id != null && groupDAO.existsById(groupUpdateDTO.id!!)) {
            val groupFind = groupDAO.findById(groupUpdateDTO.id!!).get()
            groupFind.name = groupUpdateDTO.name!!
            return groupDAO.save(groupFind)
        } else throw NoSuchElementException("Group does not exist")
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

        return groupDAO.save(group)
    }

    override fun removeMember(groupId: Long, studentId: Long): Group {
        if ((groupDAO.countMembers() - 1) < 1) return throw GroupWithEmptyMemberException()
        val group = this.read(groupId)
        val student = studentService.findById(studentId)
        group.removeMember(student)

        return groupDAO.save(group)
    }

    override fun addProject(groupId: Long, projectId: Long): Group {
        val group = this.read(groupId)
        val project = projectService.read(projectId)
        if (groupDAO.projectOwnerOfTheProject(project).isPresent) {
            throw ProjectAlreadyHasAnOwnerException()
        }
        group.addProject(project)

        return groupDAO.save(group)
    }

    override fun hasAMemberWithEmail(groupId: Long, email: String): Boolean {
        return groupDAO.hasAMemberWithEmail(groupId, email)
    }

    override fun thereIsAGroupWithThisProjectAndThisMember(projectId: Long, studentId: Long): Boolean {
        return groupDAO.thereIsAGroupWithThisProjectAndThisMember(projectId, studentId)
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