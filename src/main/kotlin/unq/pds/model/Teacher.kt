package unq.pds.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import io.swagger.v3.oas.annotations.media.Schema
import org.jasypt.util.text.AES256TextEncryptor
import unq.pds.api.Validator
import javax.management.InvalidAttributeValueException
import javax.persistence.*

@Entity
@Table(name = "teacher")
@JsonPropertyOrder("id", "firstName", "lastName", "email")
class Teacher(
    @Column(nullable = false) @JsonProperty @field:Schema(example = "German") private var firstName: String,
    @Column(nullable = false) @JsonProperty @field:Schema(example = "Greco") private var lastName: String,
    @Column(
        nullable = false,
        unique = true
    ) @JsonProperty @field:Schema(example = "german@gmail.com") private var email: String,
    @field:Schema(example = "QVNm6Z3nmXAqTzQUDWrGgTGLoyVKPw+z+RZ4784R4MZi5E2OpjqR01ChmR2qTmgo") private var password: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    private fun validateEmail(emailAddress: String?) {
        if (emailAddress.isNullOrBlank()) {
            throw InvalidAttributeValueException("The email cannot be empty")
        }
        if (!Validator.isValidEMail(emailAddress)) {
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

    fun getPassword(): String {
        return password
    }

    fun setPassword(password: String) {
        validatePassword(password)
        val encryptor = AES256TextEncryptor()
        encryptor.setPassword(System.getenv("ENCRYPT_PASSWORD"))
        val myEncryptedPassword = encryptor.encrypt(password)
        this.password = myEncryptedPassword
    }

    fun getRole(): String {
        return "TEACHER"
    }

    fun comparePassword(password: String): Boolean {
        val encryptor = AES256TextEncryptor()
        encryptor.setPassword(System.getenv("ENCRYPT_PASSWORD"))
        val myEncryptedPassword = encryptor.decrypt(getPassword())
        return password == myEncryptedPassword
    }
}