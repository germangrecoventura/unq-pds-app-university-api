package unq.pds.persistence

import org.springframework.data.jpa.repository.JpaRepository
import unq.pds.model.Repository
import java.util.*

interface RepositoryDAO : JpaRepository<Repository, Long>{
    fun findByName(name: String): Optional<Repository>
}