package unq.pds.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import unq.pds.api.Validator
import javax.management.InvalidAttributeValueException
import javax.persistence.*

@Entity
@Table(name = "admin_university")
@JsonPropertyOrder("id", "email", "password")
class Admin(
    @Column(
        nullable = false,
        unique = true
    ) @JsonProperty @field:Schema(example = "admin@gmail.com") private var email: String,
    @Column(
        nullable = false
    ) @JsonIgnore
    @field:Schema(example = "$" + "2a" + "$" + "CIymVbnbW.QfAyLP2mcw1ugKSpHbXn/N07sBhLjxUD1XqdlBNzHQi") private var password: String,

    ) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    @Schema(example = "1")
    private var id: Long? = null

    private fun validateCreate() {
        validateEmail(email)
        validatePassword(password)
    }

    private fun validateEmail(email_address: String?) {
        if (email_address.isNullOrBlank()) {
            throw InvalidAttributeValueException("The email cannot be empty")
        }
        if (!Validator.isValidEMail(email_address)) {
            throw InvalidAttributeValueException("The email is not valid")
        }
    }

    private fun validatePassword(password: String?) {
        if (password.isNullOrBlank()) {
            throw InvalidAttributeValueException("The password cannot be empty")
        }
    }

    fun getId(): Long? {
        return id
    }

    fun getEmail(): String? {
        return email
    }


    fun setEmail(emailAddress: String?) {
        validateEmail(emailAddress)
        this.email = emailAddress!!
    }

    fun getPassword(): String {
        return password
    }

    fun setPassword(password: String) {
        validatePassword(password)
        this.password = password
    }

    fun getRole(): String {
        return "ADMIN"
    }

    fun comparePassword(password: String): Boolean {
        return BCryptPasswordEncoder().matches(password, getPassword())
    }
}