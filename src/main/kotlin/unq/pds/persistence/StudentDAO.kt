package unq.pds.persistence

import org.springframework.data.jpa.repository.JpaRepository
import unq.pds.model.Student

interface StudentDAO : JpaRepository<Student, Long> {
    fun findAllById(id: Long?): MutableList<Student>?
}
