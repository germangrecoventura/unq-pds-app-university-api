package unq.pds.persistence

import org.springframework.data.jpa.repository.JpaRepository
import unq.pds.model.Admin
import java.util.*

interface AdminDAO : JpaRepository<Admin, Long> {
    fun findByEmail(email: String): Optional<Admin>
}

