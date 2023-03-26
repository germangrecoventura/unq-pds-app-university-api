package unq.pds.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.model.Teacher
import unq.pds.persistence.TeacherDAO
import unq.pds.webservice.Validator
import unq.pds.webservice.dto.TeacherCreateRequestDTO

@Service
@Transactional
open class TeacherServiceImpl {
    @Autowired
    lateinit var teacherDAO: TeacherDAO

    fun save(teacherCreateRequestDTO: TeacherCreateRequestDTO): Teacher {
        if (teacherCreateRequestDTO.firstName.isNullOrBlank()) {
            throw RuntimeException("The firstname cannot be empty")
        }
        if (Validator.containsSpecialCharacter(teacherCreateRequestDTO.firstName)) {
            throw RuntimeException("The firstname can not contain special characters")
        }
        if (Validator.containsNumber(teacherCreateRequestDTO.firstName)) {
            throw RuntimeException("The firstname can not contain numbers")
        }
        if (teacherCreateRequestDTO.lastName.isNullOrBlank()) {
            throw RuntimeException("The lastname cannot be empty")
        }
        if (Validator.containsSpecialCharacter(teacherCreateRequestDTO.lastName)) {
            throw RuntimeException("The lastname can not contain special characters")
        }
        if (Validator.containsNumber(teacherCreateRequestDTO.lastName)) {
            throw RuntimeException("The lastname can not contain numbers")
        }
        if (teacherCreateRequestDTO.email.isNullOrBlank()) {
            throw RuntimeException("The email cannot be empty")
        }
        if (!Validator.isValidEMail(teacherCreateRequestDTO.email)) {
            throw RuntimeException("The email is not valid")
        }
        val teacherFound =
            teacherDAO.findAll().find { teacher: Teacher -> teacher.getEmail() == teacherCreateRequestDTO.email }

        if (teacherFound != null) {
            throw RuntimeException("The email is already registered")
        }

        val teacher = Teacher(
            teacherCreateRequestDTO.firstName,
            teacherCreateRequestDTO.lastName,
            teacherCreateRequestDTO.email
        )
        return teacherDAO.save(teacher)
    }

    fun update(teacher: Teacher): Teacher {
        var teacherUpdate = teacherDAO.findAllById(teacher.getId())
        if (teacherUpdate.isNullOrEmpty()) {
            throw RuntimeException("Not found the student")
        }
        if (teacher.getFirstName().isNullOrBlank()) {
            throw RuntimeException("The firstname cannot be empty")
        }
        if (Validator.containsSpecialCharacter(teacher.getFirstName())) {
            throw RuntimeException("The firstname can not contain special characters")
        }
        if (Validator.containsNumber(teacher.getFirstName())) {
            throw RuntimeException("The firstname can not contain numbers")
        }
        if (teacher.getLastName().isNullOrBlank()) {
            throw RuntimeException("The lastname cannot be empty")
        }
        if (Validator.containsSpecialCharacter(teacher.getLastName())) {
            throw RuntimeException("The lastname can not contain special characters")
        }
        if (Validator.containsNumber(teacher.getLastName())) {
            throw RuntimeException("The lastname can not contain numbers")
        }
        if (teacher.getEmail().isNullOrBlank()) {
            throw RuntimeException("The email cannot be empty")
        }
        if (!Validator.isValidEMail(teacher.getEmail())) {
            throw RuntimeException("The email is not valid")
        }
        val teacherFound =
            teacherDAO.findAll().filter { t -> t.getId() != teacher.getId() }
                .find { teacherSearch: Teacher -> teacherSearch.getEmail() == teacher.getEmail() }

        if (teacherFound != null) {
            throw RuntimeException("The email is already registered")
        }
        teacherUpdate[0].setFirstName(teacher.getFirstName())
        teacherUpdate[0].setLastName(teacher.getLastName())
        teacherUpdate[0].setEmail(teacher.getEmail())
        teacherDAO.save(teacherUpdate[0])
        return teacherUpdate[0]
    }

    fun deleteById(id: Long) {
        teacherDAO.deleteById(id)
    }

    fun count(): Int {
        return teacherDAO.count().toInt()
    }


    fun clearTeachers() {
        teacherDAO.deleteAll()
    }

}
