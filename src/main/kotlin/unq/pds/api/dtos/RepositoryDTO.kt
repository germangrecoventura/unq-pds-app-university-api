package unq.pds.api.dtos

import unq.pds.model.Repository
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

class RepositoryDTO {

    @NotBlank(message = "Id cannot be empty")
    var id: Long? = null

    @NotBlank(message = "Name cannot be empty")
    @Pattern(regexp = "[a-zA-Z0-9]+[_-]", message = "The name cannot contain special characters except - and _")
    var name: String? = null

    @NotBlank(message = "Name cannot be empty")
    var created: String? = null

    fun fromDTOToModel() = Repository(id!!, name!!,created!!)
}