package unq.pds.api.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import unq.pds.api.dtos.MessageDTO
import unq.pds.api.dtos.RepositoryDTO
import unq.pds.model.Commit
import unq.pds.model.Issue
import unq.pds.model.PullRequest
import unq.pds.model.Repository
import unq.pds.services.ProjectService
import unq.pds.services.RepositoryService
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@CrossOrigin
@RequestMapping("repositories")
@SecurityRequirement(name = "bearerAuth")
class RepositoryController(
    private val repositoryService: RepositoryService,
    private val projectService: ProjectService
): ControllerHelper() {

    @PostMapping
    @Operation(
        summary = "Registers a repository",
        description = "Registers a repository",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Repository::class),
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
                                "  \"message\": \"There is no project with that id\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun createRepository(
        request: HttpServletRequest,
        @RequestBody @Valid repository: RepositoryDTO
    ): ResponseEntity<Any> {
        showLogger(
            "unq.pds.api.controller.RepositoryController.createRepository(RepositoryDTO)",
            listOf("$repository")
        )
        if (jwtDoesNotExistInTheHeader(request)) return ResponseEntity(
            messageNotAuthenticated,
            HttpStatus.UNAUTHORIZED
        )
        val body = bodyOfTheCurrentHeader()
        if ((isStudent(body) && !projectService.thereIsAGroupWhereIsStudentAndTheProjectExists(
                body.subject!!,
                repository.projectId!!
            )) || (isTeacher(body) && !projectService.thereIsACommissionWhereIsteacherAndTheProjectExists(
                body.subject!!,
                repository.projectId!!
            ))
        ) return ResponseEntity(messageNotAccess, HttpStatus.UNAUTHORIZED)
        return ResponseEntity(repositoryService.save(repository), HttpStatus.OK)
    }

    @GetMapping
    @Operation(
        summary = "Get a repository",
        description = "Get a repository using the id as the unique identifier",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Repository::class),
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
                                "  \"message\": \"Not found the repository with id\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun getRepository(request: HttpServletRequest, @NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        showLogger(
            "unq.pds.api.controller.RepositoryController.getRepository(Long)",
            listOf("$id")
        )
        if (jwtDoesNotExistInTheHeader(request)) return ResponseEntity(
            messageNotAuthenticated,
            HttpStatus.UNAUTHORIZED
        )
        return ResponseEntity(repositoryService.findById(id), HttpStatus.OK)
    }

    @PutMapping
    @Operation(
        summary = "Update a repository",
        description = "Update a repository",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Repository::class)
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
    fun updateRepository(
        request: HttpServletRequest,
        @RequestBody @Valid repository: RepositoryDTO
    ): ResponseEntity<Any> {
        showLogger(
            "unq.pds.api.controller.RepositoryController.updateRepository(RepositoryDTO)",
            listOf("$repository")
        )
        if (jwtDoesNotExistInTheHeader(request)) return ResponseEntity(
            messageNotAuthenticated,
            HttpStatus.UNAUTHORIZED
        )
        val body = bodyOfTheCurrentHeader()
        if (!projectService.isFoundRepository(repository.projectId!!, repository.name!!))
            return ResponseEntity(MessageDTO("Not found the repository"), HttpStatus.NOT_FOUND)
        if ((isStudent(body) && !projectService.thereIsAGroupWhereIsStudentAndTheProjectExists(
                body.subject!!,
                repository.projectId!!
            )) || (isTeacher(body) && !projectService.thereIsACommissionWhereIsteacherAndTheProjectExists(
                body.subject!!,
                repository.projectId!!
            ))
        ) return ResponseEntity(messageNotAccess, HttpStatus.UNAUTHORIZED)
        return ResponseEntity(repositoryService.update(repository), HttpStatus.OK)
    }

    @DeleteMapping
    @Operation(
        summary = "Delete a repository",
        description = "Delete a repository using the id as the unique identifier",
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
                                    "  \"message\": \"Repository has been deleted successfully\"\n" +
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
                                "  \"message\": \"The repository with id is not registered\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun deleteRepository(request: HttpServletRequest, @NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        showLogger(
            "unq.pds.api.controller.RepositoryController.deleteRepository(Long)",
            listOf("$id")
        )
        if (jwtDoesNotExistInTheHeader(request)) return ResponseEntity(
            messageNotAuthenticated,
            HttpStatus.UNAUTHORIZED
        )
        val body = bodyOfTheCurrentHeader()
        if (isNotAdmin(body)) return ResponseEntity(
            MessageDTO("You do not have permissions to access this resource"), HttpStatus.UNAUTHORIZED
        )
        repositoryService.deleteById(id)
        return ResponseEntity(MessageDTO("Repository has been deleted successfully"), HttpStatus.OK)
    }

    @GetMapping("/getAll")
    @Operation(
        summary = "Get all repositories",
        description = "Get all repositories",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        array = ArraySchema(schema = Schema(implementation = Repository::class)),
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
        showLogger(
            "unq.pds.api.controller.RepositoryController.getAll()",
            listOf()
        )
        if (jwtDoesNotExistInTheHeader(request)) return ResponseEntity(
            messageNotAuthenticated,
            HttpStatus.UNAUTHORIZED
        )
        return ResponseEntity(repositoryService.findByAll(), HttpStatus.OK)
    }

    @GetMapping("/lengthPagesPaginatedCommit")
    @Operation(
        summary = "Number of commit pages",
        description = "Number of commit pages",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Int::class),
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
                description = "Not Found",
                content = [Content(
                    mediaType = "application/json", examples = [ExampleObject(
                        value = "{\n" +
                                "  \"message\": \"string\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun lengthPagesPaginatedCommit(
        request: HttpServletRequest,
        @NotBlank @RequestParam name: String,
        @NotBlank @RequestParam size: Int
    ): ResponseEntity<Any> {
        showLogger(
            "unq.pds.api.controller.RepositoryController.lengthPagesPaginatedCommit(String, Int)",
            listOf(name, "$size")
        )
        if (jwtDoesNotExistInTheHeader(request)) return ResponseEntity(
            messageNotAuthenticated,
            HttpStatus.UNAUTHORIZED
        )
        return ResponseEntity(repositoryService.lengthPagesPaginatedCommit(name, size), HttpStatus.OK)
    }

    @GetMapping("/pageCommit")
    @Operation(
        summary = "Get commit page",
        description = "Get commit page",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [Content(
                    mediaType = "application/json",
                    array = ArraySchema(schema = Schema(implementation = Commit::class)),
                )]
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
                description = "Not Found",
                content = [Content(
                    mediaType = "application/json", examples = [ExampleObject(
                        value = "{\n" +
                                "  \"message\": \"string\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun getPaginatedCommit(
        request: HttpServletRequest,
        @NotBlank @RequestParam name: String,
        @NotBlank @RequestParam page: Int,
        @NotBlank @RequestParam size: Int
    ): ResponseEntity<Any> {
        showLogger(
            "unq.pds.api.controller.RepositoryController.getPaginatedCommit(String, Int, Int)",
            listOf(name, "$page", "$size")
        )
        if (jwtDoesNotExistInTheHeader(request)) return ResponseEntity(
            messageNotAuthenticated,
            HttpStatus.UNAUTHORIZED
        )
        return ResponseEntity(repositoryService.findPaginatedCommit(name, page, size), HttpStatus.OK)
    }

    @GetMapping("/lengthPagesPaginatedIssue")
    @Operation(
        summary = "Number of issue pages",
        description = "Number of issue pages",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Int::class),
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
                description = "Not Found",
                content = [Content(
                    mediaType = "application/json", examples = [ExampleObject(
                        value = "{\n" +
                                "  \"message\": \"string\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun lengthPagesPaginatedIssue(
        request: HttpServletRequest,
        @NotBlank @RequestParam name: String,
        @NotBlank @RequestParam size: Int
    ): ResponseEntity<Any> {
        showLogger(
            "unq.pds.api.controller.RepositoryController.lengthPagesPaginatedIssue(String, Int)",
            listOf(name, "$size")
        )
        if (jwtDoesNotExistInTheHeader(request)) return ResponseEntity(
            messageNotAuthenticated,
            HttpStatus.UNAUTHORIZED
        )
        return ResponseEntity(repositoryService.lengthPagesPaginatedIssue(name, size), HttpStatus.OK)
    }

    @GetMapping("/pageIssue")
    @Operation(
        summary = "Get issue page",
        description = "Get issue page",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [Content(
                    mediaType = "application/json",
                    array = ArraySchema(schema = Schema(implementation = Issue::class)),
                )]
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
                description = "Not Found",
                content = [Content(
                    mediaType = "application/json", examples = [ExampleObject(
                        value = "{\n" +
                                "  \"message\": \"string\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun getPaginatedIssue(
        request: HttpServletRequest,
        @NotBlank @RequestParam name: String,
        @NotBlank @RequestParam page: Int,
        @NotBlank @RequestParam size: Int
    ): ResponseEntity<Any> {
        showLogger(
            "unq.pds.api.controller.RepositoryController.getPaginatedIssue(String, Int, Int)",
            listOf(name, "$page", "$size")
        )
        if (jwtDoesNotExistInTheHeader(request)) return ResponseEntity(
            messageNotAuthenticated,
            HttpStatus.UNAUTHORIZED
        )
        return ResponseEntity(repositoryService.findPaginatedIssue(name, page, size), HttpStatus.OK)
    }

    @GetMapping("/lengthPagesPaginatedPullRequest")
    @Operation(
        summary = "Number of pull request pages",
        description = "Number of pull request pages",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Int::class),
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
                description = "Not Found",
                content = [Content(
                    mediaType = "application/json", examples = [ExampleObject(
                        value = "{\n" +
                                "  \"message\": \"string\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun lengthPagesPaginatedPullRequest(
        request: HttpServletRequest,
        @NotBlank @RequestParam name: String,
        @NotBlank @RequestParam size: Int
    ): ResponseEntity<Any> {
        showLogger(
            "unq.pds.api.controller.RepositoryController.lengthPagesPaginatedPullRequest(String, Int)",
            listOf(name, "$size")
        )
        if (jwtDoesNotExistInTheHeader(request)) return ResponseEntity(
            messageNotAuthenticated,
            HttpStatus.UNAUTHORIZED
        )
        return ResponseEntity(repositoryService.lengthPagesPaginatedPullRequest(name, size), HttpStatus.OK)
    }

    @GetMapping("/pagePullRequest")
    @Operation(
        summary = "Get pull request page",
        description = "Get pull request page",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [Content(
                    mediaType = "application/json",
                    array = ArraySchema(schema = Schema(implementation = PullRequest::class)),
                )]
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
                description = "Not Found",
                content = [Content(
                    mediaType = "application/json", examples = [ExampleObject(
                        value = "{\n" +
                                "  \"message\": \"string\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun getPaginatedPullRequest(
        request: HttpServletRequest,
        @NotBlank @RequestParam name: String,
        @NotBlank @RequestParam page: Int,
        @NotBlank @RequestParam size: Int
    ): ResponseEntity<Any> {
        showLogger(
            "unq.pds.api.controller.RepositoryController.getPaginatedPullRequest(String, Int, Int)",
            listOf(name, "$page", "$size")
        )
        if (jwtDoesNotExistInTheHeader(request)) return ResponseEntity(
            messageNotAuthenticated,
            HttpStatus.UNAUTHORIZED
        )
        return ResponseEntity(repositoryService.findPaginatedPullRequest(name, page, size), HttpStatus.OK)
    }
}