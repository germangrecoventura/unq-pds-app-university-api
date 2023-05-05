package unq.pds.services

import unq.pds.api.dtos.AdminCreateRequestDTO
import unq.pds.model.Admin

interface AdminService {
    fun save(adminCreateRequestDTO: AdminCreateRequestDTO): Admin
    fun update(admin: Admin): Admin
    fun deleteById(id: Long)
    fun count(): Int
    fun findById(id: Long): Admin
    fun findByEmail(email: String): Admin
    fun readAll(): List<Admin>
    fun clearAdmins()
}