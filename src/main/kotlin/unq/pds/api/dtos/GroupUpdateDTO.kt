package unq.pds.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotBlank

class GroupUpdateDTO {
    @NotBlank(message = "id cannot be empty")
    @Schema(example = "1")
    var id: Long? = null

    @NotBlank(message = "Name cannot be empty")
    @Schema(example = "Group 1")
    var name: String? = null
}