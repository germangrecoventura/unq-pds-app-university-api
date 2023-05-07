package unq.pds.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.api.dtos.StudentCreateRequestDTO
import unq.pds.model.Student
import unq.pds.model.exceptions.AlreadyRegisteredException
import unq.pds.model.exceptions.ProjectAlreadyHasAnOwnerException
import unq.pds.persistence.StudentDAO
import unq.pds.services.ProjectService
import unq.pds.services.StudentService
import javax.management.InvalidAttributeValueException

@Service
@Transactional
open class StudentServiceImpl : StudentService {

    @Autowired
    lateinit var studentDAO: StudentDAO

    @Autowired
    lateinit var projectService: ProjectService

    override fun save(studentCreateRequestDTO: StudentCreateRequestDTO): Student {
        if (studentDAO.findByEmail(studentCreateRequestDTO.email!!).isPresent) {
            throw AlreadyRegisteredException("email")
        }

        if (studentCreateRequestDTO.ownerGithub != null) {
            var studentWithOwnerGithub = studentDAO.findByOwnerGithub(studentCreateRequestDTO.ownerGithub!!)
            if (studentWithOwnerGithub.isPresent) {
                throw AlreadyRegisteredException("owner github")
            }
        }
        if (studentCreateRequestDTO.tokenGithub != null) {
            var studentWithOwnerGithub = studentDAO.findByTokenGithub(studentCreateRequestDTO.tokenGithub!!)
            if (studentWithOwnerGithub.isPresent) {
                throw AlreadyRegisteredException("token github")
            }
        }

        val student = Student(
            studentCreateRequestDTO.firstName!!,
            studentCreateRequestDTO.lastName!!,
            studentCreateRequestDTO.email!!,
            BCryptPasswordEncoder().encode(studentCreateRequestDTO.password!!),
            studentCreateRequestDTO.ownerGithub,
            studentCreateRequestDTO.tokenGithub
        )
        return studentDAO.save(student)
    }

    override fun update(student: StudentCreateRequestDTO): Student {
        if (student.ownerGithub != null) {
            var studentWithOwnerGithub = studentDAO.findByOwnerGithub(student.ownerGithub!!)
            if (studentWithOwnerGithub.isPresent && student.id != studentWithOwnerGithub.get().getId()) {
                throw AlreadyRegisteredException("owner github")
            }
        }
        if (student.tokenGithub != null) {
            var studentWithToken = studentDAO.findByTokenGithub(student.tokenGithub!!)
            if (studentWithToken.isPresent && student.id != studentWithToken.get().getId()) {
                throw AlreadyRegisteredException("token github")
            }
        }

        var studentWithEmail = studentDAO.findByEmail(student.email!!)
        if (studentWithEmail.isPresent && student.id != studentWithEmail.get().getId()) {
            throw AlreadyRegisteredException("email")
        }

        if (student.id != null && studentDAO.existsById(student.id!!)) {
            val studentFind = studentDAO.findById(student.id!!).get()
            studentFind.apply {
                setFirstName(student.firstName)
                setLastName(student.lastName)
                setEmail(student.email)
                setPassword(BCryptPasswordEncoder().encode(student.password))
                setOwnerGithub(student.ownerGithub)
                setTokenGithub(student.tokenGithub)
            }
            return studentDAO.save(studentFind)
        }
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

        return studentDAO.save(student)
    }

    override fun readAll(): List<Student> {
        return studentDAO.findAll().toList()
    }

    override fun clearStudents() {
        studentDAO.deleteAll()
    }

}