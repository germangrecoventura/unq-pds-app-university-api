package unq.pds.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.model.Admin
import unq.pds.model.Student
import unq.pds.model.Teacher
import unq.pds.persistence.AdminDAO
import unq.pds.persistence.StudentDAO
import unq.pds.persistence.TeacherDAO
import unq.pds.services.UserService
import java.util.*

@Service
@Transactional
open class UserServiceImpl : UserService {

    @Autowired
    lateinit var studentDAO: StudentDAO

    @Autowired
    lateinit var teacherDAO: TeacherDAO

    @Autowired
    lateinit var adminDAO: AdminDAO
    override fun findStudent(email: String): Optional<Student> {
        return studentDAO.findByEmail(email)
    }

    override fun findTeacher(email: String): Optional<Teacher> {
        return teacherDAO.findByEmail(email)
    }

    override fun findAdmin(email: String): Optional<Admin> {
        return adminDAO.findByEmail(email)
    }


}