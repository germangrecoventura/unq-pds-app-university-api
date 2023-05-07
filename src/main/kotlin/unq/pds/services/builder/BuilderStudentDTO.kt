package unq.pds.services.builder

import unq.pds.api.dtos.StudentCreateRequestDTO


class BuilderStudentDTO {
    private var id: Long? = null
    private var firstName: String? = "German"
    private var lastName: String? = "Fernandez"
    private var emailAddress: String? = "german@gmail.com"
    private var password: String? = "funciona"
    private var ownerGithub: String? = "germangrecoventura"
    private var tokenGithub: String? = null

    fun build(): StudentCreateRequestDTO {
        var studentRequest = StudentCreateRequestDTO()
        studentRequest.id = id
        studentRequest.firstName = firstName
        studentRequest.lastName = lastName
        studentRequest.email = emailAddress
        studentRequest.password = password
        studentRequest.ownerGithub = ownerGithub
        studentRequest.tokenGithub = tokenGithub

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

    fun withLastName(last_name: String?): BuilderStudentDTO {
        this.lastName = last_name
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

    fun withOwnerGithub(ownerGithub: String?): BuilderStudentDTO {
        this.ownerGithub = ownerGithub
        return this
    }

    fun withTokenGithub(tokenGithub: String?): BuilderStudentDTO {
        this.tokenGithub = tokenGithub
        return this
    }

    companion object {
        fun aStudentDTO(): BuilderStudentDTO {
            return BuilderStudentDTO()
        }
    }
}