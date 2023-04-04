package unq.pds.model.builder

import unq.pds.model.Matter

class MatterBuilder {
    private var name: String = "Practica de Desarrollo de Software"

    fun build(): Matter {
        return Matter(name)
    }

    fun withName(name: String): MatterBuilder {
        this.name = name
        return this
    }

    companion object {
        fun aMatter(): MatterBuilder {
            return MatterBuilder()
        }
    }
}