package unq.pds.api.controllers

import io.swagger.v3.oas.annotations.*
import io.swagger.v3.oas.annotations.media.*
import io.swagger.v3.oas.annotations.responses.*
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import unq.pds.api.dtos.MatterDTO
import unq.pds.model.Matter
import unq.pds.services.MatterService
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@CrossOrigin
@RequestMapping("matters")
class MatterController(private val matterService: MatterService) {

    @PostMapping
    @Operation(
        summary = "Registers a matter",
        description = "Registers a matter using the name as the unique identifier",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Matter::class),
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "bad request",
                content = [Content(
                    mediaType = "application/json", examples = [ExampleObject(
                        value = "{\n" +
                                "  \"additionalProp1\": \"string\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun createMatter(@RequestBody @Valid matter: MatterDTO): ResponseEntity<Matter> {
        val matterSaved = matterService.save(matter.fromDTOToModel())
        return ResponseEntity(matterSaved, HttpStatus.OK)
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
                description = "success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Matter::class),
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "bad request",
                content = [Content(
                    mediaType = "application/json", examples = [ExampleObject(
                        value = "{\n" +
                                "  \"message\": \"Not found matter with id\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun getMatter(@NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        return try {
            ResponseEntity(matterService.recover(id), HttpStatus.OK)
        } catch (e: Exception) {
            ResponseEntity(
                "{\n" +
                        "  \"message\": \"Not found matter with id\"\n" +
                        "}", HttpStatus.BAD_REQUEST
            )
        }
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
                description = "success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Matter::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "bad request",
                content = [Content(
                    mediaType = "application/json", examples = [ExampleObject(
                        value = "{\n" +
                                "  \"message\": \"string\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun updateMatter(@RequestBody matter: Matter): ResponseEntity<Any> {
        return try {
            ResponseEntity(matterService.update(matter), HttpStatus.OK)
        } catch (e: Exception) {
            ResponseEntity(
                "{\n" +
                        "  \"matter\": \"Not found matter with id\"\n" +
                        "}", HttpStatus.BAD_REQUEST
            )
        }
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
                description = "success",
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
                description = "bad request",
                content = [Content(
                    mediaType = "application/json", examples = [ExampleObject(
                        value = "{\n" +
                                "  \"message\": \"Not found matter with id\"\n" +
                                "}"
                    )]
                )]
            )]
    )
    fun deleteMatter(@NotBlank @RequestParam id: Long): ResponseEntity<Any> {
        return try {
            matterService.delete(id)
            ResponseEntity(
                "{\n" +
                        "  \"message\": \"Matter has been deleted successfully\"\n" +
                        "}", HttpStatus.OK
            )
        } catch (e: Exception) {
            ResponseEntity(
                "{\n" +
                        "  \"message\": \"Not found matter with id\"\n" +
                        "}", HttpStatus.BAD_REQUEST
            )
        }
    }
}