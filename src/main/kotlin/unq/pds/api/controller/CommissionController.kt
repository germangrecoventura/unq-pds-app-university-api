package unq.pds.api.controller

import io.swagger.v3.oas.annotations.*
import io.swagger.v3.oas.annotations.media.*
import io.swagger.v3.oas.annotations.responses.*
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import unq.pds.api.dtos.CommissionDTO
import unq.pds.api.dtos.MessageDTO
import unq.pds.model.Commission
import unq.pds.services.CommissionService
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@CrossOrigin
@RequestMapping("commissions")
class CommissionController(private val commissionService: CommissionService) {

    @PostMapping
    @Operation(
        summary = "Registers a commission",
        description = "Registers a commission",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Commission::class),
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
                                "  \"message\": \"There is no matter with that name\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun createCommission(@RequestBody @Valid commission: CommissionDTO): ResponseEntity<Any> {
        return try {
            ResponseEntity(commissionService.save(commission.fromDTOToModel()), HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(MessageDTO(e.message!!), HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping
    @Operation(
        summary = "Get a commission",
        description = "Get a commission using the id as the unique identifier",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Commission::class),
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
                                "  \"message\": \"There is no commission with that id\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun getCommission(@NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        return try {
            ResponseEntity(commissionService.read(id), HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(MessageDTO(e.message!!), HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping
    @Operation(
        summary = "Update a commission",
        description = "Update a commission",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Commission::class)
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
                                "  \"message\": \"Commission does not exist\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun updateCommission(@RequestBody commission: Commission): ResponseEntity<Any> {
        return try {
            ResponseEntity(commissionService.update(commission), HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(MessageDTO(e.message!!), HttpStatus.NOT_FOUND)
        }
    }

    @DeleteMapping
    @Operation(
        summary = "Delete a commission",
        description = "Delete a commission using the id as the unique identifier",
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
                                    "  \"message\": \"Commission has been deleted successfully\"\n" +
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
                                "  \"message\": \"There is no commission with that id\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun deleteCommission(@NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        return try {
            commissionService.delete(id)
            ResponseEntity(MessageDTO("Commission has been deleted successfully"), HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(MessageDTO(e.message!!), HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping("/addStudent/{commissionId}/{studentId}")
    @Operation(
        summary = "Add a student to a commission",
        description = "Add a student to a commission",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Commission::class),
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
    fun addStudent(@NotBlank @PathVariable commissionId: Long, @NotBlank @PathVariable studentId: Long): ResponseEntity<Any> {
        return try {
            ResponseEntity(commissionService.addStudent(commissionId, studentId), HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(MessageDTO(e.message!!), HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping("/removeStudent/{commissionId}/{studentId}")
    @Operation(
        summary = "Remove a student of a commission",
        description = "Remove a student of a commission",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Commission::class),
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
    fun removeStudent(@NotBlank @PathVariable commissionId: Long, @NotBlank @PathVariable studentId: Long): ResponseEntity<Any> {
        return try {
            ResponseEntity(commissionService.removeStudent(commissionId, studentId), HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(MessageDTO(e.message!!), HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping("/addTeacher/{commissionId}/{teacherId}")
    @Operation(
        summary = "Add a teacher to a commission",
        description = "Add a teacher to a commission",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Commission::class),
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
    fun addTeacher(@NotBlank @PathVariable commissionId: Long, @NotBlank @PathVariable teacherId: Long): ResponseEntity<Any> {
        return try {
            ResponseEntity(commissionService.addTeacher(commissionId, teacherId), HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(MessageDTO(e.message!!), HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping("/removeTeacher/{commissionId}/{teacherId}")
    @Operation(
        summary = "Remove a teacher of a commission",
        description = "Remove a teacher of a commission",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Commission::class),
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
    fun removeTeacher(@NotBlank @PathVariable commissionId: Long, @NotBlank @PathVariable teacherId: Long): ResponseEntity<Any> {
        return try {
            ResponseEntity(commissionService.removeTeacher(commissionId, teacherId), HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(MessageDTO(e.message!!), HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping("/addGroup/{commissionId}/{groupId}")
    @Operation(
        summary = "Add a group to a commission",
        description = "Add a group to a commission",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Commission::class),
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
    fun addGroup(@NotBlank @PathVariable commissionId: Long, @NotBlank @PathVariable groupId: Long): ResponseEntity<Any> {
        return try {
            ResponseEntity(commissionService.addGroup(commissionId, groupId), HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(MessageDTO(e.message!!), HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping("/removeGroup/{commissionId}/{groupId}")
    @Operation(
        summary = "Remove a group of a commission",
        description = "Remove a group of a commission",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Commission::class),
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
    fun removeGroup(@NotBlank @PathVariable commissionId: Long, @NotBlank @PathVariable groupId: Long): ResponseEntity<Any> {
        return try {
            ResponseEntity(commissionService.removeGroup(commissionId, groupId), HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(MessageDTO(e.message!!), HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/getAll")
    @Operation(
        summary = "Get all commissions",
        description = "Get all commissions",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        array = ArraySchema(schema = Schema(implementation = Commission::class)),
                    )
                ]
            )]
    )
    fun getAll(): ResponseEntity<List<Commission>> {
        return ResponseEntity(commissionService.readAll(), HttpStatus.OK)
    }
}