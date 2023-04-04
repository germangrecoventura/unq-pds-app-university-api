package unq.pds.api.controllers

import io.swagger.v3.oas.annotations.*
import io.swagger.v3.oas.annotations.media.*
import io.swagger.v3.oas.annotations.responses.*
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import unq.pds.model.Matter
import unq.pds.services.MatterService
import javax.validation.Valid

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
    fun createMatter(@RequestBody @Valid matter: Matter): ResponseEntity<Matter> {
        val matterSaved = matterService.save(matter)
        return ResponseEntity(matterSaved, HttpStatus.OK)
    }
}