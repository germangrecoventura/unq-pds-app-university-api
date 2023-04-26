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
import unq.pds.api.dtos.ErrorDTO
import unq.pds.api.dtos.ProjectDTO
import unq.pds.model.Project
import unq.pds.services.ProjectService
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@CrossOrigin
@RequestMapping("projects")
class ProjectController(private val projectService: ProjectService) {

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
            )]
    )
    fun createProject(@RequestBody @Valid project: ProjectDTO): ResponseEntity<Any> {
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
    fun getProject(@NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        return try {
            ResponseEntity(projectService.read(id), HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(ErrorDTO(e.message!!), HttpStatus.NOT_FOUND)
        }
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
    fun updateProject(@RequestBody project: Project): ResponseEntity<Any> {
        return try {
            ResponseEntity(projectService.update(project), HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(ErrorDTO(e.message!!), HttpStatus.NOT_FOUND)
        }
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
    fun deleteProject(@NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        return try {
            projectService.delete(id)
            ResponseEntity(
                "{\n" +
                        "  \"message\": \"Project has been deleted successfully\"\n" +
                        "}", HttpStatus.OK
            )
        } catch (e: NoSuchElementException) {
            ResponseEntity(ErrorDTO(e.message!!), HttpStatus.NOT_FOUND)
        }
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
    fun addRepository(@NotBlank @PathVariable projectId: Long, @NotBlank @PathVariable repositoryId: Long): ResponseEntity<Any> {
        return try {
            ResponseEntity(projectService.addRepository(projectId, repositoryId), HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(ErrorDTO(e.message!!), HttpStatus.NOT_FOUND)
        }
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
            )]
    )
    fun getAll(): ResponseEntity<List<Project>> {
        return ResponseEntity(projectService.readAll(), HttpStatus.OK)
    }
}