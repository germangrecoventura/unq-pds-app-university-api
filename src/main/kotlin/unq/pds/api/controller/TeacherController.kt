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
        fun createTeacher(@RequestBody @Valid teacher: TeacherCreateRequestDTO): ResponseEntity<Teacher> {
            var teacherSaved = teacherService.save(teacher)
            return ResponseEntity(teacherSaved, HttpStatus.OK)
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
                                    "  \"teacher\": \"Not found teacher with id\"\n" +
                                    "}"
                        )]
                    )]
                )]
        )
        fun getTeacher(@NotBlank @RequestParam id: Long): ResponseEntity<Any> {
            return try {
                ResponseEntity(teacherService.findById(id), HttpStatus.OK)
            } catch (e: Exception) {
                ResponseEntity(
                    "{\n" +
                            "  \"teacher\": \"Not found teacher with id\"\n" +
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
                                    "  \"teacher\": \"Not found teacher with id\"\n" +
                                    "}"
                        )]
                    )]
                )]
        )
        fun updateTeacher(@RequestBody teacher: Teacher): ResponseEntity<Any> {
            return try {
                ResponseEntity(teacherService.update(teacher), HttpStatus.OK)
            } catch (e: Exception) {
                ResponseEntity(
                    "{\n" +
                            "  \"teacher\": \"Not found teacher with id\"\n" +
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
                                    "  \"teacher\": \"Not found teacher with id\"\n" +
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
                                    "  \"teacher\": \"Not found teacher with id\"\n" +
                                    "}"
                        )]
                    )]
                )]
        )
        fun deleteTeacher(@NotBlank @RequestParam id: Long): ResponseEntity<Any> {
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
                            "  \"teacher\": \"Not found teacher with id\"\n" +
                            "}", HttpStatus.BAD_REQUEST
                )
            }
        }
    }
}