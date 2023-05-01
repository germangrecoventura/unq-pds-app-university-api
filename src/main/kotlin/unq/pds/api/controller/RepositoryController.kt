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
    fun createRepository(
        @CookieValue("jwt") jwt: String?,
        @RequestBody @Valid repository: RepositoryDTO
    ): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
        }
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
    fun getRepository(@CookieValue("jwt") jwt: String?, @NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
        }
        val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
        return if (body["role"] == "STUDENT") ResponseEntity(
            MessageDTO("You do not have permissions to access this resource"),
            HttpStatus.UNAUTHORIZED
        )
        else ResponseEntity(repositoryService.findById(id), HttpStatus.OK)
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
    fun updateRepository(
        @CookieValue("jwt") jwt: String?,
        @RequestBody @Valid repository: RepositoryDTO
    ): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
        }
        val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
        return if (body["role"] == "STUDENT") ResponseEntity(
            MessageDTO("You do not have permissions to access this resource"),
            HttpStatus.UNAUTHORIZED
        )
        else ResponseEntity(repositoryService.update(repository), HttpStatus.OK)
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
    fun deleteRepository(@CookieValue("jwt") jwt: String?, @NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
        }
        val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
        if (body["role"] == "STUDENT") return ResponseEntity(
            MessageDTO("You do not have permissions to access this resource"),
            HttpStatus.UNAUTHORIZED
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
            )]
    )
    fun getAll(@CookieValue("jwt") jwt: String?): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
        }
        return ResponseEntity(repositoryService.findByAll(), HttpStatus.OK)
    }
}