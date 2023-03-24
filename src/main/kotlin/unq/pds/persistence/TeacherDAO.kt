package unq.pds.persistence

import org.springframework.data.jpa.repository.JpaRepository
import unq.pds.model.Teacher

interface TeacherDAO : JpaRepository<Teacher, Long>