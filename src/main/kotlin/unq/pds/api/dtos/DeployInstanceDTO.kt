package unq.pds.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import org.hibernate.validator.constraints.ConstraintComposition
import org.hibernate.validator.constraints.URL
import unq.pds.model.Comment
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

    @URL(message = "The url is not valid")
    @NotBlank(message = "Url cannot be empty")
    @Schema(example = "https://railway.app/project/unq-pds-app-university-api")
    var url: String? = null

    @NotBlank(message = "The comment cannot be blank")
    @Schema(example = "To start, go to...")
    var comment: String? = null

    @Schema(example = "1")
    var projectId: Long? = null

    fun fromDTOToModel() = DeployInstance(name!!, url!!, comment!!)
}