package unq.pds.api.dtos

class TeacherCreateRequestDTO(
    val firstName: String,
    val lastName: String,
    val email: String,
) {
    companion object {
        fun fromModel(
            firstName: String,
            lastName: String,
            email: String
        ): TeacherCreateRequestDTO {
            return TeacherCreateRequestDTO(firstName, lastName, email)
        }
    }
}
