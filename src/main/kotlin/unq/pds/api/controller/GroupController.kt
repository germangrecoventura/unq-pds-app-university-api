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
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*
import unq.pds.api.dtos.GroupDTO
import unq.pds.api.dtos.GroupUpdateDTO
import unq.pds.api.dtos.MessageDTO
import unq.pds.model.Group
import unq.pds.security.JwtUtilService
import unq.pds.services.CommissionService
import unq.pds.services.GroupService
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@CrossOrigin
@RequestMapping("groups")
@SecurityRequirement(name = "bearerAuth")
class GroupController(private val groupService: GroupService, private val commissionService: CommissionService) {
    private val messageNotAuthenticated = MessageDTO("It is not authenticated. Please log in")
    private val messageNotAccess = MessageDTO("You do not have permissions to access this resource")
    private val passwordEncrypt = JwtUtilService.JWT_SECRET_KEY

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
    fun createGroup(request: HttpServletRequest, @RequestBody @Valid group: GroupDTO): ResponseEntity<Any> {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!existJWT(header)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        return ResponseEntity(groupService.save(group), HttpStatus.OK)
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
    fun getGroup(request: HttpServletRequest, @NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!existJWT(header)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
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
    fun updateGroup(
        request: HttpServletRequest,
        @RequestBody groupUpdateDTO: GroupUpdateDTO
    ): ResponseEntity<Any> {
        var header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!existJWT(header)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        header = header.substring(7, header.length)
        val body = Jwts.parser().setSigningKey(passwordEncrypt).parseClaimsJws(header).body
        return if ((isStudent(body) && !groupService.hasAMemberWithEmail(groupUpdateDTO.id!!, body.subject))
            ||
            (isTeacher(body) && !commissionService.thereIsACommissionWithATeacherWithEmailAndGroupWithId(
                body.subject,
                groupUpdateDTO.id!!
            ))
        )
            ResponseEntity(messageNotAccess, HttpStatus.UNAUTHORIZED)
        else ResponseEntity(groupService.update(groupUpdateDTO), HttpStatus.OK)
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
    fun deleteGroup(request: HttpServletRequest, @NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        var header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!existJWT(header)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        header = header.substring(7, header.length)
        val body = Jwts.parser().setSigningKey(passwordEncrypt).parseClaimsJws(header).body
        if (isNotAdmin(body)) return ResponseEntity(messageNotAccess, HttpStatus.UNAUTHORIZED)
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
        request: HttpServletRequest,
        @NotBlank @PathVariable groupId: Long,
        @NotBlank @PathVariable studentId: Long
    ): ResponseEntity<Any> {
        var header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!existJWT(header)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        header = header.substring(7, header.length)
        val body = Jwts.parser().setSigningKey(passwordEncrypt).parseClaimsJws(header).body
        return if ((isStudent(body) && !groupService.hasAMemberWithEmail(groupId, body.subject))
            ||
            (isTeacher(body) &&
                    !commissionService.thereIsACommissionWithATeacherWithEmailAndGroupWithId(body.subject, groupId))
        )
            ResponseEntity(messageNotAccess, HttpStatus.UNAUTHORIZED)
        else ResponseEntity(groupService.addMember(groupId, studentId), HttpStatus.OK)
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
        request: HttpServletRequest,
        @NotBlank @PathVariable groupId: Long,
        @NotBlank @PathVariable studentId: Long
    ): ResponseEntity<Any> {
        var header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!existJWT(header)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        header = header.substring(7, header.length)
        val body = Jwts.parser().setSigningKey(passwordEncrypt).parseClaimsJws(header).body
        return if ((isStudent(body) && !groupService.hasAMemberWithEmail(groupId, body.subject))
            ||
            (isTeacher(body) &&
                    !commissionService.thereIsACommissionWithATeacherWithEmailAndGroupWithId(body.subject, groupId))
        )
            ResponseEntity(messageNotAccess, HttpStatus.UNAUTHORIZED)
        else ResponseEntity(groupService.removeMember(groupId, studentId), HttpStatus.OK)
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
        request: HttpServletRequest,
        @NotBlank @PathVariable groupId: Long,
        @NotBlank @PathVariable projectId: Long
    ): ResponseEntity<Any> {
        var header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!existJWT(header)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        header = header.substring(7, header.length)
        val body = Jwts.parser().setSigningKey(passwordEncrypt).parseClaimsJws(header).body
        return if ((isStudent(body) && !groupService.hasAMemberWithEmail(groupId, body.subject))
            ||
            (isTeacher(body) &&
                    !commissionService.thereIsACommissionWithATeacherWithEmailAndGroupWithId(body.subject, groupId))
        )
            ResponseEntity(messageNotAccess, HttpStatus.UNAUTHORIZED)
        else ResponseEntity(groupService.addProject(groupId, projectId), HttpStatus.OK)
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
    fun getAll(request: HttpServletRequest): ResponseEntity<Any> {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!existJWT(header)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        return ResponseEntity(groupService.readAll(), HttpStatus.OK)
    }

    private fun isStudent(body: Claims): Boolean {
        val role = body["role"] as List<String>
        return role.contains("STUDENT")
    }

    private fun isTeacher(body: Claims): Boolean {
        val role = body["role"] as List<String>
        return role.contains("TEACHER")
    }

    private fun existJWT(jwt: String?): Boolean {
        return StringUtils.hasText(jwt) &&
                jwt!!.startsWith("Bearer ")
                && !jwt.substring(7, jwt.length).isNullOrEmpty()
    }

    private fun isNotAdmin(body: Claims): Boolean {
        val role = body["role"] as List<String>
        return !role.contains("ADMIN")
    }
}