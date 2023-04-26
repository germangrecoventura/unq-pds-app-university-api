package unq.pds.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.api.dtos.StudentCreateRequestDTO
import unq.pds.model.Student
import unq.pds.model.exceptions.AlreadyRegisteredException
import unq.pds.model.exceptions.ProjectAlreadyHasAnOwnerException
import unq.pds.persistence.StudentDAO
import unq.pds.services.ProjectService
import unq.pds.services.StudentService

@Service
@Transactional
open class StudentServiceImpl : StudentService {

    @Autowired lateinit var studentDAO: StudentDAO
    @Autowired lateinit var projectService: ProjectService

    override fun save(studentCreateRequestDTO: StudentCreateRequestDTO): Student {
        if (studentDAO.findByEmail(studentCreateRequestDTO.email!!).isPresent) {
            throw AlreadyRegisteredException("email")
        }

        val student = Student(
            studentCreateRequestDTO.firstName!!,
            studentCreateRequestDTO.lastName!!,
            studentCreateRequestDTO.email!!
        )
        return studentDAO.save(student)
    }

    override fun update(student: Student): Student {
        var studentWithEmail = studentDAO.findByEmail(student.getEmail()!!)
        if (studentWithEmail.isPresent && student.getId() != studentWithEmail.get().getId()) {
            throw AlreadyRegisteredException("email")
        }
        if (student.getId() != null && studentDAO.existsById(student.getId()!!)) return studentDAO.save(student)
         else throw NoSuchElementException("Student does not exist")
    }

    override fun deleteById(id: Long) {
        try {
            studentDAO.deleteById(id)
        } catch (e: RuntimeException) {
            throw NoSuchElementException("The student with id $id is not registered")
        }
    }

    override fun count(): Int {
        return studentDAO.count().toInt()
    }

    override fun findById(id: Long): Student {
        return studentDAO.findById(id).orElseThrow { NoSuchElementException("Not found the student with id $id") }
    }

    override fun findByEmail(email: String): Student {
        return studentDAO.findByEmail(email)
            .orElseThrow { NoSuchElementException("Not found the student with email $email") }
    }

    override fun addProject(studentId: Long, projectId: Long): Student {
        val student = this.findById(studentId)
        val project = projectService.read(projectId)
        if (studentDAO.projectOwnerOfTheProject(project).isPresent) {
            throw ProjectAlreadyHasAnOwnerException()
        }
        student.addProject(project)

        return this.update(student)
    }

    override fun readAll(): List<Student> {
        return studentDAO.findAll().toList()
    }

    override fun clearStudents() {
        studentDAO.deleteAll()
    }

}