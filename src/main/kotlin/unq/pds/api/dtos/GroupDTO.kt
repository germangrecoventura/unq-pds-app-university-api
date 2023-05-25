package unq.pds.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import unq.pds.model.Group
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

class GroupDTO {

    @NotBlank(message = "Name cannot be empty")
    @Schema(example = "Group 1")
    var name: String? = null

    @NotNull(message = "There must be at least one member")
    @NotEmpty(message = "There must be at least one member")
    var members: List<String>? = null

    @NotBlank(message = "Name project cannot be empty")
    var nameProject: String? = null

    @Schema(example = "germangrecoventura")
    var ownerGithub: String? = null

    @Schema(example = "")
    var tokenGithub: String? = null

    fun fromDTOToModel() = Group(name!!)
}