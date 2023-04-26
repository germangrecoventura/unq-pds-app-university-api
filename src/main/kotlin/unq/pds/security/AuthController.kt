package unq.pds.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import unq.pds.api.dtos.MessageDTO
import unq.pds.services.StudentService
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@CrossOrigin
class AuthController(private val studentService: StudentService) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody body: LoginDTO, response: HttpServletResponse): ResponseEntity<Any> {
        try {
            val student = studentService.findByEmail(body.email!!)
            if (!student.comparePassword(body.password!!)) {
                return ResponseEntity(MessageDTO("Password is incorrect"), HttpStatus.UNAUTHORIZED)
            }
            val issuer = student.getEmail().toString()
            val jwt = Jwts.builder()
                .setIssuer(issuer)
                .setExpiration(Date(System.currentTimeMillis() + 60 * 24 * 1000))
                .signWith(SignatureAlgorithm.HS512, "secret".encodeToByteArray())
                .compact()

            val cookie = Cookie("jwt", jwt)
            cookie.isHttpOnly = false
            response.addCookie(cookie)
            return ResponseEntity("You are logged in correctly", HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            return ResponseEntity(MessageDTO(e.message!!), HttpStatus.UNAUTHORIZED)
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
        return ResponseEntity(studentService.findByEmail(body.issuer), HttpStatus.OK)
    }
}