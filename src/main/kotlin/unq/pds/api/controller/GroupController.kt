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
import unq.pds.api.dtos.GroupDTO
import unq.pds.api.dtos.GroupUpdateDTO
import unq.pds.api.dtos.MessageDTO
import unq.pds.model.Group
import unq.pds.services.CommissionService
import unq.pds.services.GroupService
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@CrossOrigin
@RequestMapping("groups")
class GroupController(private val groupService: GroupService, private val commissionService: CommissionService) {
    private val messageNotAuthenticated = MessageDTO("It is not authenticated. Please log in")
    private val messageNotAccess = MessageDTO("You do not have permissions to access this resource")
    @PostMapping
    @Operation(
        summary = "Registers a group",
        description = "Registers a group",
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
                                "  \"name\": \"Name cannot be empty\"\n" +
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
    fun createGroup(@CookieValue("jwt") jwt: String?, @RequestBody @Valid group: GroupDTO): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        }
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
                                "  \"message\": \"There is no group with that id\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun getGroup(@CookieValue("jwt") jwt: String?, @NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        }
        return ResponseEntity(groupService.read(id), HttpStatus.OK)
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
                description = "Success",
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
                                "  \"message\": \"Name cannot be empty\"\n" +
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
                                "  \"message\": \"Group does not exist\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun updateGroup(@CookieValue("jwt") jwt: String?, @RequestBody groupUpdateDTO: GroupUpdateDTO): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        }
        val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
        return if (body["role"] == "STUDENT" && !groupService.hasAMemberWithEmail(groupUpdateDTO.id!!, body.issuer))
            ResponseEntity(
                messageNotAccess,
                HttpStatus.UNAUTHORIZED
            ) else if (body["role"] == "TEACHER" &&
                !commissionService.thereIsACommissionWithATeacherWithEmailAndGroupWithId(body.issuer, groupUpdateDTO.id!!)) {
            ResponseEntity(
                messageNotAccess,
                HttpStatus.UNAUTHORIZED
            )
        } else ResponseEntity(groupService.update(groupUpdateDTO), HttpStatus.OK)
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
                description = "Success",
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
                                "  \"message\": \"There is no group with that id\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun deleteGroup(@CookieValue("jwt") jwt: String?, @NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        }
        val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
        if (body["role"] != "ADMIN") return ResponseEntity(
            messageNotAccess,
            HttpStatus.UNAUTHORIZED
        )
        groupService.delete(id)
        return ResponseEntity(MessageDTO("Group has been deleted successfully"), HttpStatus.OK)
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
                                "  \"message\": \"string\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun addMember(
        @CookieValue("jwt") jwt: String?,
        @NotBlank @PathVariable groupId: Long,
        @NotBlank @PathVariable studentId: Long
    ): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        }
        val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
        return if (body["role"] == "STUDENT" && !groupService.hasAMemberWithEmail(groupId, body.issuer)
            && body["id"].toString().toLong() != studentId)
            ResponseEntity(
                messageNotAccess,
                HttpStatus.UNAUTHORIZED
            ) else if (body["role"] == "TEACHER" &&
                    !commissionService.thereIsACommissionWithATeacherWithEmailAndGroupWithId(body.issuer, groupId)) {
            ResponseEntity(
                messageNotAccess,
                HttpStatus.UNAUTHORIZED
            )
        } else ResponseEntity(groupService.addMember(groupId, studentId), HttpStatus.OK)
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
                                "  \"message\": \"string\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun removeMember(
        @CookieValue("jwt") jwt: String?,
        @NotBlank @PathVariable groupId: Long,
        @NotBlank @PathVariable studentId: Long
    ): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        }
        val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
        return if (body["role"] == "STUDENT" && !groupService.hasAMemberWithEmail(groupId, body.issuer))
            ResponseEntity(
                messageNotAccess,
                HttpStatus.UNAUTHORIZED
            ) else if (body["role"] == "TEACHER" &&
                !commissionService.thereIsACommissionWithATeacherWithEmailAndGroupWithId(body.issuer, groupId)) {
            ResponseEntity(
                messageNotAccess,
                HttpStatus.UNAUTHORIZED
            )
        } else ResponseEntity(groupService.removeMember(groupId, studentId), HttpStatus.OK)
    }

    @PutMapping("/addProject/{groupId}/{projectId}")
    @Operation(
        summary = "Add a project to a group",
        description = "Add a project to a group",
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
                                "  \"message\": \"string\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun addProject(
        @CookieValue("jwt") jwt: String?,
        @NotBlank @PathVariable groupId: Long,
        @NotBlank @PathVariable projectId: Long
    ): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        }
        val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
        return if (body["role"] == "STUDENT" && !groupService.hasAMemberWithEmail(groupId, body.issuer))
            ResponseEntity(
                messageNotAccess,
                HttpStatus.UNAUTHORIZED
            ) else if (body["role"] == "TEACHER" &&
                    !commissionService.thereIsACommissionWithATeacherWithEmailAndGroupWithId(body.issuer, groupId)) {
            ResponseEntity(
                messageNotAccess,
                HttpStatus.UNAUTHORIZED
            )
        } else ResponseEntity(groupService.addProject(groupId, projectId), HttpStatus.OK)
    }

    @GetMapping("/getAll")
    @Operation(
        summary = "Get all groups",
        description = "Get all groups",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        array = ArraySchema(schema = Schema(implementation = Group::class)),
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
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        }
        return ResponseEntity(groupService.readAll(), HttpStatus.OK)
    }
}