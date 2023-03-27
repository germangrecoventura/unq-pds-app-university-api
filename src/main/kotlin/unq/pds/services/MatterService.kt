package unq.pds.services

import unq.pds.model.Matter

interface MatterService {
    fun save(matter: Matter): Matter
    fun update(matter: Matter): Matter
    fun recover(matterId: Long): Matter
    fun delete(matterId: Long)
    fun count(): Int
    fun clearMatters()
}