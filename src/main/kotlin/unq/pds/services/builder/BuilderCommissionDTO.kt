package unq.pds.services.builder

import unq.pds.api.dtos.CommissionDTO
import unq.pds.model.FourMonthPeriod

class BuilderCommissionDTO {
    private var year: Int? = 2023
    private var fourMonthPeriod: FourMonthPeriod? = FourMonthPeriod.FIRST_PERIOD
    private var matterName: String? = "Software development practice"

    fun build(): CommissionDTO {
        var commission = CommissionDTO()
        commission.year = year
        commission.fourMonthPeriod = fourMonthPeriod
        commission.matterName = matterName
        return commission
    }

    fun withYear(year: Int): BuilderCommissionDTO {
        this.year = year
        return this
    }

    fun withFourMonthPeriod(fourMonthPeriod: FourMonthPeriod): BuilderCommissionDTO {
        this.fourMonthPeriod = fourMonthPeriod
        return this
    }

    fun withMatterName(matterName: String): BuilderCommissionDTO {
        this.matterName = matterName
        return this
    }

    companion object {
        fun aCommissionDTO(): BuilderCommissionDTO {
            return BuilderCommissionDTO()
        }
    }
}