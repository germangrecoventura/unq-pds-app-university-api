package unq.pds.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

class TeacherCreateRequestDTO {
    @NotBlank(message = "The first name cannot be blank")
    @Pattern(regexp = "[a-zA-Z ]+", message = "The first name cannot contain special characters or numbers")
    @Schema(example = "German")
    var firstName: String? = null

    @NotBlank(message = "The last name cannot be blank")
    @Pattern(regexp = "[a-zA-Z ]+", message = "The last name cannot contain special characters or numbers")
    @Schema(example = "Greco")
    var lastName: String? = null

    @Email(message = "The email address is not valid")
    @NotBlank(message = "The email address cannot be blank")
    @Schema(example = "german@gmail.com")
    var email: String? = null

    @NotBlank(message = "The password cannot be blank")
    @Schema(example = "funciona")
    var password: String? = null
}