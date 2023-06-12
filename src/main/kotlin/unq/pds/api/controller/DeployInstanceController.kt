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
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import unq.pds.api.dtos.DeployInstanceDTO
import unq.pds.api.dtos.MessageDTO
import unq.pds.model.DeployInstance
import unq.pds.services.DeployInstanceService
import unq.pds.services.GroupService
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@CrossOrigin
@RequestMapping("deployInstances")
class DeployInstanceController(
    private val deployInstanceService: DeployInstanceService,
    private val groupService: GroupService
) {

    private val messageNotAuthenticated = MessageDTO("It is not authenticated. Please log in")
    private val messageNotAccess = MessageDTO("You do not have permissions to access this resource")

    @PostMapping
    @Operation(
        summary = "Registers a deploy instance",
        description = "Registers a deploy instance",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = DeployInstance::class),
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
            )]
    )
    fun createDeployInstance(@CookieValue("jwt") jwt: String?, @RequestBody @Valid deployInstance: DeployInstanceDTO): ResponseEntity<Any> {
        if (!existJWT(jwt)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
        return if (isTeacher(body) || (isStudent(body) &&
                    !groupService.thereIsAGroupWithThisProjectAndThisMember(
                        deployInstance.projectId!!, body["id"].toString().toLong())))
            ResponseEntity(messageNotAccess, HttpStatus.UNAUTHORIZED)
        else ResponseEntity(deployInstanceService.save(deployInstance.fromDTOToModel()), HttpStatus.OK)
    }

    @GetMapping
    @Operation(
        summary = "Get a deploy instance",
        description = "Get a deploy instance using the id as the unique identifier",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = DeployInstance::class),
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
                                "  \"message\": \"There is no deploy instance with that id\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun getDeployInstance(@CookieValue("jwt") jwt: String?, @NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        if (!existJWT(jwt)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        return ResponseEntity(deployInstanceService.read(id), HttpStatus.OK)
    }

    @PutMapping
    @Operation(
        summary = "Update a deploy instance",
        description = "Update a deploy instance",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = DeployInstance::class)
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
                                "  \"message\": \"Deploy instance does not exist\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun updateDeployInstance(@CookieValue("jwt") jwt: String?, @RequestBody deployInstance: DeployInstanceDTO): ResponseEntity<Any> {
        if (!existJWT(jwt)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
        return if (isTeacher(body) || (isStudent(body) &&
                    !groupService.thereIsAGroupWhereIsStudentAndTheDeployInstanceExists(
                        body.issuer, deployInstance.id!!)))
            ResponseEntity(messageNotAccess, HttpStatus.UNAUTHORIZED)
        else ResponseEntity(deployInstanceService.update(deployInstance), HttpStatus.OK)
    }

    @DeleteMapping
    @Operation(
        summary = "Delete a deploy instance",
        description = "Delete a deploy instance using the id as the unique identifier",
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
                                    "  \"message\": \"Deploy instance has been deleted successfully\"\n" +
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
                                "  \"message\": \"There is no deploy instance with that id\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun deleteDeployInstance(@CookieValue("jwt") jwt: String?, @NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        if (!existJWT(jwt)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
        if (isTeacher(body) || (isStudent(body) &&
                    !groupService.thereIsAGroupWhereIsStudentAndTheDeployInstanceExists(
                        body.issuer, id)))
            return ResponseEntity(messageNotAccess, HttpStatus.UNAUTHORIZED)
        deployInstanceService.delete(id)
        return ResponseEntity(MessageDTO("Deploy instance has been deleted successfully"), HttpStatus.OK)
    }

    @GetMapping("/getAll")
    @Operation(
        summary = "Get all deploy instances",
        description = "Get all deploy instances",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        array = ArraySchema(schema = Schema(implementation = DeployInstance::class)),
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
    fun getAll(@CookieValue("jwt") jwt: String?): ResponseEntity<Any> {
        if (!existJWT(jwt)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        return ResponseEntity(deployInstanceService.readAll(), HttpStatus.OK)
    }

    private fun existJWT(jwt: String?): Boolean {
        return !jwt.isNullOrBlank()
    }

    private fun isTeacher(body: Claims): Boolean {
        return body["role"] == "TEACHER"
    }

    private fun isStudent(body: Claims): Boolean {
        return body["role"] == "STUDENT"
    }
}