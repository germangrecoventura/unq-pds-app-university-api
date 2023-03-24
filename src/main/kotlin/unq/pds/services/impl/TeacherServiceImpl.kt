package unq.pds.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
        if (teacher.getLastName().isNullOrBlank()) {
            throw Throwable("The lastname cannot be empty")
        }
        if (teacher.getEmail().isNullOrBlank()) {
            throw Throwable("The email cannot be empty")
        }
        return teacherDAO.save(teacher)
    }

    override fun clearTeachers() {
        teacherDAO.deleteAll()
    }
}
