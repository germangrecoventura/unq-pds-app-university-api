package unq.pds.services

import unq.pds.api.dtos.StudentCreateRequestDTO
import unq.pds.model.Student

interface StudentService {
    fun save(studentCreateRequestDTO: StudentCreateRequestDTO): Student
    fun update(student: Student): Student
    fun deleteById(id: Long)
    fun count(): Int
    fun findAllById(id: Long?): MutableList<Student>?
    fun clearStudents()
}