package unq.pds.services.builder

import unq.pds.security.LoginDTO


class BuilderLoginDTO {
    private var email: String? = "german@gmail.com"
    private var password: String? = "funciona"
    private var role: String? = "TEACHER"

    fun build(): LoginDTO {
        var login = LoginDTO()
        login.role = role
        login.password = password
        login.email = email
        return login
    }

    fun withRole(role: String?): BuilderLoginDTO {
        this.role = role
        return this
    }

    fun withEmail(email: String?): BuilderLoginDTO {
        this.email = email
        return this
    }

    fun withPassword(password: String?): BuilderLoginDTO {
        this.password = password
        return this
    }

    companion object {
        fun aLoginDTO(): BuilderLoginDTO {
            return BuilderLoginDTO()
        }
    }
}