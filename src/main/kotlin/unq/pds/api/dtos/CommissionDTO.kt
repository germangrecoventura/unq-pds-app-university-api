package unq.pds.api.dtos

import unq.pds.model.Commission
import unq.pds.model.FourMonthPeriod
import unq.pds.model.Matter

class CommissionDTO {

    var year: Int? = null

    var fourMonthPeriod: FourMonthPeriod? = null

    var matter: MatterDTO? = null

    fun fromDTOToModel() = Commission(year!!, fourMonthPeriod!!, Matter(matter!!.name!!))
}