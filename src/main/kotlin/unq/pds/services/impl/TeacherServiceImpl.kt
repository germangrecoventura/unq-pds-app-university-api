package unq.pds.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.api.dtos.CommentCreateRequestDTO
import unq.pds.api.dtos.TeacherCreateRequestDTO
import unq.pds.model.Comment
import unq.pds.model.Teacher
import unq.pds.model.exceptions.AlreadyRegisteredException
import unq.pds.persistence.CommentDAO
import unq.pds.persistence.RepositoryDAO
import unq.pds.persistence.TeacherDAO
import unq.pds.services.TeacherService
import unq.pds.services.UserService

@Service
@Transactional
open class TeacherServiceImpl : TeacherService {
    @Autowired
    lateinit var teacherDAO: TeacherDAO

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var repositoryDAO: RepositoryDAO

    @Autowired
    lateinit var commentDAO: CommentDAO


    override fun save(teacherCreateRequestDTO: TeacherCreateRequestDTO): Teacher {
        if (userService.theEmailIsRegistered(teacherCreateRequestDTO.email!!)) throw AlreadyRegisteredException("email")
        val encryptor = BCryptPasswordEncoder()
        val myEncryptedPassword = encryptor.encode(teacherCreateRequestDTO.password)
        val teacher = Teacher(
            teacherCreateRequestDTO.firstName!!,
            teacherCreateRequestDTO.lastName!!,
            teacherCreateRequestDTO.email!!,
            myEncryptedPassword
        )
        return teacherDAO.save(teacher)
    }


    override fun update(teacher: Teacher): Teacher {
        var teacherRecovery = findById(teacher.getId()!!)
        var teacherWithEmail = teacherDAO.findByEmail(teacher.getEmail()!!)
        if (userService.theEmailIsRegistered(teacher.getEmail()!!) && !teacherWithEmail.isPresent) {
            throw AlreadyRegisteredException("email")
        }
        if (teacherWithEmail.isPresent && teacherRecovery.getId() != teacherWithEmail.get().getId()) {
            throw AlreadyRegisteredException("email")
        }
        teacherRecovery.setFirstName(teacher.getFirstName())
        teacherRecovery.setLastName(teacher.getLastName())
        teacherRecovery.setEmail(teacher.getEmail())
        teacherRecovery.setPassword(teacher.getPassword())
        return teacherDAO.save(teacherRecovery)
    }

    override fun deleteById(id: Long) {
        try {
            teacherDAO.deleteById(id)
        } catch (e: RuntimeException) {
            throw NoSuchElementException("The teacher with id $id is not registered")
        }
    }

    override fun count(): Int {
        return teacherDAO.count().toInt()
    }

    override fun findById(id: Long): Teacher {
        return teacherDAO.findById(id).orElseThrow { NoSuchElementException("Not found the teacher with id $id") }
    }

    override fun findByEmail(email: String): Teacher {
        return teacherDAO.findByEmail(email)
            .orElseThrow { NoSuchElementException("Not found the teacher with email $email") }
    }

    override fun readAll(): List<Teacher> {
        return teacherDAO.findAll().toList()
    }

    override fun clearTeachers() {
        teacherDAO.deleteAll()
    }

    override fun addCommentToRepository(commentDTO: CommentCreateRequestDTO): Comment {
        val repositoryRecovery = repositoryDAO.findById(commentDTO.repositoryId!!).orElseThrow {
            NoSuchElementException("Not found the repository")
        }
        val comment = commentDAO.save(Comment(commentDTO.comment!!))
        repositoryRecovery.addComment(comment)
        repositoryDAO.save(repositoryRecovery)
        return comment
    }
}
