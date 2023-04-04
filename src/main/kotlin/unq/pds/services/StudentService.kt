package unq.pds.services

import unq.pds.api.dtos.StudentCreateRequestDTO
import unq.pds.model.Student

interface StudentService {
    fun save(studentCreateRequestDTO: StudentCreateRequestDTO): Student
    fun update(student: Student): Student
    fun deleteById(id: Long)
    fun count(): Int
    fun findById(id: Long): Student
    fun findByEmail(email: String): Student
    fun clearStudents()
}