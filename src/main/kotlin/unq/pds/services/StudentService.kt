package unq.pds.services

import unq.pds.api.dtos.StudentCreateRequestDTO
import unq.pds.model.Student

interface StudentService {
    fun save(studentCreateRequestDTO: StudentCreateRequestDTO): Student
    fun update(student: StudentCreateRequestDTO): Student
    fun deleteById(id: Long)
    fun count(): Int
    fun findById(id: Long): Student
    fun findByEmail(email: String): Student
    fun addProject(studentId: Long, projectId: Long): Student
    fun isHisProject(studentId: Long, projectId: Long): Boolean
    fun readAll(): List<Student>
    fun clearStudents()
}