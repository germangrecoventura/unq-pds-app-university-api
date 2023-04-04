package unq.pds.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import io.swagger.v3.oas.annotations.media.Schema
import unq.pds.api.Validator
import javax.persistence.*

@Entity
@Table(name = "teacher")
@JsonPropertyOrder("id", "firstName", "lastName", "email")
class Teacher(
    @Column(nullable = false) @JsonProperty @field:Schema(example = "German") private var firstName: String,
    @Column(nullable = false) @JsonProperty @field:Schema(example = "Greco") private var lastName: String,
    @Column(nullable = false, unique = true) @JsonProperty @field:Schema(example = "german@gmail.com") private var email: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    @Schema(example = "1")
    private var id: Long? = null

    init {
        validateCreate()
    }

    private fun validateCreate() {
        validatePerson(firstName, "firstname")
        validatePerson(lastName, "lastname")
        validateEmail(email)
    }

    private fun validatePerson(element: String?, field: String) {
        if (element.isNullOrBlank()) {
            throw RuntimeException("The $field cannot be empty")
        }
        if (Validator.containsNumber(element)) {
            throw RuntimeException("The $field can not contain numbers")
        }
        if (Validator.containsSpecialCharacter(element)) {
            throw RuntimeException("The $field can not contain special characters")
        }
    }

    private fun validateEmail(email_address: String?) {
        if (email_address.isNullOrBlank()) {
            throw RuntimeException("The email cannot be empty")
        }
        if (!Validator.isValidEMail(email_address)) {
            throw RuntimeException("The email is not valid")
        }
    }

    fun getId(): Long? {
        return id
    }

    fun getFirstName(): String {
        return firstName
    }

    fun getLastName(): String {
        return lastName
    }

    fun getEmail(): String {
        return email
    }

    fun setId(idNew: Long?) {
        id = idNew
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
        email = emailAddress!!
    }
}