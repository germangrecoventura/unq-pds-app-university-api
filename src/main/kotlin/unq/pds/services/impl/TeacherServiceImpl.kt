package unq.pds.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.api.controllers.Validator
import unq.pds.model.Teacher
import unq.pds.persistence.TeacherDAO
import unq.pds.services.TeacherService

@Service
@Transactional
open class TeacherServiceImpl : TeacherService {

    @Autowired
    private lateinit var teacherDAO: TeacherDAO

    override fun save(teacher: Teacher): Teacher {
        if (teacher.getFirstName().isNullOrBlank()) {
            throw Throwable("The firstname cannot be empty")
        }
        if (Validator.containsSpecialCharacter(teacher.getFirstName())) {
            throw Throwable("First Name can not contain special characters")
        }
        if (Validator.containsNumber(teacher.getFirstName())) {
            throw Throwable("First Name can not contain numbers")
        }
        if (teacher.getLastName().isNullOrBlank()) {
            throw Throwable("The lastname cannot be empty")
        }
        if (Validator.containsSpecialCharacter(teacher.getLastName())) {
            throw Throwable("Last Name can not contain special characters")
        }
        if (Validator.containsNumber(teacher.getLastName())) {
            throw Throwable("Last Name can not contain numbers")
        }
        if (teacher.getEmail().isNullOrBlank()) {
            throw Throwable("The email cannot be empty")
        }
        if (!Validator.isValidEMail(teacher.getEmail())) {
            throw Throwable("Mail is not valid")
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
