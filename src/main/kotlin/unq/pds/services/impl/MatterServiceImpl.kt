package unq.pds.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.model.Matter
import unq.pds.model.exceptions.MatterNameAlreadyRegisteredException
import unq.pds.persistence.MatterDAO
import unq.pds.services.MatterService

@Service
@Transactional
open class MatterServiceImpl : MatterService {

    @Autowired
    private lateinit var matterDAO: MatterDAO

    override fun save(matter: Matter): Matter {
        if (matterDAO.findByName(matter.name).isPresent) throw MatterNameAlreadyRegisteredException()
        return matterDAO.save(matter)
    }

    override fun update(matter: Matter): Matter {
        val matterWithNameRegistered = matterDAO.findByName(matter.name)
        if (matterWithNameRegistered.isPresent && matterWithNameRegistered.get().id != matter.id)
            throw MatterNameAlreadyRegisteredException()
        if (matter.id != null && matterDAO.existsById(matter.id!!)) return matterDAO.save(matter)
         else throw NoSuchElementException("Matter does not exists")
    }

    override fun read(matterId: Long): Matter {
        return matterDAO.findById(matterId).orElseThrow { NoSuchElementException("There is no matter with that id") }
    }

    override fun delete(matterId: Long) {
        if (matterDAO.existsById(matterId)) matterDAO.deleteById(matterId)
         else throw NoSuchElementException("There is no matter with that id")
    }

    override fun findByName(name: String): Matter {
        return matterDAO.findByName(name).orElseThrow { NoSuchElementException("There is no matter with that name") }
    }

    override fun count(): Int {
        return matterDAO.count().toInt()
    }

    override fun clearMatters() {
        matterDAO.deleteAll()
    }
}