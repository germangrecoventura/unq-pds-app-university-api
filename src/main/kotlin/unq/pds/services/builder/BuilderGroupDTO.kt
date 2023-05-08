package unq.pds.services.builder

import unq.pds.api.dtos.GroupDTO
import unq.pds.api.dtos.StudentCreateRequestDTO


class BuilderGroupDTO {
    private var name: String? = "German"

    fun build(): GroupDTO {
        var group = GroupDTO()
        group.name = name
        return group
    }

    fun withName(name: String?): BuilderGroupDTO {
        this.name = name
        return this
    }

    companion object {
        fun aGroupDTO(): BuilderGroupDTO {
            return BuilderGroupDTO()
        }
    }
}