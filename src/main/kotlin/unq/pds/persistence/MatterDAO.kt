package unq.pds.persistence

import org.springframework.data.jpa.repository.JpaRepository
import unq.pds.model.Matter
import java.util.Optional

interface MatterDAO : JpaRepository<Matter, Long> {
    fun findByName(name: String): Optional<Matter>
}