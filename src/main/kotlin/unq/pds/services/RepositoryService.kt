package unq.pds.services

import unq.pds.api.dtos.RepositoryDTO
import unq.pds.model.Repository
import java.util.*

interface RepositoryService {
    fun save(repositoryDTO: RepositoryDTO): Repository
    fun update(repositoryDTO: RepositoryDTO): Repository
    fun findById(repositoryId: Long): Repository
    fun findByName(name: String): Repository
    fun findByAll(): List<Repository>
    fun deleteById(repositoryId: Long)
    fun count(): Int
    fun clearRepositories()
}