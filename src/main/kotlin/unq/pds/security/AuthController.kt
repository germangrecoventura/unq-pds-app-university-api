package unq.pds.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import unq.pds.api.dtos.MessageDTO
import unq.pds.services.StudentService
import unq.pds.services.TeacherService
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@CrossOrigin
class AuthController(private val studentService: StudentService, private val teacherService: TeacherService) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody body: LoginDTO, response: HttpServletResponse): ResponseEntity<Any> {
        return try {
            if (body.role == "STUDENT") {
                studentLogin(body, response)
            } else {
                teacherLogin(body, response)
            }
        } catch (e: NoSuchElementException) {
            ResponseEntity(MessageDTO(e.message!!), HttpStatus.UNAUTHORIZED)
        }
    }

    @PostMapping("/logout")
    fun logout(@CookieValue("jwt") jwt: String?, response: HttpServletResponse): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
        }
        val cookie = Cookie("jwt", null)
        cookie.isHttpOnly = true
        cookie.maxAge = 0
        response.addCookie(cookie)
        return ResponseEntity("You successfully logged out", HttpStatus.OK)
    }

    @GetMapping("user-logged")
    fun user(@CookieValue("jwt") jwt: String?): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
        }
        val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
        return if (body["role"] == "STUDENT") ResponseEntity(studentService.findByEmail(body.issuer), HttpStatus.OK)
        else ResponseEntity(teacherService.findByEmail(body.issuer), HttpStatus.OK)
    }

    private fun studentLogin(body: LoginDTO, response: HttpServletResponse): ResponseEntity<Any> {
        val student = studentService.findByEmail(body.email!!)
        if (!student.comparePassword(body.password!!)) {
            return ResponseEntity(MessageDTO("Password is incorrect"), HttpStatus.UNAUTHORIZED)
        }
        val issuer = student.getEmail().toString()
        val role = student.getRole()
        addCookie(role, issuer, response)
        return ResponseEntity("You are logged in correctly", HttpStatus.OK)
    }


    private fun teacherLogin(body: LoginDTO, response: HttpServletResponse): ResponseEntity<Any> {
        val teacher = teacherService.findByEmail(body.email!!)
        if (!teacher.comparePassword(body.password!!)) {
            return ResponseEntity(MessageDTO("Password is incorrect"), HttpStatus.UNAUTHORIZED)
        }
        val issuer = teacher.getEmail()
        val role = teacher.getRole()
        addCookie(role, issuer, response)
        return ResponseEntity("You are logged in correctly", HttpStatus.OK)
    }

    private fun addCookie(role: String, issuer: String, response: HttpServletResponse) {
        val jwt = Jwts.builder()
            .claim("role", role)
            .setIssuer(issuer)
            .setExpiration(Date(System.currentTimeMillis() + 60 * 24 * 1000))
            .signWith(SignatureAlgorithm.HS512, "secret".encodeToByteArray())
            .compact()

        val cookie = Cookie("jwt", jwt)
        cookie.isHttpOnly = false
        response.addCookie(cookie)
    }
}