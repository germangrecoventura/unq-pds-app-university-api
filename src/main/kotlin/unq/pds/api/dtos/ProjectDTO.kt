package unq.pds.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import unq.pds.model.Project
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

class ProjectDTO {

    @NotBlank(message = "Name cannot be empty")
    @Pattern(regexp = "[a-zA-Z0-9_-]+", message = "The name cannot contain special characters except - and _")
    @Schema(example = "unq-pds")
    var name: String? = null

    fun fromDTOToModel() = Project(name!!)
}