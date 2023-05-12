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
import unq.pds.api.dtos.MatterDTO
import unq.pds.api.dtos.MessageDTO
import unq.pds.model.Matter
import unq.pds.services.MatterService
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@CrossOrigin
@RequestMapping("matters")
class MatterController(private val matterService: MatterService) {
    private val messageNotAuthenticated = MessageDTO("It is not authenticated. Please log in")
    private val messageNotAccess = MessageDTO("You do not have permissions to access this resource")
    @PostMapping
    @Operation(
        summary = "Registers a matter",
        description = "Registers a matter using the name as the unique identifier",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Matter::class),
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
    fun createMatter(@CookieValue("jwt") jwt: String?, @RequestBody @Valid matter: MatterDTO): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        }
        val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
        return if (body["role"] != "ADMIN") ResponseEntity(
            messageNotAccess,
            HttpStatus.UNAUTHORIZED
        )
        else ResponseEntity(matterService.save(matter.fromDTOToModel()), HttpStatus.OK)
    }

    @GetMapping
    @Operation(
        summary = "Get a matter",
        description = "Get a matter using the id as the unique identifier",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Matter::class),
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
                                "  \"message\": \"There is no matter with that id\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun getMatter(@CookieValue("jwt") jwt: String?, @NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        }
        return ResponseEntity(matterService.read(id), HttpStatus.OK)
    }

    @PutMapping
    @Operation(
        summary = "Update a matter",
        description = "Update a matter",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Matter::class)
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
                                "  \"message\": \"Matter does not exist\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun updateMatter(@CookieValue("jwt") jwt: String?, @RequestBody matter: Matter): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        }
        val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
        return if (body["role"] != "ADMIN") ResponseEntity(
            messageNotAccess,
            HttpStatus.UNAUTHORIZED
        )
        else ResponseEntity(matterService.update(matter), HttpStatus.OK)
    }

    @DeleteMapping
    @Operation(
        summary = "Delete a matter",
        description = "Delete a matter using the id as the unique identifier",
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
                                    "  \"message\": \"Matter has been deleted successfully\"\n" +
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
                                "  \"message\": \"There is no matter with that id\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun deleteMatter(@CookieValue("jwt") jwt: String?, @NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        }
        val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
        if (body["role"] != "ADMIN") return ResponseEntity(
            messageNotAccess,
            HttpStatus.UNAUTHORIZED
        )

        matterService.delete(id)
        return ResponseEntity(MessageDTO("Matter has been deleted successfully"), HttpStatus.OK)
    }

    @GetMapping("/getAll")
    @Operation(
        summary = "Get all matters",
        description = "Get all matters",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        array = ArraySchema(schema = Schema(implementation = Matter::class)),
                    )
                ]
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
    fun getAll(@CookieValue("jwt") jwt: String?): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(messageNotAuthenticated, HttpStatus.UNAUTHORIZED)
        }
        return ResponseEntity(matterService.readAll(), HttpStatus.OK)
    }
}