package unq.pds.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.api.dtos.DeployInstanceCommentDTO
import unq.pds.api.dtos.StudentCreateRequestDTO
import unq.pds.model.Comment
import unq.pds.model.Student
import unq.pds.model.exceptions.AlreadyRegisteredException
import unq.pds.persistence.CommentDAO
import unq.pds.persistence.DeployInstanceDAO
import unq.pds.persistence.StudentDAO
import unq.pds.services.StudentService
import unq.pds.services.UserService

@Service
@Transactional
open class StudentServiceImpl : StudentService {

    @Autowired
    lateinit var studentDAO: StudentDAO

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var deployInstanceDAO: DeployInstanceDAO

    @Autowired
    lateinit var commentDAO: CommentDAO

    override fun save(studentCreateRequestDTO: StudentCreateRequestDTO): Student {
        val encryptor = BCryptPasswordEncoder()
        val myEncryptedPassword = encryptor.encode(studentCreateRequestDTO.password)
        if (studentDAO.findByEmail(studentCreateRequestDTO.email!!).isPresent) throw AlreadyRegisteredException("email")
        val student = Student(
            studentCreateRequestDTO.firstName!!,
            studentCreateRequestDTO.lastName!!,
            studentCreateRequestDTO.email!!,
            myEncryptedPassword,
        )
        return studentDAO.save(student)
    }

    override fun update(studentDTO: StudentCreateRequestDTO): Student {
        val studentWithEmail = studentDAO.findByEmail(studentDTO.email!!)
        if ((userService.theEmailIsRegistered(studentDTO.email!!) && !studentWithEmail.isPresent)
            ||
            (studentWithEmail.isPresent && studentDTO.id != studentWithEmail.get().getId())) {
            throw AlreadyRegisteredException("email")
        }
        val studentFind = findById(studentDTO.id!!)
        studentFind.setFirstName(studentDTO.firstName)
        studentFind.setLastName(studentDTO.lastName)
        studentFind.setEmail(studentDTO.email)
        studentFind.setPassword(studentDTO.password!!)
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

    override fun readAll(): List<Student> {
        return studentDAO.findAll().toList()
    }

    override fun addCommentToDeployInstance(commentDTO: DeployInstanceCommentDTO): Comment {
        val deployInstanceRecovery = deployInstanceDAO.findById(commentDTO.deployInstanceId!!).orElseThrow {
            NoSuchElementException("Not found the deploy instance")
        }
        val comment = commentDAO.save(Comment(commentDTO.comment!!))
        deployInstanceRecovery.addComment(comment)
        deployInstanceDAO.save(deployInstanceRecovery)
        return comment
    }

    override fun clearStudents() {
        studentDAO.deleteAll()
    }

}