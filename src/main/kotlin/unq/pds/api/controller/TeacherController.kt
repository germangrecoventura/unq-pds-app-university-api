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
import unq.pds.api.dtos.TeacherCreateRequestDTO
import unq.pds.model.Teacher
import unq.pds.services.TeacherService
import javax.validation.Valid
import javax.validation.constraints.NotBlank

class TeacherController {
    @RestController
    @CrossOrigin
    @RequestMapping("teachers")
    class TeacherController(private val teacherService: TeacherService) {
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
                )]
        )
        fun createTeacher(
            @CookieValue("jwt") jwt: String?,
            @RequestBody @Valid teacher: TeacherCreateRequestDTO
        ): ResponseEntity<Any> {
            if (jwt.isNullOrBlank()) {
                return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
            }
            val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
            return if (body["role"] != "ADMIN") ResponseEntity(
                MessageDTO("You do not have permissions to access this resource"),
                HttpStatus.UNAUTHORIZED
            )
            else ResponseEntity(teacherService.save(teacher), HttpStatus.OK)
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
        fun getTeacher(@CookieValue("jwt") jwt: String?, @NotBlank @RequestParam id: Long): ResponseEntity<Any> {
            if (jwt.isNullOrBlank()) {
                return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
            }
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
        fun updateTeacher(@CookieValue("jwt") jwt: String?, @RequestBody teacher: Teacher): ResponseEntity<Any> {
            if (jwt.isNullOrBlank()) {
                return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
            }
            val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
            return if (body["role"] == "STUDENT") ResponseEntity(
                MessageDTO("You do not have permissions to access this resource"),
                HttpStatus.UNAUTHORIZED
            ) else if (body["role"] == "TEACHER" && body["id"] != teacher.getId()) {
                ResponseEntity(
                    MessageDTO("You do not have permissions to update teachers except yourself"),
                    HttpStatus.UNAUTHORIZED
                )
            } else ResponseEntity(teacherService.update(teacher), HttpStatus.OK)
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
        fun deleteTeacher(@CookieValue("jwt") jwt: String?, @NotBlank @RequestParam id: Long): ResponseEntity<Any> {
            if (jwt.isNullOrBlank()) {
                return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
            }
            val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
            if (body["role"] != "ADMIN") return ResponseEntity(
                MessageDTO("You do not have permissions to access this resource"),
                HttpStatus.UNAUTHORIZED
            )
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
                )]
        )
        fun getAll(@CookieValue("jwt") jwt: String?): ResponseEntity<Any> {
            if (jwt.isNullOrBlank()) {
                return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
            }
            return ResponseEntity(teacherService.readAll(), HttpStatus.OK)
        }
    }
}