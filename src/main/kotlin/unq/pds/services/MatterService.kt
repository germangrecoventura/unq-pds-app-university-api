package unq.pds.services

import unq.pds.model.Matter

interface MatterService {
    fun save(matter: Matter): Matter
    fun update(matter: Matter): Matter
    fun read(matterId: Long): Matter
    fun delete(matterId: Long)
    fun findByName(name: String): Matter
    fun readAll(): List<Matter>
    fun count(): Int
    fun clearMatters()
}