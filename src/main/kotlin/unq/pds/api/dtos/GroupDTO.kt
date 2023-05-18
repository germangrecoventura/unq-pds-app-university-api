package unq.pds.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import unq.pds.model.Group
import javax.validation.constraints.NotBlank

class GroupDTO {

    @NotBlank(message = "Name cannot be empty")
    @Schema(example = "Group 1")
    var name: String? = null

    fun fromDTOToModel() = Group(name!!)
}