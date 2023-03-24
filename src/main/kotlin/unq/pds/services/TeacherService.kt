package unq.pds.services

import unq.pds.model.Teacher

interface TeacherService {
    fun save(teacher: Teacher): Teacher
    fun clearTeachers()
}