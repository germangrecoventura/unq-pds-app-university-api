package unq.pds.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

class AdminCreateRequestDTO {
    @Email(message = "The email address is not valid")
    @NotBlank(message = "The email address cannot be blank")
    @Schema(example = "admin@gmail.com")
    var email: String? = null

    @NotBlank(message = "The password cannot be blank")
    @Schema(example = "funciona")
    var password: String? = null
}