package unq.pds.services

import unq.pds.api.dtos.RepositoryDTO
import unq.pds.model.Repository
import java.util.*

interface RepositoryService {
    fun save(repositoryDTO: RepositoryDTO): Repository
    fun update(repository: Repository): Repository
    fun findById(repositoryId: Long): Repository
    fun findByName(name: String): Optional<Repository>
    fun findByAll(): List<Repository>
    fun deleteById(repositoryId: Long)
    fun count(): Int
    fun clearRepositories()
}