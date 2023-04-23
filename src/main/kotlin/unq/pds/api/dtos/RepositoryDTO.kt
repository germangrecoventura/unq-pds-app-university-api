package unq.pds.api.dtos

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

class RepositoryDTO {
    @NotBlank(message = "Name cannot be empty")
    @Pattern(regexp = "[a-zA-Z0-9]+[_-]", message = "The name cannot contain special characters except - and _")
    var name: String? = null

    @NotBlank(message = "Name cannot be empty")
    var owner: String? = null
}