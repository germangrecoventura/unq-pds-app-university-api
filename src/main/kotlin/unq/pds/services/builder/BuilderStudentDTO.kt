package unq.pds.services.builder

import unq.pds.api.dtos.StudentCreateRequestDTO


class BuilderStudentDTO {
    private var id: Long? = null
    private var firstName: String? = "German"
    private var lastName: String? = "Fernandez"
    private var emailAddress: String? = "german@gmail.com"
    private var password: String? = "funciona"

    fun build(): StudentCreateRequestDTO {
        var studentRequest = StudentCreateRequestDTO()
        studentRequest.id = id
        studentRequest.firstName = firstName
        studentRequest.lastName = lastName
        studentRequest.email = emailAddress
        studentRequest.password = password
        return studentRequest
    }

    fun withId(id: Long?): BuilderStudentDTO {
        this.id = id
        return this
    }

    fun withFirstName(name: String?): BuilderStudentDTO {
        this.firstName = name
        return this
    }

    fun withLastName(lastName: String?): BuilderStudentDTO {
        this.lastName = lastName
        return this
    }

    fun withEmail(email: String?): BuilderStudentDTO {
        this.emailAddress = email
        return this
    }

    fun withPassword(password: String?): BuilderStudentDTO {
        this.password = password
        return this
    }

    companion object {
        fun aStudentDTO(): BuilderStudentDTO {
            return BuilderStudentDTO()
        }
    }
}