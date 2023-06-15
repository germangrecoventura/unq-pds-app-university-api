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
import unq.pds.api.dtos.CommentCreateRequestDTO
import unq.pds.api.dtos.MessageDTO
import unq.pds.api.dtos.TeacherCreateRequestDTO
import unq.pds.model.Comment
import unq.pds.model.Teacher
import unq.pds.security.JwtUtilService
import unq.pds.services.ProjectService
import unq.pds.services.TeacherService
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@CrossOrigin
@RequestMapping("teachers")
@SecurityRequirement(name = "bearerAuth")
class TeacherController(
    private val teacherService: TeacherService,
    private val projectService: ProjectService
) {
    private val messageNotAuthenticated = MessageDTO("It is not authenticated. Please log in")
    private val messageNotAccess = MessageDTO("You do not have permissions to access this resource")
    private val passwordEncrypt = JwtUtilService.JWT_SECRET_KEY

    @PostMapping
    @Operation(
        summary = "Registers a teacher",
        description = "Registers a teacher using the email address as the unique identifier",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Teacher::class),
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
            )]
    )
    fun createTeacher(
        request: HttpServletRequest,
        @RequestBody @Valid teacher: TeacherCreateRequestDTO
    ): ResponseEntity<Any> {
        var header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!existJWT(header)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        header = header.substring(7, header.length)
        val body = Jwts.parser().setSigningKey(passwordEncrypt).parseClaimsJws(header).body
        if (isNotAdmin(body)) return ResponseEntity(messageNotAccess, HttpStatus.UNAUTHORIZED)
        return ResponseEntity(teacherService.save(teacher), HttpStatus.OK)
    }


    @GetMapping
    @Operation(
        summary = "Get a teacher",
        description = "Get a teacher using the id as the unique identifier",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Teacher::class),
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
                                "  \"teacher\": \"Not found the teacher with id\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun getTeacher(request: HttpServletRequest, @NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!existJWT(header)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        return ResponseEntity(teacherService.findById(id), HttpStatus.OK)
    }

    @PutMapping
    @Operation(
        summary = "Update a teacher",
        description = "Update a teacher",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Teacher::class)
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
                                "  \"teacher\": \"Not found the teacher with id\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun updateTeacher(request: HttpServletRequest, @RequestBody teacher: Teacher): ResponseEntity<Any> {
        var header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!existJWT(header)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        header = header.substring(7, header.length)
        val body = Jwts.parser().setSigningKey(passwordEncrypt).parseClaimsJws(header).body
        return if (isStudent(body) || (isTeacher(body) && !isSameId(body, teacher)))
            ResponseEntity(messageNotAccess, HttpStatus.UNAUTHORIZED)
        else ResponseEntity(teacherService.update(teacher), HttpStatus.OK)
    }

    @DeleteMapping
    @Operation(
        summary = "Delete a teacher",
        description = "Delete a teacher using the id as the unique identifier",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json", examples = [ExampleObject(
                            value = "{\n" +
                                    "  \"message\": \"Teacher has been deleted successfully\"\n" +
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
                                "  \"teacher\": \"Required request parameter 'id' for method parameter type long is not present\"\n" +
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
                                "  \"message\": \"The teacher with id is not registered\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun deleteTeacher(request: HttpServletRequest, @NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        var header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!existJWT(header)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        header = header.substring(7, header.length)
        val body = Jwts.parser().setSigningKey(passwordEncrypt).parseClaimsJws(header).body
        if (isNotAdmin(body)) return ResponseEntity(messageNotAccess, HttpStatus.UNAUTHORIZED)
        teacherService.deleteById(id)
        return ResponseEntity(MessageDTO("Teacher has been deleted successfully"), HttpStatus.OK)
    }

    @GetMapping("/getAll")
    @Operation(
        summary = "Get all teachers",
        description = "Get all teachers",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        array = ArraySchema(schema = Schema(implementation = Teacher::class)),
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
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!existJWT(header)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        return ResponseEntity(teacherService.readAll(), HttpStatus.OK)
    }


    @PostMapping("/addComment")
    @Operation(
        summary = "Add comment to repository",
        description = "Add comment to repository",
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
    fun addCommentToRepository(
        request: HttpServletRequest,
        @RequestBody @Valid comment:
        CommentCreateRequestDTO
    ): ResponseEntity<Any> {
        var header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!existJWT(header)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        header = header.substring(7, header.length)
        val body = Jwts.parser().setSigningKey(passwordEncrypt).parseClaimsJws(header).body
        return if (isStudent(body) ||
            (isTeacher(body) && !projectService.thereIsACommissionWhereIsteacherAndTheRepositoryExists(
                body.subject!!,
                comment.repositoryId!!
            ))
        ) ResponseEntity(messageNotAccess, HttpStatus.UNAUTHORIZED)
        else ResponseEntity(teacherService.addCommentToRepository(comment), HttpStatus.OK)
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

    private fun isSameId(body: Claims, teacher: Teacher): Boolean {
        return teacherService.findByEmail(body.subject).getId() == teacher.getId()
    }
}
