package unq.pds.services

import unq.pds.api.dtos.GroupDTO
import unq.pds.api.dtos.GroupUpdateDTO
import unq.pds.model.Group

interface GroupService {
    fun save(groupDTO: GroupDTO): Group
    fun update(groupUpdateDTO: GroupUpdateDTO): Group
    fun read(groupId: Long): Group
    fun delete(groupId: Long)
    fun addMember(groupId: Long, studentId: Long): Group
    fun removeMember(groupId: Long, studentId: Long): Group
    fun addProject(groupId: Long, projectId: Long): Group
    fun thereIsAGroupWithThisProjectAndThisMemberWithEmail(projectId: Long, studentEmail: String): Boolean
    fun thereIsAGroupWhereIsStudentAndTheDeployInstanceExists(studentEmail: String, deployInstanceId: Long): Boolean
    fun hasAMemberWithEmail(groupId: Long, email: String): Boolean
    fun readAll(): List<Group>
    fun count(): Int
    fun clearGroups()
}