package unq.pds.services.impl


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.api.Validator
import unq.pds.api.dtos.StudentCreateRequestDTO
import unq.pds.model.Student
import unq.pds.persistence.StudentDAO
import unq.pds.services.StudentService

@Service
@Transactional
open class StudentServiceImpl : StudentService {
    @Autowired
    lateinit var studentDAO: StudentDAO

    override fun save(studentCreateRequestDTO: StudentCreateRequestDTO): Student {
        if (studentCreateRequestDTO.firstName.isNullOrBlank()) {
            throw RuntimeException("The firstname cannot be empty")
        }
        if (Validator.containsSpecialCharacter(studentCreateRequestDTO.firstName)) {
            throw RuntimeException("The firstname can not contain special characters")
        }
        if (Validator.containsNumber(studentCreateRequestDTO.firstName)) {
            throw RuntimeException("The firstname can not contain numbers")
        }
        if (studentCreateRequestDTO.lastName.isNullOrBlank()) {
            throw RuntimeException("The lastname cannot be empty")
        }
        if (Validator.containsSpecialCharacter(studentCreateRequestDTO.lastName)) {
            throw RuntimeException("The lastname can not contain special characters")
        }
        if (Validator.containsNumber(studentCreateRequestDTO.lastName)) {
            throw RuntimeException("The lastname can not contain numbers")
        }
        if (studentCreateRequestDTO.email.isNullOrBlank()) {
            throw RuntimeException("The email cannot be empty")
        }
        if (!Validator.isValidEMail(studentCreateRequestDTO.email)) {
            throw RuntimeException("The email is not valid")
        }
        val studentFound =
            studentDAO.findAll().find { student: Student -> student.getEmail() == studentCreateRequestDTO.email }

        if (studentFound != null) {
            throw RuntimeException("The email is already registered")
        }

        val student = Student(
            studentCreateRequestDTO.firstName,
            studentCreateRequestDTO.lastName,
            studentCreateRequestDTO.email
        )
        return studentDAO.save(student)
    }

    override fun update(student: Student): Student {
        var studentUpdate = studentDAO.findAllById(student.getId())
        if (studentUpdate.isNullOrEmpty()) {
            throw RuntimeException("Not found the student with id ${student.getId()}")
        }/*
        if (student.getFirstName().isNullOrBlank()) {
            throw RuntimeException("The firstname cannot be empty")
        }
        if (Validator.containsSpecialCharacter(student.getFirstName())) {
            throw RuntimeException("The firstname can not contain special characters")
        }
        if (Validator.containsNumber(student.getFirstName())) {
            throw RuntimeException("The firstname can not contain numbers")
        }
        if (student.getLastName().isNullOrBlank()) {
            throw RuntimeException("The lastname cannot be empty")
        }
        if (Validator.containsSpecialCharacter(student.getLastName())) {
            throw RuntimeException("The lastname can not contain special characters")
        }
        if (Validator.containsNumber(student.getLastName())) {
            throw RuntimeException("The lastname can not contain numbers")
        }
        if (student.getEmail().isNullOrBlank()) {
            throw RuntimeException("The email cannot be empty")
        }
        if (!Validator.isValidEMail(student.getEmail())) {
            throw RuntimeException("The email is not valid")
        }*/
        val studentFound =
            studentDAO.findAll().filter { s -> s.getId() != student.getId() }
                .find { studentSearch: Student -> studentSearch.getEmail() == student.getEmail() }

        if (studentFound != null) {
            throw RuntimeException("The email is already registered")
        }
        studentUpdate[0].setFirstName(student.getFirstName())
        studentUpdate[0].setLastName(student.getLastName())
        studentUpdate[0].setEmail(student.getEmail())
        return studentDAO.save(studentUpdate[0])
    }

    override fun deleteById(id: Long) {
        try {
            studentDAO.deleteById(id)
        } catch (e: RuntimeException) {
            throw RuntimeException("The student with id $id is not registered")
        }
    }

    override fun count(): Int {
        return studentDAO.count().toInt()
    }

    override fun findAllById(id: Long?): MutableList<Student>? {
        return studentDAO.findAllById(id)
    }


    override fun clearStudents() {
        studentDAO.deleteAll()
    }

}