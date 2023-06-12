package unq.pds.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import unq.pds.persistence.AdminDAO
import unq.pds.persistence.StudentDAO
import unq.pds.persistence.TeacherDAO
import java.util.stream.Collectors

@Service
class CustomUserDetailService : UserDetailsService {
    @Autowired
    private lateinit var studentDAO: StudentDAO

    @Autowired
    private lateinit var teacherDAO: TeacherDAO

    @Autowired
    private lateinit var adminDAO: AdminDAO

    private fun mapToAutorities(roles: List<String>): Collection<GrantedAuthority> {
        return roles.stream().map { role -> SimpleGrantedAuthority(role) }.collect(Collectors.toList())
    }

    override fun loadUserByUsername(email: String): UserDetails {
        val student = studentDAO.findByEmail(email)
        if (student.isPresent) {
            return User(
                email,
                student.get().getPassword(),
                mapToAutorities(listOf(student.get().getRole()))
            )
        }
        val teacher = teacherDAO.findByEmail(email)
        if (teacher.isPresent) {
            return User(
                email,
                teacher.get().getPassword(),
                mapToAutorities(listOf(teacher.get().getRole()))
            )
        }
        val admin = adminDAO.findByEmail(email)
            .orElseThrow { UsernameNotFoundException(String.format("User: %s, not found", email)) }
        return User(
            email,
            admin.getPassword(),
            mapToAutorities(listOf(admin.getRole()))
        )
    }
}