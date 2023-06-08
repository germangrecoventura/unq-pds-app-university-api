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
import unq.pds.api.dtos.MessageDTO
import unq.pds.api.dtos.ProjectDTO
import unq.pds.model.Project
import unq.pds.services.CommissionService
import unq.pds.services.ProjectService
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@CrossOrigin
@RequestMapping("projects")
class ProjectController(
    private val projectService: ProjectService,
    private val commissionService: CommissionService
) {
    private val messageNotAuthenticated = MessageDTO("It is not authenticated. Please log in")
    private val messageNotAccess = MessageDTO("You do not have permissions to access this resource")

    @PostMapping
    @Operation(
        summary = "Registers a project",
        description = "Registers a project",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Project::class),
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
    fun createProject(@CookieValue("jwt") jwt: String?, @RequestBody @Valid project: ProjectDTO): ResponseEntity<Any> {
        if (!existJWT(jwt)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        return ResponseEntity(projectService.save(project.fromDTOToModel()), HttpStatus.OK)
    }

    @GetMapping
    @Operation(
        summary = "Get a project",
        description = "Get a project using the id as the unique identifier",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Project::class),
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
                                "  \"message\": \"There is no project with that id\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun getProject(@CookieValue("jwt") jwt: String?, @NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        if (!existJWT(jwt)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        return ResponseEntity(projectService.read(id), HttpStatus.OK)
    }

    @PutMapping
    @Operation(
        summary = "Update a project",
        description = "Update a project",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Project::class)
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
                                "  \"message\": \"Project does not exist\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun updateProject(@CookieValue("jwt") jwt: String?, @RequestBody project: ProjectDTO): ResponseEntity<Any> {
        if (!existJWT(jwt)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
        if ((isStudent(body) && !projectService.thereIsAGroupWhereIsStudentAndTheProjectExists(
                body.issuer!!,
                project.id!!
            ) || (isTeacher(body) && !projectService.thereIsACommissionWhereIsteacherAndTheProjectExists(
                body.issuer!!,
                project.id!!
            )
                    ))
        ) return ResponseEntity(messageNotAccess, HttpStatus.UNAUTHORIZED)
        return ResponseEntity(projectService.update(project), HttpStatus.OK)
    }

    @DeleteMapping
    @Operation(
        summary = "Delete a project",
        description = "Delete a project using the id as the unique identifier",
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
                                    "  \"message\": \"Project has been deleted successfully\"\n" +
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
                                "  \"message\": \"There is no project with that id\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun deleteProject(@CookieValue("jwt") jwt: String?, @NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        if (!existJWT(jwt)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
        if (isNotAdmin(body)) return ResponseEntity(messageNotAccess, HttpStatus.UNAUTHORIZED)
        projectService.delete(id)
        return ResponseEntity(MessageDTO("Project has been deleted successfully"), HttpStatus.OK)
    }

    @PutMapping("/addRepository/{projectId}/{repositoryId}")
    @Operation(
        summary = "Add a repository to a project",
        description = "Add a repository to a project",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Project::class),
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
    fun addRepository(
        @CookieValue("jwt") jwt: String?,
        @NotBlank @PathVariable projectId: Long,
        @NotBlank @PathVariable repositoryId: Long
    ): ResponseEntity<Any> {
        if (!existJWT(jwt)) return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        return ResponseEntity(projectService.addRepository(projectId, repositoryId), HttpStatus.OK)
    }

    @GetMapping("/getAll")
    @Operation(
        summary = "Get all projects",
        description = "Get all projects",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        array = ArraySchema(schema = Schema(implementation = Project::class)),
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
        return ResponseEntity(projectService.readAll(), HttpStatus.OK)
    }

    private fun existJWT(jwt: String?): Boolean {
        return !jwt.isNullOrBlank()
    }

    private fun isNotAdmin(body: Claims): Boolean {
        return body["role"] != "ADMIN"
    }

    private fun isStudent(body: Claims): Boolean {
        return body["role"] == "STUDENT"
    }

    private fun isTeacher(body: Claims): Boolean {
        return body["role"] == "TEACHER"
    }
}