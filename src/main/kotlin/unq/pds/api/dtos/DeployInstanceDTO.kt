package unq.pds.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import unq.pds.model.DeployInstance
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

class DeployInstanceDTO {

    @Schema(example = "1")
    var id: Long? = null

    @NotBlank(message = "Name cannot be empty")
    @Pattern(regexp = "[a-zA-Z ]+")
    @Schema(example = "Railway")
    var name: String? = null

    @NotBlank(message = "Url cannot be empty")
    @Schema(example = "https://railway.app/project/unq-pds-app-university-api")
    var url: String? = null

    fun fromDTOToModel() = DeployInstance(name!!, url!!)
}