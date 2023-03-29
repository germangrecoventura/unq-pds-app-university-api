package unq.pds.services

import unq.pds.api.dtos.StudentCreateRequestDTO
import unq.pds.model.Student
import unq.pds.model.Teacher
import java.util.*

interface StudentService {
    fun save(studentCreateRequestDTO: StudentCreateRequestDTO): Student
    fun update(student: Student): Student
    fun deleteById(id: Long)
    fun count(): Int
    fun findById(id: Long): Optional<Student>
    fun findByEmail(email: String): Optional<Student>
    fun clearStudents()
}