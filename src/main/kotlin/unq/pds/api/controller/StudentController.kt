package unq.pds.api.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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
        fun createUser(@RequestBody @Valid student: StudentCreateRequestDTO): ResponseEntity<Student> {
            var userSaved = studentService.save(student)
            return ResponseEntity(userSaved, HttpStatus.OK)
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
                                    "  \"student\": \"Not found student with id\"\n" +
                                    "}"
                        )]
                    )]
                )]
        )
        fun getStudent(@NotBlank @RequestParam id: Long): ResponseEntity<Any> {
            return try {
                ResponseEntity(studentService.findById(id), HttpStatus.OK)
            } catch (e: RuntimeException) {
                ResponseEntity(
                    "{\n" +
                            "  \"student\": \"Not found student with id\"\n" +
                            "}", HttpStatus.NOT_FOUND
                )
            }
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
                                    "  \"student\": \"Not found student with id\"\n" +
                                    "}"
                        )]
                    )]
                )]
        )
        fun updateStudent(@RequestBody student: Student): ResponseEntity<Any> {
            return try {
                ResponseEntity(studentService.update(student), HttpStatus.OK)
            } catch (e: Exception) {
                ResponseEntity(
                    "{\n" +
                            "  \"student\": \"Not found student with id ${student.getId()}\"\n" +
                            "}", HttpStatus.NOT_FOUND
                )
            }
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
                                    "  \"student\": \"Not found student with id\"\n" +
                                    "}"
                        )]
                    )]
                )]
        )
        fun deleteStudent(@NotBlank @RequestParam id: Long): ResponseEntity<Any> {
            return try {
                studentService.deleteById(id)

                ResponseEntity(
                    "{\n" +
                            "  \"message\": \"Student has been deleted successfully\"\n" +
                            "}", HttpStatus.OK
                )
            } catch (e: Exception) {
                ResponseEntity(
                    "{\n" +
                            "  \"student\": \"Not found student with id ${id}\"\n" +
                            "}", HttpStatus.NOT_FOUND
                )
            }
        }
    }
}