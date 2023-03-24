package unq.pds.services

import unq.pds.api.dtos.TeacherCreateRequestDTO
import unq.pds.model.Teacher

interface TeacherService {
    fun save(teacherCreateRequestDTO: TeacherCreateRequestDTO): Teacher
    fun update(teacher: Teacher): Teacher
    fun delete(teacher: Teacher)
    fun count(): Int
    fun clearTeachers()
}