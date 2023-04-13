package unq.pds.model.builder

import unq.pds.model.Teacher


class BuilderTeacher {
    private var firstName: String? = "German"
    private var lastName: String? = "Fernandez"
    private var emailAddress: String? = "german@gmail.com"

    fun build(): Teacher {
        return Teacher(firstName!!, lastName!!, emailAddress!!)
    }

    fun withFirstName(name: String?): BuilderTeacher {
        this.firstName = name
        return this
    }

    fun withLastName(lastname: String?): BuilderTeacher {
        this.lastName = lastname
        return this
    }

    fun withEmail(email: String?): BuilderTeacher {
        this.emailAddress = email
        return this
    }

    companion object {
        fun aTeacher(): BuilderTeacher {
            return BuilderTeacher()
        }
    }
}