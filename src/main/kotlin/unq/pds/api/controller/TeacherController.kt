package unq.pds.api.controller

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
import unq.pds.model.exceptions.AlreadyRegisteredException
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
        fun getTeacher(@NotBlank @RequestParam id: Long): ResponseEntity<Any> {
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
        fun updateTeacher(@RequestBody teacher: Teacher): ResponseEntity<Any> {
            return ResponseEntity(teacherService.update(teacher), HttpStatus.OK)
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
        fun deleteTeacher(@NotBlank @RequestParam id: Long): ResponseEntity<Any> {
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
        fun getAll(): ResponseEntity<List<Teacher>> {
            return ResponseEntity(teacherService.readAll(), HttpStatus.OK)
        }
    }
}