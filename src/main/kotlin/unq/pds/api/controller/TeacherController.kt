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
                    description = "success",
                    content = [
                        Content(
                            mediaType = "application/json",
                            schema = Schema(implementation = Teacher::class),
                        )
                    ]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "bad request",
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
        fun createUser(@RequestBody @Valid teacher: TeacherCreateRequestDTO): ResponseEntity<Teacher> {
            var userSaved = teacherService.save(teacher)
            return ResponseEntity(userSaved, HttpStatus.OK)
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
                    description = "success",
                    content = [
                        Content(
                            mediaType = "application/json",
                            schema = Schema(implementation = Teacher::class),
                        )
                    ]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "bad request",
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
                ResponseEntity(teacherService.findById(id), HttpStatus.OK)
            } catch (e: Exception) {
                ResponseEntity(
                    "{\n" +
                            "  \"student\": \"Not found teacher with id\"\n" +
                            "}", HttpStatus.BAD_REQUEST
                )
            }
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
                    description = "success",
                    content = [
                        Content(
                            mediaType = "application/json",
                            schema = Schema(implementation = Teacher::class)
                        )
                    ]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "bad request",
                    content = [Content(
                        mediaType = "application/json", examples = [ExampleObject(
                            value = "{\n" +
                                    "  \"message\": \"string\"\n" +
                                    "}"
                        )]
                    )]
                )]
        )
        fun updateStudent(@RequestBody tea: Teacher): ResponseEntity<Any> {
            return try {
                ResponseEntity(teacherService.update(tea), HttpStatus.OK)
            } catch (e: Exception) {
                ResponseEntity(
                    "{\n" +
                            "  \"student\": \"Not found teacher with id\"\n" +
                            "}", HttpStatus.BAD_REQUEST
                )
            }
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
                    description = "success",
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
                    description = "bad request",
                    content = [Content(
                        mediaType = "application/json", examples = [ExampleObject(
                            value = "{\n" +
                                    "  \"student\": \"Not found Teacher with id\"\n" +
                                    "}"
                        )]
                    )]
                )]
        )
        fun deleteStudent(@NotBlank @RequestParam id: Long): ResponseEntity<Any> {
            return try {
                teacherService.deleteById(id)

                ResponseEntity(
                    "{\n" +
                            "  \"message\": \"Teacher has been deleted successfully\"\n" +
                            "}", HttpStatus.OK
                )
            } catch (e: Exception) {
                ResponseEntity(
                    "{\n" +
                            "  \"student\": \"Not found teacher with id\"\n" +
                            "}", HttpStatus.BAD_REQUEST
                )
            }
        }
    }
}