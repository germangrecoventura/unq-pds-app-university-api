package unq.pds.model.builder

import unq.pds.model.Commission
import unq.pds.model.FourMonthPeriod
import unq.pds.model.Matter
import unq.pds.model.builder.MatterBuilder.Companion.aMatter

class CommissionBuilder {
    private var year: Int = 2023
    private var fourMonthPeriod: FourMonthPeriod = FourMonthPeriod.FIRST_PERIOD
    private var matter: Matter = aMatter().build()

    fun build(): Commission {
        return Commission(year, fourMonthPeriod, matter)
    }

    fun withYear(year: Int): CommissionBuilder {
        this.year = year
        return this
    }

    fun withFourMonthPeriod(fourMonthPeriod: FourMonthPeriod): CommissionBuilder {
        this.fourMonthPeriod = fourMonthPeriod
        return this
    }

    fun withMatter(matter: Matter): CommissionBuilder {
        this.matter = matter
        return this
    }

    companion object {
        fun aCommission(): CommissionBuilder {
            return CommissionBuilder()
        }
    }
}