package unq.pds.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.api.controllers.Validator
import unq.pds.api.dtos.TeacherCreateRequestDTO
import unq.pds.model.Teacher
import unq.pds.persistence.TeacherDAO
import unq.pds.services.TeacherService

@Service
@Transactional
open class TeacherServiceImpl : TeacherService {

    @Autowired
    private lateinit var teacherDAO: TeacherDAO

    override fun save(teacherCreateRequestDTO: TeacherCreateRequestDTO): Teacher {
        if (teacherCreateRequestDTO.firstName.isNullOrBlank()) {
            throw Throwable("The firstname cannot be empty")
        }
        if (Validator.containsSpecialCharacter(teacherCreateRequestDTO.firstName)) {
            throw Throwable("First Name can not contain special characters")
        }
        if (Validator.containsNumber(teacherCreateRequestDTO.firstName)) {
            throw Throwable("First Name can not contain numbers")
        }
        if (teacherCreateRequestDTO.lastName.isNullOrBlank()) {
            throw Throwable("The lastname cannot be empty")
        }
        if (Validator.containsSpecialCharacter(teacherCreateRequestDTO.lastName)) {
            throw Throwable("Last Name can not contain special characters")
        }
        if (Validator.containsNumber(teacherCreateRequestDTO.lastName)) {
            throw Throwable("Last Name can not contain numbers")
        }
        if (teacherCreateRequestDTO.email.isNullOrBlank()) {
            throw Throwable("The email cannot be empty")
        }
        if (!Validator.isValidEMail(teacherCreateRequestDTO.email)) {
            throw Throwable("Mail is not valid")
        }
        val teacherFound =
            teacherDAO.findAll().find { teacher: Teacher -> teacher.getEmail() == teacherCreateRequestDTO.email }

        if (teacherFound != null) {
            throw Throwable("The email is already registered")
        }
        val teacher = Teacher(
            teacherCreateRequestDTO.firstName,
            teacherCreateRequestDTO.lastName,
            teacherCreateRequestDTO.email
        )
        return teacherDAO.save(teacher)
    }

    override fun update(teacherUpdate: Teacher): Teacher {
        if (teacherUpdate.getEmail().isNullOrBlank()) {
            throw Throwable("The email cannot be empty")
        }
        if (!Validator.isValidEMail(teacherUpdate.getEmail())) {
            throw Throwable("Mail is not valid")
        }
        teacherDAO.saveAndFlush(teacherUpdate)
        return teacherUpdate
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
