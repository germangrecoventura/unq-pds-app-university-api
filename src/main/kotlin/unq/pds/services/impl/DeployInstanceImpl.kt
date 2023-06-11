package unq.pds.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.api.dtos.DeployInstanceDTO
import unq.pds.model.DeployInstance
import unq.pds.persistence.DeployInstanceDAO
import unq.pds.services.DeployInstanceService

@Service
@Transactional
open class DeployInstanceImpl : DeployInstanceService {

    @Autowired
    private lateinit var deployInstanceDAO: DeployInstanceDAO

    override fun save(deployInstance: DeployInstance): DeployInstance {
        return deployInstanceDAO.save(deployInstance)
    }

    override fun update(deployInstance: DeployInstanceDTO): DeployInstance {
        if (deployInstance.id != null && deployInstanceDAO.existsById(deployInstance.id!!)) {
            val deployInstanceFind = deployInstanceDAO.findById(deployInstance.id!!).get()
            deployInstanceFind.name = deployInstance.name!!
            deployInstanceFind.url = deployInstance.url!!
            return deployInstanceDAO.save(deployInstanceFind)
        }
        else throw NoSuchElementException("Deploy instance does not exist")
    }

    override fun read(deployInstanceId: Long): DeployInstance {
        return deployInstanceDAO.findById(deployInstanceId).orElseThrow { NoSuchElementException("There is no deploy instance with that id") }
    }

    override fun delete(deployInstanceId: Long) {
        if (deployInstanceDAO.existsById(deployInstanceId)) deployInstanceDAO.deleteById(deployInstanceId)
        else throw NoSuchElementException("There is no deploy instance with that id")
    }

    override fun readAll(): List<DeployInstance> {
        return deployInstanceDAO.findAll().toList()
    }

    override fun count(): Int {
        return deployInstanceDAO.count().toInt()
    }

    override fun clearDeployInstances() {
        deployInstanceDAO.deleteAll()
    }
}