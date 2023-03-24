package unq.pds.services

import unq.pds.model.Teacher

interface TeacherService {
    fun save(teacher: Teacher): Teacher
    fun delete(teacher: Teacher)
    fun count(): Int
    fun clearTeachers()
}