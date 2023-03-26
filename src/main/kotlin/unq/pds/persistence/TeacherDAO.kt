package unq.pds.persistence

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import unq.pds.model.Teacher

@Repository
interface TeacherDAO : CrudRepository<Teacher, Long> {
    fun findAllById(id: Long?): MutableList<Teacher>?
}