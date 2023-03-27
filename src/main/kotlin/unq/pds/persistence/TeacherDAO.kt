package unq.pds.persistence

import org.springframework.data.jpa.repository.JpaRepository
import unq.pds.model.Teacher
import unq.pds.model.User

interface TeacherDAO : JpaRepository<Teacher, Long> {
    fun findAllById(id: Long?): MutableList<Teacher>?
}