package unq.pds.api.controller

import io.swagger.v3.oas.annotations.*
import io.swagger.v3.oas.annotations.media.*
import io.swagger.v3.oas.annotations.responses.*
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import unq.pds.api.dtos.MessageDTO
import unq.pds.api.dtos.RepositoryDTO
import unq.pds.model.Repository
import unq.pds.services.RepositoryService
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@CrossOrigin
@RequestMapping("repositories")
class RepositoryController(private val repositoryService: RepositoryService) {

    @PostMapping
    @Operation(
        summary = "Registers a repository",
        description = "Registers a repository",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "success",
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
            )]
    )
    fun createRepository(@RequestBody @Valid repository: RepositoryDTO): ResponseEntity<Repository> {
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
    fun getRepository(@NotBlank @RequestParam id: Long): ResponseEntity<Any> {
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
                description = "success",
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
                responseCode = "404",
                description = "Not found",
                content = [Content(
                    mediaType = "application/json", examples = [ExampleObject(
                        value = "{\n" +
                                "  \"message\": \"Repository does not exist\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun updateRepository(@RequestBody @Valid repository: RepositoryDTO): ResponseEntity<Any> {
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
                description = "success",
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
    fun deleteRepository(@NotBlank @RequestParam id: Long): ResponseEntity<Any> {
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
            )]
    )
    fun getAll(): ResponseEntity<List<Repository>> {
        return ResponseEntity(repositoryService.findByAll(), HttpStatus.OK)
    }
}