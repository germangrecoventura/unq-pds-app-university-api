package unq.pds.services.builder

import unq.pds.api.dtos.TeacherCreateRequestDTO


class BuilderTeacherDTO {
    private var firstName: String? = "German"
    private var lastName: String? = "Fernandez"
    private var emailAddress: String? = "german@gmail.com"

    fun build(): TeacherCreateRequestDTO {
        var teacherRequest = TeacherCreateRequestDTO()
        teacherRequest.firstName = firstName
        teacherRequest.lastName = lastName
        teacherRequest.email = emailAddress
        return teacherRequest
    }

    fun withFirstName(name: String?): BuilderTeacherDTO {
        this.firstName = name
        return this
    }

    fun withLastName(lastname: String?): BuilderTeacherDTO {
        this.lastName = lastname
        return this
    }

    fun withEmail(email: String?): BuilderTeacherDTO {
        this.emailAddress = email
        return this
    }

    companion object {
        fun aTeacherDTO(): BuilderTeacherDTO {
            return BuilderTeacherDTO()
        }
    }
}