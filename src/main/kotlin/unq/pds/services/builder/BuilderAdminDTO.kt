package unq.pds.services.builder

import unq.pds.api.dtos.AdminCreateRequestDTO


class BuilderAdminDTO {
    private var emailAddress: String? = "admin@gmail.com"
    private var password: String? = "funciona"

    fun build(): AdminCreateRequestDTO {
        var adminRequest = AdminCreateRequestDTO()
        adminRequest.email = emailAddress
        adminRequest.password = password
        return adminRequest
    }

    fun withEmail(email: String?): BuilderAdminDTO {
        this.emailAddress = email
        return this
    }

    fun withPassword(password: String?): BuilderAdminDTO {
        this.password = password
        return this
    }

    companion object {
        fun aAdminDTO(): BuilderAdminDTO {
            return BuilderAdminDTO()
        }
    }
}