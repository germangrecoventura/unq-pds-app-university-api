package unq.pds.model.builder

import unq.pds.model.Student


class BuilderStudent {
    private var firstName: String? = "German"
    private var lastName: String? = "Fernandez"
    private var emailAddress: String? = "german@gmail.com"

    fun build(): Student {
        return Student(firstName!!, lastName!!, emailAddress!!)
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

    companion object {
        fun aStudent(): BuilderStudent {
            return BuilderStudent()
        }
    }
}