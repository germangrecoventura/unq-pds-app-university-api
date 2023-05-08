package unq.pds.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class CommentUpdateDTO {
    @NotNull(message = "The id cannot be blank")
    @Schema(example = "1")
    var id: Long? = null

    @NotBlank(message = "The comment cannot be blank")
    @Schema(example = "Exercise done correctly")
    var comment: String? = null
}