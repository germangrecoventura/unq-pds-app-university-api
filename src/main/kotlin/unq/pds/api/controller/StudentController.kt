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
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import unq.pds.api.dtos.MessageDTO
import unq.pds.api.dtos.StudentCreateRequestDTO
import unq.pds.model.Student
import unq.pds.services.StudentService
import javax.validation.Valid
import javax.validation.constraints.NotBlank

class StudentController {
    @RestController
    @CrossOrigin
    @RequestMapping("students")
    class StudentController(private val studentService: StudentService) {
        private val messageNotAuthenticated = MessageDTO("It is not authenticated. Please log in")
        private val messageNotAccess = MessageDTO("You do not have permissions to access this resource")

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
            @CookieValue("jwt") jwt: String?,
            @RequestBody @Valid student: StudentCreateRequestDTO
        ): ResponseEntity<Any> {
            if (!existJWT(jwt)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
            val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
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
        fun getStudent(@CookieValue("jwt") jwt: String?, @NotBlank @RequestParam id: Long): ResponseEntity<Any> {
            if (!existJWT(jwt)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
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
            @CookieValue("jwt") jwt: String?,
            @RequestBody @Valid student: StudentCreateRequestDTO
        ): ResponseEntity<Any> {
            if (!existJWT(jwt)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
            val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
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
        fun deleteStudent(@CookieValue("jwt") jwt: String?, @NotBlank @RequestParam id: Long): ResponseEntity<Any> {
            if (!existJWT(jwt)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
            val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
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
        fun getAll(@CookieValue("jwt") jwt: String?): ResponseEntity<Any> {
            if (!existJWT(jwt)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
            return ResponseEntity(studentService.readAll(), HttpStatus.OK)
        }

        private fun existJWT(jwt: String?): Boolean {
            return !jwt.isNullOrBlank()
        }

        private fun isNotAdmin(body: Claims): Boolean {
            return body["role"] != "ADMIN"
        }

        private fun isStudent(body: Claims): Boolean {
            return body["role"] == "STUDENT"
        }

        private fun isTeacher(body: Claims): Boolean {
            return body["role"] == "TEACHER"
        }

        private fun isSameId(body: Claims, student: StudentCreateRequestDTO): Boolean {
            return body["id"].toString().toLong() == student.id
        }
    }
}