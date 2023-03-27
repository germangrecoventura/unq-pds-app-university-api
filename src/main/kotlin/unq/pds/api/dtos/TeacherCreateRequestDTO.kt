package unq.pds.api.dtos

class TeacherCreateRequestDTO(
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
) {
    class BuilderTeacherCreateDTO {
        private val teacherDTO = TeacherCreateRequestDTO()
        fun withFirstName(first_name: String?) = apply { teacherDTO.firstName = first_name }
        fun withLastName(last_name: String?) = apply { teacherDTO.lastName = last_name }
        fun withEmail(email_address: String?) = apply { teacherDTO.email = email_address }
        fun build() = teacherDTO
    }
}
