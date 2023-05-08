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
import unq.pds.api.dtos.CommissionDTO
import unq.pds.api.dtos.MessageDTO
import unq.pds.model.Commission
import unq.pds.services.CommissionService
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@CrossOrigin
@RequestMapping("commissions")
class CommissionController(private val commissionService: CommissionService) {

    @PostMapping
    @Operation(
        summary = "Registers a commission",
        description = "Registers a commission",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Commission::class),
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
                                "  \"message\": \"There is no matter with that name\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun createCommission(
        @CookieValue("jwt") jwt: String?,
        @RequestBody @Valid commission: CommissionDTO
    ): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
        }
        val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
        return if (body["role"] != "ADMIN") ResponseEntity(
            MessageDTO("You do not have permissions to access this resource"),
            HttpStatus.UNAUTHORIZED
        )
        else ResponseEntity(commissionService.save(commission.fromDTOToModel()), HttpStatus.OK)
    }

    @GetMapping
    @Operation(
        summary = "Get a commission",
        description = "Get a commission using the id as the unique identifier",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Commission::class),
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
                                "  \"message\": \"There is no commission with that id\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun getCommission(@CookieValue("jwt") jwt: String?, @NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
        }
        return ResponseEntity(commissionService.read(id), HttpStatus.OK)
    }

    @DeleteMapping
    @Operation(
        summary = "Delete a commission",
        description = "Delete a commission using the id as the unique identifier",
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
                                    "  \"message\": \"Commission has been deleted successfully\"\n" +
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
                                "  \"message\": \"There is no commission with that id\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun deleteCommission(@CookieValue("jwt") jwt: String?, @NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
        }
        val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
        if (body["role"] != "ADMIN") return ResponseEntity(
            MessageDTO("You do not have permissions to access this resource"),
            HttpStatus.UNAUTHORIZED
        )
        commissionService.delete(id)
        return ResponseEntity(MessageDTO("Commission has been deleted successfully"), HttpStatus.OK)
    }

    @PutMapping("/addStudent/{commissionId}/{studentId}")
    @Operation(
        summary = "Add a student to a commission",
        description = "Add a student to a commission",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Commission::class),
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
    fun addStudent(
        @CookieValue("jwt") jwt: String?,
        @NotBlank @PathVariable commissionId: Long,
        @NotBlank @PathVariable studentId: Long
    ): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
        }
        val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
        return if (body["role"] == "STUDENT") ResponseEntity(
            MessageDTO("You do not have permissions to access this resource"),
            HttpStatus.UNAUTHORIZED
        ) else if (body["role"] == "TEACHER" && !commissionService.hasATeacherWithEmail(commissionId, body.issuer))
            ResponseEntity(
                MessageDTO("You do not have permissions to access this resource"),
                HttpStatus.UNAUTHORIZED
            )
        else ResponseEntity(commissionService.addStudent(commissionId, studentId), HttpStatus.OK)
    }

    @PutMapping("/removeStudent/{commissionId}/{studentId}")
    @Operation(
        summary = "Remove a student of a commission",
        description = "Remove a student of a commission",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Commission::class),
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
    fun removeStudent(
        @CookieValue("jwt") jwt: String?,
        @NotBlank @PathVariable commissionId: Long,
        @NotBlank @PathVariable studentId: Long
    ): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
        }
        val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
        return if (body["role"] == "STUDENT") ResponseEntity(
            MessageDTO("You do not have permissions to access this resource"),
            HttpStatus.UNAUTHORIZED
        ) else if (body["role"] == "TEACHER" && !commissionService.hasATeacherWithEmail(commissionId, body.issuer))
            ResponseEntity(
                MessageDTO("You do not have permissions to access this resource"),
                HttpStatus.UNAUTHORIZED
            )
        else ResponseEntity(commissionService.removeStudent(commissionId, studentId), HttpStatus.OK)
    }

    @PutMapping("/addTeacher/{commissionId}/{teacherId}")
    @Operation(
        summary = "Add a teacher to a commission",
        description = "Add a teacher to a commission",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Commission::class),
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
    fun addTeacher(
        @CookieValue("jwt") jwt: String?,
        @NotBlank @PathVariable commissionId: Long,
        @NotBlank @PathVariable teacherId: Long
    ): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
        }
        val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
        return if (body["role"] != "ADMIN") ResponseEntity(
            MessageDTO("You do not have permissions to access this resource"),
            HttpStatus.UNAUTHORIZED
        )
        else ResponseEntity(commissionService.addTeacher(commissionId, teacherId), HttpStatus.OK)
    }

    @PutMapping("/removeTeacher/{commissionId}/{teacherId}")
    @Operation(
        summary = "Remove a teacher of a commission",
        description = "Remove a teacher of a commission",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Commission::class),
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
    fun removeTeacher(
        @CookieValue("jwt") jwt: String?,
        @NotBlank @PathVariable commissionId: Long,
        @NotBlank @PathVariable teacherId: Long
    ): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
        }
        val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
        return if (body["role"] != "ADMIN") ResponseEntity(
            MessageDTO("You do not have permissions to access this resource"),
            HttpStatus.UNAUTHORIZED
        )
        else ResponseEntity(commissionService.removeTeacher(commissionId, teacherId), HttpStatus.OK)
    }

    @PutMapping("/addGroup/{commissionId}/{groupId}")
    @Operation(
        summary = "Add a group to a commission",
        description = "Add a group to a commission",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Commission::class),
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
    fun addGroup(
        @CookieValue("jwt") jwt: String?,
        @NotBlank @PathVariable commissionId: Long,
        @NotBlank @PathVariable groupId: Long
    ): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
        }
        val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
        return if (body["role"] == "STUDENT") ResponseEntity(
            MessageDTO("You do not have permissions to access this resource"),
            HttpStatus.UNAUTHORIZED
        ) else if (body["role"] == "TEACHER" && !commissionService.hasATeacherWithEmail(commissionId, body.issuer))
            ResponseEntity(
                MessageDTO("You do not have permissions to access this resource"),
                HttpStatus.UNAUTHORIZED
            )
        else ResponseEntity(commissionService.addGroup(commissionId, groupId), HttpStatus.OK)
    }

    @PutMapping("/removeGroup/{commissionId}/{groupId}")
    @Operation(
        summary = "Remove a group of a commission",
        description = "Remove a group of a commission",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Commission::class),
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
    fun removeGroup(
        @CookieValue("jwt") jwt: String?,
        @NotBlank @PathVariable commissionId: Long,
        @NotBlank @PathVariable groupId: Long
    ): ResponseEntity<Any> {
        if (jwt.isNullOrBlank()) {
            return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
        }
        val body = Jwts.parser().setSigningKey("secret".encodeToByteArray()).parseClaimsJws(jwt).body
        return if (body["role"] == "STUDENT") ResponseEntity(
            MessageDTO("You do not have permissions to access this resource"),
            HttpStatus.UNAUTHORIZED
        ) else if (body["role"] == "TEACHER" && !commissionService.hasATeacherWithEmail(commissionId, body.issuer))
            ResponseEntity(
                MessageDTO("You do not have permissions to access this resource"),
                HttpStatus.UNAUTHORIZED
            )
        else ResponseEntity(commissionService.removeGroup(commissionId, groupId), HttpStatus.OK)
    }

    @GetMapping("/getAll")
    @Operation(
        summary = "Get all commissions",
        description = "Get all commissions",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = "application/json",
                        array = ArraySchema(schema = Schema(implementation = Commission::class)),
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
            return ResponseEntity(MessageDTO("It is not authenticated. Please log in"), HttpStatus.UNAUTHORIZED)
        }
        return ResponseEntity(commissionService.readAll(), HttpStatus.OK)
    }
}