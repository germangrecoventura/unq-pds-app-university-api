package unq.pds.persistence

import org.springframework.data.jpa.repository.JpaRepository
import unq.pds.model.Student
import java.util.*

interface StudentDAO : JpaRepository<Student, Long> {
    override fun findById(id: Long?): Optional<Student>
    fun findByEmail(email: String): Optional<Student>
}

