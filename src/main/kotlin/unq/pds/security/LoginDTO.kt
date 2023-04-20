package unq.pds.security

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

class LoginDTO {
    @NotBlank(message = "The email cannot be blank")
    @Email(message = "The email address is not valid")
    val email: String? = null
    @NotBlank(message = "The password cannot be blank")
    val password: String? = null
}