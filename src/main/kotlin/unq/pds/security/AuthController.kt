package unq.pds.security

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import unq.pds.api.controller.ControllerHelper
import unq.pds.api.dtos.MessageDTO
import unq.pds.services.AdminService
import unq.pds.services.StudentService
import unq.pds.services.TeacherService
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid
import kotlin.math.log

@RestController
@CrossOrigin
class AuthController : ControllerHelper() {

    @Autowired
    private lateinit var jwtUtilService: JwtUtilService

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var adminService: AdminService

    @Autowired
    private lateinit var studentService: StudentService

    @Autowired
    private lateinit var teacherService: TeacherService

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
        showLogger(
            "unq.pds.security.AuthController.login(LoginDTO)",
            listOf("$loginDTO")
        )
        val authentication =
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(loginDTO.email, loginDTO.password))
        SecurityContextHolder.getContext().authentication = authentication
        val token = jwtUtilService.generateToken(authentication)
        return ResponseEntity.ok().body(TokenInfo(token))
    }

    @PostMapping("/log-out")
    @SecurityRequirement(name = "bearerAuth")
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
    fun logout(request: HttpServletRequest): ResponseEntity<Any> {
        showLogger(
            "unq.pds.security.AuthController.logout()",
            listOf()
        )
        if (jwtDoesNotExistInTheHeader(request)) return ResponseEntity(
            messageNotAuthenticated,
            HttpStatus.UNAUTHORIZED
        )
        return ResponseEntity(MessageDTO("You successfully logged out"), HttpStatus.OK)
    }

    @GetMapping("user-logged")
    @SecurityRequirement(name = "bearerAuth")
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
    @Suppress("UNCHECKED_CAST")
    fun user(request: HttpServletRequest): ResponseEntity<Any> {
        showLogger(
            "unq.pds.security.AuthController.user()",
            listOf()
        )
        if (jwtDoesNotExistInTheHeader(request)) return ResponseEntity(
            messageNotAuthenticated,
            HttpStatus.UNAUTHORIZED
        )
        val body = bodyOfTheCurrentHeader()
        return when (body["role"] as List<String>) {
            listOf("STUDENT") -> ResponseEntity(studentService.findByEmail(body.subject), HttpStatus.OK)
            listOf("TEACHER") -> ResponseEntity(teacherService.findByEmail(body.subject), HttpStatus.OK)
            listOf("ADMIN") -> ResponseEntity(adminService.findByEmail(body.subject), HttpStatus.OK)
            else -> return ResponseEntity(
                MessageDTO("It is not an allowed role in the system"),
                HttpStatus.BAD_REQUEST
            )
        }
    }
}