package unq.pds.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class DeployInstanceCommentDTO {
    @NotNull(message = "The deploy instance id cannot be blank")
    @Schema(example = "1")
    var deployInstanceId: Long? = null

    @NotBlank(message = "The comment cannot be blank")
    @Schema(example = "To start, go to...")
    var comment: String? = null
}