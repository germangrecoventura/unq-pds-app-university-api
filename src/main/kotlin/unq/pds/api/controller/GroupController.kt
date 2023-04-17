package unq.pds.api.controller

import io.swagger.v3.oas.annotations.*
import io.swagger.v3.oas.annotations.media.*
import io.swagger.v3.oas.annotations.responses.*
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import unq.pds.api.dtos.ErrorDTO
import unq.pds.api.dtos.GroupDTO
import unq.pds.model.Group
import unq.pds.services.GroupService
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@CrossOrigin
@RequestMapping("groups")
class GroupController(private val groupService: GroupService) {

    @PostMapping
    @Operation(
        summary = "Registers a group",
        description = "Registers a group",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Group::class),
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request",
                content = [Content(
                    mediaType = "application/json", examples = [ExampleObject(
                        value = "{\n" +
                                "  \"name\": \"Name cannot be empty\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun createGroup(@RequestBody @Valid group: GroupDTO): ResponseEntity<Group> {
        return ResponseEntity(groupService.save(group.fromDTOToModel()), HttpStatus.OK)
    }

    @GetMapping
    @Operation(
        summary = "Get a group",
        description = "Get a group using the id as the unique identifier",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Group::class),
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
                                "  \"message\": \"There is no group with that id\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun getGroup(@NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        return try {
            ResponseEntity(groupService.read(id), HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(ErrorDTO(e.message!!), HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping
    @Operation(
        summary = "Update a group",
        description = "Update a group",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Group::class)
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
                                "  \"message\": \"Group does not exists\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun updateGroup(@RequestBody group: Group): ResponseEntity<Any> {
        return try {
            ResponseEntity(groupService.update(group), HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(ErrorDTO(e.message!!), HttpStatus.NOT_FOUND)
        }
    }

    @DeleteMapping
    @Operation(
        summary = "Delete a group",
        description = "Delete a group using the id as the unique identifier",
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
                                    "  \"message\": \"Group has been deleted successfully\"\n" +
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
                                "  \"message\": \"There is no group with that id\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun deleteMatter(@NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        return try {
            groupService.delete(id)
            ResponseEntity(
                "{\n" +
                        "  \"message\": \"Group has been deleted successfully\"\n" +
                        "}", HttpStatus.OK
            )
        } catch (e: NoSuchElementException) {
            ResponseEntity(ErrorDTO(e.message!!), HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping("/addMember/{groupId}/{studentId}")
    @Operation(
        summary = "Add a member to a group",
        description = "Add a member to a group",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Group::class),
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
    fun addMember(@NotBlank @PathVariable groupId: Long, @NotBlank @PathVariable studentId: Long): ResponseEntity<Any> {
        return try {
            ResponseEntity(groupService.addMember(groupId, studentId), HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(ErrorDTO(e.message!!), HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping("/removeMember/{groupId}/{studentId}")
    @Operation(
        summary = "Remove a member of a group",
        description = "Remove a member of a group",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Group::class),
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
    fun removeMember(@NotBlank @PathVariable groupId: Long, @NotBlank @PathVariable studentId: Long): ResponseEntity<Any> {
        return try {
            ResponseEntity(groupService.removeMember(groupId, studentId), HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(ErrorDTO(e.message!!), HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/getAll")
    fun getAll(): ResponseEntity<List<Group>> {
        return ResponseEntity(groupService.readAll(), HttpStatus.OK)
    }
}