package unq.pds.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.api.dtos.AdminCreateRequestDTO
import unq.pds.model.Admin
import unq.pds.model.exceptions.AlreadyRegisteredException
import unq.pds.persistence.AdminDAO
import unq.pds.services.AdminService

@Service
@Transactional
open class AdminServiceImpl : AdminService {
    @Autowired
    lateinit var adminDAO: AdminDAO

    override fun save(adminCreateRequestDTO: AdminCreateRequestDTO): Admin {
        if (adminDAO.findByEmail(adminCreateRequestDTO.email!!).isPresent) {
            throw AlreadyRegisteredException("email")
        }
        val admin = Admin(
            adminCreateRequestDTO.email!!,
            BCryptPasswordEncoder().encode(adminCreateRequestDTO.password)
        )
        return adminDAO.save(admin)
    }

    override fun update(admin: Admin): Admin {
        var adminRecovery = findById(admin.getId()!!)
        var adminWithEmail = adminDAO.findByEmail(admin.getEmail()!!)
        if (adminWithEmail.isPresent && adminRecovery.getId() != adminWithEmail.get().getId()) {
            throw AlreadyRegisteredException("email")
        }
        adminRecovery.setEmail(admin.getEmail())
        adminRecovery.setPassword(BCryptPasswordEncoder().encode(admin.getPassword()))
        return adminDAO.save(adminRecovery)
    }

    override fun deleteById(id: Long) {
        try {
            adminDAO.deleteById(id)
        } catch (e: RuntimeException) {
            throw NoSuchElementException("The admin with id $id is not registered")
        }
    }

    override fun count(): Int {
        return adminDAO.count().toInt()
    }

    override fun findById(id: Long): Admin {
        return adminDAO.findById(id).orElseThrow { NoSuchElementException("Not found the admin with id $id") }
    }

    override fun findByEmail(email: String): Admin {
        return adminDAO.findByEmail(email)
            .orElseThrow { NoSuchElementException("Not found the admin with email $email") }
    }

    override fun readAll(): List<Admin> {
        return adminDAO.findAll().toList()
    }

    override fun clearAdmins() {
        adminDAO.deleteAll()
    }
}
