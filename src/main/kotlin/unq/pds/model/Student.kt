package unq.pds.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import unq.pds.api.Validator
import javax.management.InvalidAttributeValueException
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "student")
@JsonPropertyOrder("id", "firstName", "lastName", "email", "projects")
class Student(
    @Column(nullable = false) @JsonProperty @field:Schema(example = "German") private var firstName: String,
    @Column(nullable = false) @JsonProperty @field:Schema(example = "Greco") private var lastName: String,
    @Column(
        nullable = false,
        unique = true
    ) @JsonProperty @field:Schema(example = "german@gmail.com") private var email: String,
    @field:Schema(example = "$" + "2a" + "$" + "CIymVbnbW.QfAyLP2mcw1ugKSpHbXn/N07sBhLjxUD1XqdlBNzHQi") private var password: String,
    @Column(
        nullable = true,
        unique = true
    ) @JsonProperty @field:Schema(example = "germangrecoventura") private var ownerGithub: String? = null,
    @Column(
        nullable = true,
        unique = true
    ) @JsonProperty @field:Schema(example = "") private var tokenGithub: String? = null
) : ProjectOwner() {
    init {
        validateCreate()
    }

    private fun validateCreate() {
        validatePerson(firstName, "firstname")
        validatePerson(lastName, "lastname")
        validateEmail(email)
        validatePassword(password)
    }

    private fun validatePerson(element: String?, field: String) {
        if (element.isNullOrBlank()) {
            throw InvalidAttributeValueException("The $field cannot be empty")
        }
        if (Validator.containsNumber(element)) {
            throw InvalidAttributeValueException("The $field can not contain numbers")
        }
        if (Validator.containsSpecialCharacter(element)) {
            throw InvalidAttributeValueException("The $field can not contain special characters")
        }
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

    fun getFirstName(): String? {
        return firstName
    }

    fun getLastName(): String? {
        return lastName
    }

    fun getEmail(): String? {
        return email
    }

    fun getOwnerGithub(): String? {
        return ownerGithub
    }

    fun getTokenGithub(): String? {
        return tokenGithub
    }

    fun setFirstName(firstName: String?) {
        validatePerson(firstName, "firstname")
        this.firstName = firstName!!
    }

    fun setLastName(lastName: String?) {
        validatePerson(lastName, "lastname")
        this.lastName = lastName!!
    }

    fun setEmail(emailAddress: String?) {
        validateEmail(emailAddress)
        this.email = emailAddress!!
    }

    fun setOwnerGithub(ownerGithub: String?) {
        this.ownerGithub = ownerGithub
    }

    fun setTokenGithub(token: String?) {
        this.tokenGithub = token
    }

    fun getPassword(): String {
        return password
    }

    fun setPassword(password: String) {
        this.password = password
    }

    fun getRole(): String {
        return "STUDENT"
    }

    fun comparePassword(password: String): Boolean {
        return BCryptPasswordEncoder().matches(password, getPassword())
    }
}