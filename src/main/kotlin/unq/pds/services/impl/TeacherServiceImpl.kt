package unq.pds.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.model.Teacher
import unq.pds.persistence.TeacherDAO
import unq.pds.services.TeacherService
import java.util.regex.Pattern

@Service
@Transactional
open class TeacherServiceImpl : TeacherService {

    @Autowired
    private lateinit var teacherDAO: TeacherDAO

    val EMAIL_ADDRESS_PATTERN: Pattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    override fun save(teacher: Teacher): Teacher {
        if (teacher.getFirstName().isNullOrBlank()) {
            throw Throwable("The firstname cannot be empty")
        }
        if (teacher.getLastName().isNullOrBlank()) {
            throw Throwable("The lastname cannot be empty")
        }
        if (teacher.getEmail().isNullOrBlank()) {
            throw Throwable("The email cannot be empty")
        }
        if (!EMAIL_ADDRESS_PATTERN.matcher(teacher.getEmail()).matches()) {
            throw Throwable("The email given is not an email")
        }
        return teacherDAO.save(teacher)
    }

    override fun delete(teacher: Teacher) {
        teacherDAO.delete(teacher)
    }

    override fun count(): Int {
        return teacherDAO.count().toInt()
    }
    override fun clearTeachers() {
        teacherDAO.deleteAll()
    }
}
