package unq.pds.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import unq.pds.model.Matter

class MatterDTO{

    @Schema(example = "Matematica")
    var name: String? = null

    fun fromDTOToModel() = Matter(name!!)
}