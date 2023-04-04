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

    @Autowired
    private lateinit var matterDAO: MatterDAO

    override fun save(matter: Matter): Matter {
        if (matterDAO.findByName(matter.name).isPresent) throw RuntimeException("The matter name is already registered")
        return matterDAO.save(matter)
    }

    override fun update(matter: Matter): Matter {
        val matterWithNameRegistered = matterDAO.findByName(matter.name)
        if (matterWithNameRegistered.isPresent && matterWithNameRegistered.get().id != matter.id)
            throw RuntimeException("The matter name is already registered")
        return matterDAO.save(matter)
    }

    override fun recover(matterId: Long): Matter {
        return matterDAO.findById(matterId).orElseThrow { RuntimeException("There is no matter with that id") }
    }

    override fun delete(matterId: Long) {
        if (matterDAO.existsById(matterId)) matterDAO.deleteById(matterId)
        else throw RuntimeException("There is no matter with that id")
    }

    override fun findByName(name: String): Matter {
        return matterDAO.findByName(name).orElseThrow { RuntimeException("There is no matter with that name") }
    }

    override fun count(): Int {
        return matterDAO.count().toInt()
    }

    override fun clearMatters() {
        matterDAO.deleteAll()
    }
}