package unq.pds.services

import unq.pds.model.Group

interface GroupService {
    fun save(group: Group): Group
    fun update(group: Group): Group
    fun recover(groupId: Long): Group
    fun delete(groupId: Long)
    fun addMember(groupId: Long, studentId: Long): Group
    fun removeMember(groupId: Long, studentId: Long): Group
    fun count(): Int
    fun clearGroups()
}