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
import unq.pds.persistence.*
import unq.pds.services.TeacherService

@Service
@Transactional
open class TeacherServiceImpl : TeacherService {
    @Autowired
    lateinit var teacherDAO: TeacherDAO

    @Autowired
    lateinit var studentDAO: StudentDAO

    @Autowired
    lateinit var repositoryDAO: RepositoryDAO

    @Autowired
    lateinit var commentDAO: CommentDAO

    @Autowired
    lateinit var groupDAO: GroupDAO

    override fun save(teacherCreateRequestDTO: TeacherCreateRequestDTO): Teacher {
        if (teacherDAO.findByEmail(teacherCreateRequestDTO.email!!).isPresent) {
            throw AlreadyRegisteredException("email")
        }
        val teacher = Teacher(
            teacherCreateRequestDTO.firstName!!,
            teacherCreateRequestDTO.lastName!!,
            teacherCreateRequestDTO.email!!,
            BCryptPasswordEncoder().encode(teacherCreateRequestDTO.password)
        )
        return teacherDAO.save(teacher)
    }


    override fun update(teacher: Teacher): Teacher {
        var teacherRecovery = findById(teacher.getId()!!)
        var teacherWithEmail = teacherDAO.findByEmail(teacher.getEmail())
        if (teacherWithEmail.isPresent && teacherRecovery.getId() != teacherWithEmail.get().getId()) {
            throw AlreadyRegisteredException("email")
        }
        teacherRecovery.setFirstName(teacher.getFirstName())
        teacherRecovery.setLastName(teacher.getLastName())
        teacherRecovery.setEmail(teacher.getEmail())
        teacherRecovery.setPassword(BCryptPasswordEncoder().encode(teacher.getPassword()))
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

    override fun addCommentToStudent(commentDTO: CommentCreateRequestDTO): Comment {
        val studentRecovery = studentDAO.findById(commentDTO.idToComment)
            .orElseThrow { NoSuchElementException("Not found the student with id ${commentDTO.idToComment}") }
        val repositoryRecovery = repositoryDAO.findByName(commentDTO.nameRepository!!)
            .orElseThrow { NoSuchElementException("Not found the repository with name ${commentDTO.nameRepository}") }
        studentRecovery.projects.find { project -> project.repositories.contains(repositoryRecovery) }
            ?: throw NoSuchElementException("Not found the repository with student")

        val comment = Comment(commentDTO.comment!!)
        val commentSaved = commentDAO.save(comment)
        repositoryRecovery.addComment(commentSaved)
        repositoryDAO.save(repositoryRecovery)
        return commentSaved
    }

    override fun addCommentToGroup(commentDTO: CommentCreateRequestDTO): Comment {
        val groupRecovery = groupDAO.findById(commentDTO.idToComment)
            .orElseThrow { NoSuchElementException("Not found the group with id ${commentDTO.idToComment}") }
        val repositoryRecovery = repositoryDAO.findByName(commentDTO.nameRepository!!)
            .orElseThrow { NoSuchElementException("Not found the repository with name ${commentDTO.nameRepository}") }
        groupRecovery.projects.find { project -> project.repositories.contains(repositoryRecovery) }
            ?: throw NoSuchElementException("Not found the repository with group")

        val comment = Comment(commentDTO.comment!!)
        val commentSaved = commentDAO.save(comment)
        repositoryRecovery.addComment(commentSaved)
        repositoryDAO.save(repositoryRecovery)
        return commentSaved
    }
}
