package unq.pds.services

import unq.pds.model.Admin
import unq.pds.model.Student
import unq.pds.model.Teacher
import java.util.*

interface UserService {
    fun findStudent(email: String): Optional<Student>
    fun findTeacher(email: String): Optional<Teacher>
    fun findAdmin(email: String): Optional<Admin>
    fun theEmailIsRegistered(email: String): Boolean
}