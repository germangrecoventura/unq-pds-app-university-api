package unq.pds.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import unq.pds.model.Matter
import javax.validation.constraints.Pattern

class MatterDTO{

    @Pattern(regexp = "[a-zA-Z0-9 ]+")
    @Schema(example = "Matematica")
    var name: String? = null

    fun fromDTOToModel() = Matter(name!!)
}