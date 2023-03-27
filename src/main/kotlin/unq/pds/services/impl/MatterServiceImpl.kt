package unq.pds.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.model.Matter
import unq.pds.persistence.MatterDAO
import unq.pds.services.MatterService

@Service
@Transactional
open class MatterServiceImpl : MatterService {

    @Autowired private lateinit var matterDAO: MatterDAO

    override fun save(matter: Matter): Matter {
        return matterDAO.save(matter)
    }

    override fun update(matter: Matter): Matter {
        if (matterDAO.existsById(matter.id!!)) return matterDAO.save(matter)
         else throw RuntimeException("Matter does not exist")
    }

    override fun recover(matterId: Long): Matter {
        return matterDAO.findById(matterId).orElseThrow { RuntimeException("There is no matter with that id") }
    }

    override fun delete(matterId: Long) {
        matterDAO.deleteById(matterId)
    }

    override fun clearMatters() {
        matterDAO.deleteAll()
    }
}