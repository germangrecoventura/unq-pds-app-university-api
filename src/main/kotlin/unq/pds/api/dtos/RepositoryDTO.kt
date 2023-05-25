package unq.pds.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

class RepositoryDTO {

    @NotBlank(message = "Name cannot be empty")
    @Pattern(regexp = "[a-zA-Z0-9_-]+", message = "The name cannot contain special characters except - and _")
    var name: String? = null

    @NotNull(message = "The project id cannot be blank")
    @Schema(example = "1")
    var projectId: Long? = null
}