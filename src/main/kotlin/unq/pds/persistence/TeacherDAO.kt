package unq.pds.persistence

import org.springframework.data.jpa.repository.JpaRepository
import unq.pds.model.Teacher
import java.util.Optional

interface TeacherDAO : JpaRepository<Teacher, Long> {
    fun findByEmail(email: String): Optional<Teacher>

}