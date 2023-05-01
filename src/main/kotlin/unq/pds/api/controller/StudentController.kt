package unq.pds.api.controller

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
                )]
        )
        fun createStudent(
            @CookieValue("jwt") jwt: String?,
            @RequestBody @Valid student: StudentCreateRequestDTO
        ): ResponseEntity<Any> {
            if (jwt.isNullOrBlank()) {
                return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
            }
            val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
            return if (body["role"] == "STUDENT") ResponseEntity(
                MessageDTO("You do not have permissions to access this resource"),
                HttpStatus.UNAUTHORIZED
            )
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
            if (jwt.isNullOrBlank()) {
                return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
            }
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
        fun updateStudent(@CookieValue("jwt") jwt: String?, @RequestBody student: Student): ResponseEntity<Any> {
            if (jwt.isNullOrBlank()) {
                return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
            }
            val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
            return if (body["role"] == "STUDENT") ResponseEntity(
                MessageDTO("You do not have permissions to access this resource"),
                HttpStatus.UNAUTHORIZED
            )
            else ResponseEntity(studentService.update(student), HttpStatus.OK)
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
            if (jwt.isNullOrBlank()) {
                return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
            }
            val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
            if (body["role"] == "STUDENT") return ResponseEntity(
                MessageDTO("You do not have permissions to access this resource"),
                HttpStatus.UNAUTHORIZED
            )
            studentService.deleteById(id)
            return ResponseEntity(MessageDTO("Student has been deleted successfully"), HttpStatus.OK)
        }

        @PutMapping("/addProject/{studentId}/{projectId}")
        @Operation(
            summary = "Add a project to a student",
            description = "Add a project to a student",
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
                                    "  \"message\": \"string\"\n" +
                                    "}"
                        )]
                    )]
                ),
                ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = [Content(
                        mediaType = "application/json", examples = [ExampleObject(
                            value = "{\n" +
                                    "  \"message\": \"string\"\n" +
                                    "}"
                        )]
                    )]
                )]
        )
        fun addProject(
            @CookieValue("jwt") jwt: String?,
            @NotBlank @PathVariable studentId: Long,
            @NotBlank @PathVariable projectId: Long
        ): ResponseEntity<Any> {
            if (jwt.isNullOrBlank()) {
                return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
            }
            val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
            return if (body["role"] == "STUDENT") ResponseEntity(
                MessageDTO("You do not have permissions to access this resource"),
                HttpStatus.UNAUTHORIZED
            )
            else ResponseEntity(studentService.addProject(studentId, projectId), HttpStatus.OK)
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
                )]
        )
        fun getAll(@CookieValue("jwt") jwt: String?): ResponseEntity<Any> {
            if (jwt.isNullOrBlank()) {
                return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
            }
            return ResponseEntity(studentService.readAll(), HttpStatus.OK)
        }
    }
}