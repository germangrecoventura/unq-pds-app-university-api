package unq.pds.api.dtos

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

class RepositoryDTO {
    @NotBlank(message = "Name cannot be empty")
    @Pattern(regexp = "[a-zA-Z0-9_-]+", message = "The name cannot contain special characters except - and _")
    var name: String? = null

    @NotBlank(message = "Owner cannot be empty")
    var owner: String? = null

    @NotBlank(message = "Token cannot be empty")
    var token: String? = null
}