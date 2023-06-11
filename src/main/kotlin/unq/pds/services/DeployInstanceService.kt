package unq.pds.services

import unq.pds.api.dtos.DeployInstanceDTO
import unq.pds.model.DeployInstance

interface DeployInstanceService {
    fun save(deployInstance: DeployInstance): DeployInstance
    fun update(deployInstance: DeployInstanceDTO): DeployInstance
    fun read(deployInstanceId: Long): DeployInstance
    fun delete(deployInstanceId: Long)
    fun readAll(): List<DeployInstance>
    fun count(): Int
    fun clearDeployInstances()
}