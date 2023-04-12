package unq.pds.api.dtos

import unq.pds.model.Group

class GroupDTO {

    var name: String? = null

    fun fromDTOToModel() = Group(name!!)
}