package unq.pds.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class PaginatedRepositoryDTO {
    @NotBlank(message = "The name repository cannot be blank")
    @Schema(example = "unq-pds-app-university-api")
    var nameRepository: String? = null

    @NotNull(message = "The number page cannot be blank")
    @Schema(example = "0")
    var numberPage: Int? = null

    @NotNull(message = "The size page cannot be blank")
    @Schema(example = "5")
    var sizePage: Int? = null
}