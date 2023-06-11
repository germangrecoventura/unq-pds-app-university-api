package unq.pds.services

import unq.pds.api.dtos.DeployInstanceCommentDTO
import unq.pds.api.dtos.StudentCreateRequestDTO
import unq.pds.model.Comment
import unq.pds.model.Student

interface StudentService {
    fun save(studentCreateRequestDTO: StudentCreateRequestDTO): Student
    fun update(studentDTO: StudentCreateRequestDTO): Student
    fun deleteById(id: Long)
    fun count(): Int
    fun findById(id: Long): Student
    fun findByEmail(email: String): Student
    fun readAll(): List<Student>
    fun addCommentToDeployInstance(commentDTO: DeployInstanceCommentDTO): Comment
    fun clearStudents()
}