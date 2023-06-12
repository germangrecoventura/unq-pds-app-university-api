package unq.pds.security

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@CrossOrigin
class AuthController(
) {
    @Autowired
    private lateinit var jwtUtilService: JwtUtilService

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager


    @PostMapping("/login")
    @Operation(
        summary = "Log in the page",
        description = "log in depending on the role in the system",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [Content(
                    mediaType = "application/json", examples = [ExampleObject(
                        value = "{\n" +
                                "  \"message\": \"You are logged in correctly\"\n" +
                                "}"
                    )]
                )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request",
                content = [Content(
                    mediaType = "application/json", examples = [ExampleObject(
                        value = "{\n" +
                                "  \"message\": \"string\"\n" +
                                "}"
                    )]
                )]
            ), ApiResponse(
                responseCode = "401",
                description = "Not authenticated",
                content = [Content(
                    mediaType = "application/json", examples = [ExampleObject(
                        value = "{\n" +
                                "  \"message\": \"Password is incorrect\"\n" +
                                "}"
                    )]
                )
                ]
            ), ApiResponse(
                responseCode = "404",
                description = "Not found",
                content = [Content(
                    mediaType = "application/json", examples = [ExampleObject(
                        value = "{\n" +
                                "  \"message\": \"Not found the user with email\"\n" +
                                "}"
                    )]
                )
                ]
            )]
    )
    fun login(@Valid @RequestBody loginDTO: LoginDTO): ResponseEntity<TokenInfo> {
        val authentication =
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(loginDTO.email, loginDTO.password))
        SecurityContextHolder.getContext().authentication = authentication
        val token = jwtUtilService.generateToken(authentication)
        return ResponseEntity.ok().body(TokenInfo(token))
    }
    /*
        fun login(@Valid @RequestBody body: LoginDTO, response: HttpServletResponse): ResponseEntity<Any> {
            val student = userService.findStudent(body.email!!)
            if (student.isPresent) {
                return studentLogin(student.get(), body.password!!, response)
            }
            val teacher = userService.findTeacher(body.email!!)
            if (teacher.isPresent) {
                return teacherLogin(teacher.get(), body.password!!, response)
            }
            val admin = userService.findAdmin(body.email!!)
            return if (admin.isEmpty) {
                ResponseEntity(MessageDTO("Not found the user with email ${body.email}"), HttpStatus.NOT_FOUND)
            } else {
                adminLogin(admin.get(), body.password!!, response)
            }
        }


        @PostMapping("/logout")
        @Operation(
            summary = "Log out the page",
            description = "Log out the system",
        )
        @ApiResponses(
            value = [
                ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = [Content(
                        mediaType = "application/json", examples = [ExampleObject(
                            value = "{\n" +
                                    "  \"message\": \"You successfully logged out\"\n" +
                                    "}"
                        )]
                    )
                    ]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = [Content(
                        mediaType = "application/json", examples = [ExampleObject(
                            value = "{\n" +
                                    "  \"message\": \"string\"\n" +
                                    "}"
                        )]
                    )
                    ]
                ),
                ApiResponse(
                    responseCode = "401",
                    description = "Not authenticated",
                    content = [Content(
                        mediaType = "application/json", examples = [ExampleObject(
                            value = "{\n" +
                                    "  \"message\": \"It is not authenticated. Please log in\"\n" +
                                    "}"
                        )]
                    )
                    ]
                )]
        )
        fun logout(@CookieValue("jwt") jwt: String?, response: HttpServletResponse): ResponseEntity<Any> {
            if (jwt.isNullOrBlank()) {
                return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
            }
            val cookie = Cookie("jwt", null)
            cookie.isHttpOnly = true
            cookie.maxAge = 0
            response.addCookie(cookie)
            return ResponseEntity(MessageDTO("You successfully logged out"), HttpStatus.OK)
        }

        @GetMapping("user-logged")
        @Operation(
            summary = "Get user logged",
            description = "Gets the user from the jwt",
        )
        @ApiResponses(
            value = [
                ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = [Content(
                        mediaType = "application/json", examples = [ExampleObject(
                            value = "User { ... }"
                        )]
                    )
                    ]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = [Content(
                        mediaType = "application/json", examples = [ExampleObject(
                            value = "{\n" +
                                    "  \"message\": \"It is not an allowed role in the system\"\n" +
                                    "}"
                        )]
                    )]
                ),
                ApiResponse(
                    responseCode = "401",
                    description = "Not authenticated",
                    content = [Content(
                        mediaType = "application/json", examples = [ExampleObject(
                            value = "{\n" +
                                    "  \"message\": \"It is not authenticated. Please log in\"\n" +
                                    "}"
                        )]
                    )
                    ]
                ),
                ApiResponse(
                    responseCode = "404",
                    description = "Not Found",
                    content = [Content(
                        mediaType = "application/json", examples = [ExampleObject(
                            value = "{\n" +
                                    "  \"message\": \"string\"\n" +
                                    "}"
                        )]
                    )
                    ]
                )
            ]
        )
        fun user(@CookieValue("jwt") jwt: String?): ResponseEntity<Any> {
            if (jwt.isNullOrBlank()) {
                return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
            }
            val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
            return when (body["role"]) {
                "STUDENT" -> ResponseEntity(studentService.findByEmail(body.issuer), HttpStatus.OK)
                "TEACHER" -> ResponseEntity(teacherService.findByEmail(body.issuer), HttpStatus.OK)
                "ADMIN" -> ResponseEntity(adminService.findByEmail(body.issuer), HttpStatus.OK)
                else -> return ResponseEntity(
                    MessageDTO("It is not an allowed role in the system"),
                    HttpStatus.BAD_REQUEST
                )
            }
        }

        private fun studentLogin(
            student: Student,
            password: String,
            response: HttpServletResponse
        ): ResponseEntity<Any> {
            if (!student.comparePassword(password)) {
                return ResponseEntity(MessageDTO("Password is incorrect"), HttpStatus.UNAUTHORIZED)
            }
            val issuer = student.getEmail().toString()
            val role = student.getRole()
            val id = student.getId()!!
            addCookie(id, role, issuer, response)

            return ResponseEntity(MessageDTO("You are logged in correctly"), HttpStatus.OK)
        }


        private fun teacherLogin(
            teacher: Teacher,
            password: String,
            response: HttpServletResponse
        ): ResponseEntity<Any> {
            if (!teacher.comparePassword(password)) {
                return ResponseEntity(MessageDTO("Password is incorrect"), HttpStatus.UNAUTHORIZED)
            }
            val issuer = teacher.getEmail()
            val role = teacher.getRole()
            val id = teacher.getId()!!
            addCookie(id, role, issuer!!, response)
            return ResponseEntity(MessageDTO("You are logged in correctly"), HttpStatus.OK)
        }

        private fun adminLogin(admin: Admin, password: String, response: HttpServletResponse): ResponseEntity<Any> {
            if (!admin.comparePassword(password)) {
                return ResponseEntity(MessageDTO("Password is incorrect"), HttpStatus.UNAUTHORIZED)
            }
            val issuer = admin.getEmail()!!
            val role = admin.getRole()
            val id = admin.getId()!!
            addCookie(id, role, issuer, response)
            return ResponseEntity(MessageDTO("You are logged in correctly"), HttpStatus.OK)
        }

        private fun addCookie(id: Long, role: String, issuer: String, response: HttpServletResponse) {
            val jwt = Jwts.builder()
                .claim("role", role)
                .claim("id", id)
                .setIssuer(issuer)
                .setExpiration(Date(System.currentTimeMillis() + 86400000))
                .signWith(SignatureAlgorithm.HS512, "secret".encodeToByteArray())
                .compact()

            val cookie = Cookie("jwt", jwt)
            cookie.isHttpOnly = false
            response.addCookie(cookie)
        }*/
}