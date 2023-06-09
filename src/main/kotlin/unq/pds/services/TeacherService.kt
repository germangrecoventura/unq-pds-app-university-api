package unq.pds.services

import unq.pds.api.dtos.CommentCreateRequestDTO
import unq.pds.api.dtos.TeacherCreateRequestDTO
import unq.pds.model.Comment
import unq.pds.model.Teacher

interface TeacherService {
    fun save(teacherCreateRequestDTO: TeacherCreateRequestDTO): Teacher
    fun update(teacher: Teacher): Teacher
    fun deleteById(id: Long)
    fun count(): Int
    fun findById(id: Long): Teacher
    fun findByEmail(email: String): Teacher
    fun readAll(): List<Teacher>
    fun addCommentToRepository(commentDTO: CommentCreateRequestDTO): Comment
    fun clearTeachers()
}