package unq.pds.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.model.Group
import unq.pds.persistence.GroupDAO
import unq.pds.services.GroupService

@Service
@Transactional
open class GroupServiceImpl : GroupService {

    @Autowired
    private lateinit var groupDAO: GroupDAO

    override fun save(group: Group): Group {
        return groupDAO.save(group)
    }

    override fun update(group: Group): Group {
        if (group.id != null && groupDAO.existsById(group.id!!)) return groupDAO.save(group)
         else throw NoSuchElementException("Group does not exists")
    }

    override fun recover(groupId: Long): Group {
        return groupDAO.findById(groupId).orElseThrow { NoSuchElementException("There is no group with that id") }
    }

    override fun delete(groupId: Long) {
        if (groupDAO.existsById(groupId)) groupDAO.deleteById(groupId)
         else throw NoSuchElementException("There is no group with that id")
    }

    override fun count(): Int {
        return groupDAO.count().toInt()
    }

    override fun clearGroups() {
        groupDAO.deleteAll()
    }
}