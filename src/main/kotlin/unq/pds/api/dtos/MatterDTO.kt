package unq.pds.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import unq.pds.model.Matter
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

class MatterDTO {
    @NotBlank(message = "The first name cannot be blank")
    @Pattern(regexp = "[a-zA-Z ]+", message = "The first name cannot contain special characters or numbers")
    @Schema(example = "Matematica")
    var name: String? = null

    fun fromDTOToModel() : Matter{
        return Matter(name!!)
    }
}