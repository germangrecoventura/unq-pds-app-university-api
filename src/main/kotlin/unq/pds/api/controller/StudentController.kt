package unq.pds.api.controller

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*
import unq.pds.api.dtos.DeployInstanceCommentDTO
import unq.pds.api.dtos.MessageDTO
import unq.pds.api.dtos.StudentCreateRequestDTO
import unq.pds.model.Comment
import unq.pds.model.Student
import unq.pds.services.GroupService
import unq.pds.security.JwtUtilService
import unq.pds.services.StudentService
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@CrossOrigin
@RequestMapping("students")
@SecurityRequirement(name = "bearerAuth")
class StudentController(
    private val studentService: StudentService,
    private val groupService: GroupService
) {
    private val messageNotAuthenticated = MessageDTO("It is not authenticated. Please log in")
    private val messageNotAccess = MessageDTO("You do not have permissions to access this resource")
    private val passwordEncrypt = JwtUtilService.JWT_SECRET_KEY

    @PostMapping
    @Operation(
        summary = "Registers a student",
        description = "Registers a student using the email address as the unique identifier",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Student::class),
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request",
                content = [Content(
                    mediaType = "application/json", examples = [ExampleObject(
                        value = "{\n" +
                                "  \"additionalProp1\": \"string\",\n" +
                                "  \"additionalProp2\": \"string\",\n" +
                                "  \"additionalProp3\": \"string\"\n" +
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
                                "  \"message\": \"string\"\n" +
                                "}"
                    )]
                )
                ]
            )]
    )
    fun createStudent(
        @RequestBody @Valid student: StudentCreateRequestDTO,
        request: HttpServletRequest
    ): ResponseEntity<Any> {
        var header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!existJWT(header)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        header = header.substring(7, header.length)
        val body = Jwts.parser().setSigningKey(passwordEncrypt).parseClaimsJws(header).body
        return if (isNotAdmin(body)) ResponseEntity(messageNotAccess, HttpStatus.UNAUTHORIZED)
        else ResponseEntity(studentService.save(student), HttpStatus.OK)
    }

    @GetMapping
    @Operation(
        summary = "Get a student",
        description = "Get a student using the id as the unique identifier",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Student::class),
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request",
                content = [Content(
                    mediaType = "application/json", examples = [ExampleObject(
                        value = "{\n" +
                                "  \"message\": \"Required request parameter 'id' for method parameter type long is not present\"\n" +
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
                                "  \"message\": \"string\"\n" +
                                "}"
                    )]
                )
                ]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Not found",
                content = [Content(
                    mediaType = "application/json", examples = [ExampleObject(
                        value = "{\n" +
                                "  \"message\": \"Not found the student with id\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun getStudent(@NotBlank @RequestParam id: Long, request: HttpServletRequest): ResponseEntity<Any> {
        var header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!existJWT(header)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        return ResponseEntity(studentService.findById(id), HttpStatus.OK)
    }

    @PutMapping
    @Operation(
        summary = "Update a student",
        description = "Update a student",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Student::class)
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
            ),
            ApiResponse(
                responseCode = "401",
                description = "Not authenticated",
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
                responseCode = "404",
                description = "Not found",
                content = [Content(
                    mediaType = "application/json", examples = [ExampleObject(
                        value = "{\n" +
                                "  \"message\": \"Student does not exist\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun updateStudent(
        @RequestBody @Valid student: StudentCreateRequestDTO,
        request: HttpServletRequest
    ): ResponseEntity<Any> {
        var header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!existJWT(header)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        header = header.substring(7, header.length)
        val body = Jwts.parser().setSigningKey(passwordEncrypt).parseClaimsJws(header).body
        return if (isTeacher(body)) ResponseEntity(messageNotAccess, HttpStatus.UNAUTHORIZED)
        else if (isStudent(body) && !isSameId(body, student)) {
            ResponseEntity(
                MessageDTO("You do not have permissions to update students except yourself"),
                HttpStatus.UNAUTHORIZED
            )
        } else ResponseEntity(studentService.update(student), HttpStatus.OK)
    }

    @DeleteMapping
    @Operation(
        summary = "Delete a student",
        description = "Delete a student using the id as the unique identifier",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "success",
                content = [
                    Content(
                        mediaType = "application/json", examples = [ExampleObject(
                            value = "{\n" +
                                    "  \"message\": \"Student has been deleted successfully\"\n" +
                                    "}"
                        )]
                    )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request",
                content = [Content(
                    mediaType = "application/json", examples = [ExampleObject(
                        value = "{\n" +
                                "  \"message\": \"Required request parameter 'id' for method parameter type long is not present\"\n" +
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
                                "  \"message\": \"string\"\n" +
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
                                "  \"message\": \"The student with id is not registered\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun deleteStudent(@NotBlank @RequestParam id: Long, request: HttpServletRequest): ResponseEntity<Any> {
        var header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!existJWT(header)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        header = header.substring(7, header.length)
        val body = Jwts.parser().setSigningKey(passwordEncrypt).parseClaimsJws(header).body
        if (isNotAdmin(body)) return ResponseEntity(messageNotAccess, HttpStatus.UNAUTHORIZED)
        studentService.deleteById(id)
        return ResponseEntity(MessageDTO("Student has been deleted successfully"), HttpStatus.OK)
    }

    @GetMapping("/getAll")
    @Operation(
        summary = "Get all students",
        description = "Get all students",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        array = ArraySchema(schema = Schema(implementation = Student::class)),
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
                                "  \"message\": \"string\"\n" +
                                "}"
                    )]
                )
                ]
            )]
    )
    fun getAll(request: HttpServletRequest): ResponseEntity<Any> {
        var header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!existJWT(header)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        return ResponseEntity(studentService.readAll(), HttpStatus.OK)
    }

    @PostMapping("/addComment")
    @Operation(
        summary = "Add comment to deploy instance",
        description = "Add comment to deploy instance",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Comment::class),
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request",
                content = [Content(
                    mediaType = "application/json", examples = [ExampleObject(
                        value = "{\n" +
                                "  \"additionalProp1\": \"string\",\n" +
                                "  \"additionalProp2\": \"string\",\n" +
                                "  \"additionalProp3\": \"string\"\n" +
                                "}"
                    )]
                )]
            ), ApiResponse(
                responseCode = "401",
                description = "Not authenticated",
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
            )]
    )
    fun addCommentToDeployInstance(
        @RequestBody @Valid comment: DeployInstanceCommentDTO,
        request: HttpServletRequest
    ): ResponseEntity<Any> {
        var header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!existJWT(header)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        header = header.substring(7, header.length)
        val body = Jwts.parser().setSigningKey(passwordEncrypt).parseClaimsJws(header).body
        return if (isTeacher(body) ||
            (isStudent(body) && !groupService.thereIsAGroupWhereIsStudentAndTheDeployInstanceExists(
                body.subject,
                comment.deployInstanceId!!
            ))
        ) ResponseEntity(messageNotAccess, HttpStatus.UNAUTHORIZED)
        else ResponseEntity(studentService.addCommentToDeployInstance(comment), HttpStatus.OK)
    }

    private fun existJWT(jwt: String?): Boolean {
        return StringUtils.hasText(jwt) &&
                jwt!!.startsWith("Bearer ")
                && !jwt.substring(7, jwt.length).isNullOrEmpty()
    }

    private fun isNotAdmin(body: Claims): Boolean {
        val role = body["role"] as List<String>
        return !role.contains("ADMIN")
    }

    private fun isStudent(body: Claims): Boolean {
        val role = body["role"] as List<String>
        return role.contains("STUDENT")
    }

    private fun isTeacher(body: Claims): Boolean {
        val role = body["role"] as List<String>
        return role.contains("TEACHER")
    }

    private fun isSameId(body: Claims, student: StudentCreateRequestDTO): Boolean {
        return studentService.findByEmail(body.subject).getId() == student.id
    }
}
