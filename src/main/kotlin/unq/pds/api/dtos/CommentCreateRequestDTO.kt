package unq.pds.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class CommentCreateRequestDTO {
    @NotNull(message = "The repository id cannot be blank")
    @Schema(example = "1")
    var repositoryId: Long? = null

    @NotBlank(message = "The comment cannot be blank")
    @Schema(example = "Exercise done correctly")
    var comment: String? = null
}