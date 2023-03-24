package unq.pds.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import unq.pds.model.Teacher

interface TeacherDAO : JpaRepository<Teacher, Long> {
    fun findAllById(id: Long?): MutableList<Teacher>?
    override fun deleteById(id: Long?)
}