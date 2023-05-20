package unq.pds.services.impl

import org.jasypt.util.text.AES256TextEncryptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.api.dtos.StudentCreateRequestDTO
import unq.pds.model.Student
import unq.pds.model.exceptions.AlreadyRegisteredException
import unq.pds.persistence.StudentDAO
import unq.pds.services.StudentService

@Service
@Transactional
open class StudentServiceImpl : StudentService {

    @Autowired
    lateinit var studentDAO: StudentDAO

    override fun save(studentCreateRequestDTO: StudentCreateRequestDTO): Student {
        val encryptor = AES256TextEncryptor()
        encryptor.setPassword(System.getenv("ENCRYPT_PASSWORD"))
        val myEncryptedPassword = encryptor.encrypt(studentCreateRequestDTO.password)
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
        if (studentWithEmail.isPresent && studentDTO.id != studentWithEmail.get().getId()) {
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

    override fun clearStudents() {
        studentDAO.deleteAll()
    }

}