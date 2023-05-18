package unq.pds.services.impl

import org.jasypt.util.text.AES256TextEncryptor
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
import unq.pds.services.UserService
import javax.management.InvalidAttributeValueException

@Service
@Transactional
open class StudentServiceImpl : StudentService {

    @Autowired
    lateinit var studentDAO: StudentDAO

    @Autowired
    lateinit var projectService: ProjectService

    @Autowired
    lateinit var userService: UserService

    override fun save(studentCreateRequestDTO: StudentCreateRequestDTO): Student {
        val encryptor = AES256TextEncryptor()
        encryptor.setPassword(System.getenv("ENCRYPT_PASSWORD"))
        val myEncryptedToken = encryptor.encrypt(studentCreateRequestDTO.tokenGithub)
        val myEncryptedPassword = encryptor.encrypt(studentCreateRequestDTO.password)
        if (userService.theEmailIsRegistered(studentCreateRequestDTO.email!!)) throw AlreadyRegisteredException("email")
        if (studentCreateRequestDTO.ownerGithub != null) {
            var studentWithOwnerGithub = studentDAO.findByOwnerGithub(studentCreateRequestDTO.ownerGithub!!)
            if (studentWithOwnerGithub.isPresent) {
                throw AlreadyRegisteredException("owner github")
            }
        }
        val student = Student(
            studentCreateRequestDTO.firstName!!,
            studentCreateRequestDTO.lastName!!,
            studentCreateRequestDTO.email!!,
            myEncryptedPassword,
            studentCreateRequestDTO.ownerGithub,
            myEncryptedToken
        )
        return studentDAO.save(student)
    }

    override fun update(studentDTO: StudentCreateRequestDTO): Student {
        if (studentDTO.ownerGithub != null) {
            val studentWithOwnerGithub = studentDAO.findByOwnerGithub(studentDTO.ownerGithub!!)
            if (studentWithOwnerGithub.isPresent && studentDTO.id != studentWithOwnerGithub.get().getId()) {
                throw AlreadyRegisteredException("owner github")
            }
        }
        val studentWithEmail = studentDAO.findByEmail(studentDTO.email!!)
        if (userService.theEmailIsRegistered(studentDTO.email!!) && !studentWithEmail.isPresent) {
            throw AlreadyRegisteredException("email")
        }
        if (studentWithEmail.isPresent && studentDTO.id != studentWithEmail.get().getId()) {
            throw AlreadyRegisteredException("email")
        }
        val studentFind = findById(studentDTO.id!!)
        studentFind.setFirstName(studentDTO.firstName)
        studentFind.setLastName(studentDTO.lastName)
        studentFind.setEmail(studentDTO.email)
        studentFind.setPassword(studentDTO.password!!)
        studentFind.setOwnerGithub(studentDTO.ownerGithub)
        studentFind.setTokenGithub(studentDTO.tokenGithub)
        return studentDAO.save(studentFind)
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

    override fun isHisProject(studentId: Long, projectId: Long): Boolean {
        return studentDAO.isHisProject(studentId, projectId)
    }

    override fun readAll(): List<Student> {
        return studentDAO.findAll().toList()
    }

    override fun clearStudents() {
        studentDAO.deleteAll()
    }

}