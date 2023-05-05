package unq.pds.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import unq.pds.model.Commission
import unq.pds.model.FourMonthPeriod
import unq.pds.model.Matter
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

class CommissionDTO {

    @Schema(example = "2023")
    var year: Int? = null

    @Schema(example = "FIRST_PERIOD")
    var fourMonthPeriod: FourMonthPeriod? = null

    @NotBlank(message = "Matter name cannot be empty")
    @Pattern(regexp = "[a-zA-Z0-9 ]+")
    @Schema(example = "Math")
    var matterName: String? = null

    fun fromDTOToModel() = Commission(year!!, fourMonthPeriod!!, Matter(matterName!!))
}