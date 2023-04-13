package unq.pds.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import unq.pds.model.Commission
import unq.pds.model.FourMonthPeriod
import unq.pds.model.Matter

class CommissionDTO {

    @Schema(example = "2023")
    var year: Int? = null

    @Schema(example = "FIRST_PERIOD")
    var fourMonthPeriod: FourMonthPeriod? = null

    var matter: MatterDTO? = null

    fun fromDTOToModel() = Commission(year!!, fourMonthPeriod!!, Matter(matter!!.name!!))
}