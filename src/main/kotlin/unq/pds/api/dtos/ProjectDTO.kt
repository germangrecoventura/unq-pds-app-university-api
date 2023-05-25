package unq.pds.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import unq.pds.model.Project
import javax.validation.constraints.NotBlank

class ProjectDTO {

    @Schema(example = "1")
    var id: Long? = null

    @NotBlank(message = "Name cannot be empty")
    @Schema(example = "unq pds")
    var name: String? = null

    @Schema(example = "germangrecoventura")
    var ownerGithub: String? = null

    @Schema(example = "")
    var tokenGithub: String? = null

    fun fromDTOToModel() = Project(name!!, ownerGithub, tokenGithub)
}