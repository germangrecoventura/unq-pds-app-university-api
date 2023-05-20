package unq.pds.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class CommentCreateRequestDTO {
    @NotNull(message = "The id cannot be blank")
    @Schema(example = "1")
    var idToComment: Long? = null

    @NotBlank(message = "The name repository cannot be blank")
    @Schema(example = "unq-pds-app-university-api")
    var nameRepository: String? = null

    @NotBlank(message = "The comment cannot be blank")
    @Schema(example = "Exercise done correctly")
    var comment: String? = null
}