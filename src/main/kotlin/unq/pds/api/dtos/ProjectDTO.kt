package unq.pds.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import unq.pds.model.Project
import javax.validation.constraints.NotBlank

class ProjectDTO {

    @NotBlank(message = "Name cannot be empty")
    @Schema(example = "unq pds")
    var name: String? = null

    fun fromDTOToModel() = Project(name!!)
}