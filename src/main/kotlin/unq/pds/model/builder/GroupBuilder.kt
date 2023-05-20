package unq.pds.model.builder

import unq.pds.model.Group

class GroupBuilder {
    private var name: String = "Group 2"

    fun build(): Group {
        return Group(name)
    }

    fun withName(name: String): GroupBuilder {
        this.name = name
        return this
    }

    companion object {
        fun aGroup(): GroupBuilder {
            return GroupBuilder()
        }
    }
}