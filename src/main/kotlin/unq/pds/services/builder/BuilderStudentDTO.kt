package unq.pds.services.builder

import unq.pds.api.dtos.StudentCreateRequestDTO


class BuilderStudentDTO {
    private var firstName: String? = "German"
    private var lastName: String? = "Fernandez"
    private var emailAddress: String? = "german@gmail.com"

    fun build(): StudentCreateRequestDTO {
        var studentRequest = StudentCreateRequestDTO()
        studentRequest.firstName = firstName
        studentRequest.lastName = lastName
        studentRequest.email = emailAddress
        return studentRequest
    }

    fun withFirstName(name: String?): BuilderStudentDTO {
        this.firstName = name
        return this
    }

    fun withLastName(last_name: String?): BuilderStudentDTO {
        this.lastName = last_name
        return this
    }

    fun withEmail(email: String?): BuilderStudentDTO {
        this.emailAddress = email
        return this
    }

    companion object {
        fun aStudentDTO(): BuilderStudentDTO {
            return BuilderStudentDTO()
        }
    }
}