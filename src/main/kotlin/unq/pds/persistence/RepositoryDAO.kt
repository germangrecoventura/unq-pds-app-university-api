package unq.pds.persistence

import org.springframework.data.jpa.repository.JpaRepository
import unq.pds.github.Repository
import unq.pds.model.Commission
import unq.pds.model.Matter
import java.util.*

interface RepositoryDAO : JpaRepository<Repository, Long>{
    fun findByName(name: String): Optional<Repository>
}