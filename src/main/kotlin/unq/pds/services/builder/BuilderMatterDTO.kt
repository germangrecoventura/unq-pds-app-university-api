package unq.pds.services.builder

import unq.pds.api.dtos.MatterDTO


class BuilderMatterDTO {
    private var name: String? = "Matematica"

    fun build(): MatterDTO {
        var matter = MatterDTO()
        matter.name = name
        return matter
    }

    fun withName(name: String?): BuilderMatterDTO {
        this.name = name
        return this
    }


    companion object {
        fun aMatterDTO(): BuilderMatterDTO {
            return BuilderMatterDTO()
        }
    }
}