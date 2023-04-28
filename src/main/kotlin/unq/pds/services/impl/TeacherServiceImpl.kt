package unq.pds.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.api.dtos.TeacherCreateRequestDTO
import unq.pds.model.Teacher
import unq.pds.model.exceptions.AlreadyRegisteredException
import unq.pds.persistence.TeacherDAO
import unq.pds.services.TeacherService
import javax.management.InvalidAttributeValueException

@Service
@Transactional
open class TeacherServiceImpl : TeacherService {
    @Autowired
    lateinit var teacherDAO: TeacherDAO

    override fun save(teacherCreateRequestDTO: TeacherCreateRequestDTO): Teacher {
        if (teacherDAO.findByEmail(teacherCreateRequestDTO.email!!).isPresent) {
            throw AlreadyRegisteredException("email")
        }
        if (teacherCreateRequestDTO.password.isNullOrBlank()) {
            throw InvalidAttributeValueException("The password cannot be empty")
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
}
