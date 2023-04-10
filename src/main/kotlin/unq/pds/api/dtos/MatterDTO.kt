package unq.pds.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import unq.pds.model.Matter

class MatterDTO{
    @Schema(example = "1")
    var id: Long? = null

    @Schema(example = "Matematica")
    var name: String? = null

    companion object {
        fun fromModelToDTO(matter: Matter) = MatterDTO().apply { id = matter.id; name = matter.name }
    }
}