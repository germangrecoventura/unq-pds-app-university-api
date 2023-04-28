package unq.pds.model.builder

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import unq.pds.model.Student


class BuilderStudent {
    private var firstName: String? = "German"
    private var lastName: String? = "Fernandez"
    private var emailAddress: String? = "german@gmail.com"
    private var password: String? = BCryptPasswordEncoder().encode("funciona")
    private var ownerGithub: String? = null
    private var tokenGithub: String? = null

    fun build(): Student {
        return Student(firstName!!, lastName!!, emailAddress!!, password!!, ownerGithub, tokenGithub)
    }

    fun withFirstName(name: String?): BuilderStudent {
        this.firstName = name
        return this
    }

    fun withLastName(lastname: String?): BuilderStudent {
        this.lastName = lastname
        return this
    }

    fun withEmail(email: String?): BuilderStudent {
        this.emailAddress = email
        return this
    }

    fun withPassword(password: String?): BuilderStudent {
        this.password = password
        return this
    }

    fun withOwnerGithub(ownerGithub: String?): BuilderStudent {
        this.ownerGithub = ownerGithub
        return this
    }

    fun withTokenGithub(tokenGithub: String?): BuilderStudent {
        this.tokenGithub = tokenGithub
        return this
    }

    companion object {
        fun aStudent(): BuilderStudent {
            return BuilderStudent()
        }
    }
}