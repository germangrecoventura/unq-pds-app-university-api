package unq.pds.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import unq.pds.api.Validator
import javax.management.InvalidAttributeValueException
import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract class User(
    @Column(
        nullable = false,
        unique = true
    ) @JsonProperty @field:Schema(example = "german@gmail.com") private var email: String,
    @JsonProperty @field:Schema(example = "$" + "2a" + "$" + "10" + "$" + "lfY4d1BFwiKH3kV1LL1cXuVmiZdWi6zQLlx6M9" + "/" + "6t2aHo9FP2Ky0i") private var password: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    @Schema(example = "1")
    private var id: Long? = null
    fun getId() = id

    fun getEmail(): String? {
        return email
    }

    fun getPassword(): String {
        return password
    }

    abstract fun getRole(): String

    fun setId(idNew: Long?) {
        id = idNew
    }

    protected fun validateEmail(emailAddress: String?) {
        if (emailAddress.isNullOrBlank()) {
            throw InvalidAttributeValueException("The email cannot be empty")
        }
        if (!Validator.isValidEMail(emailAddress)) {
            throw InvalidAttributeValueException("The email is not valid")
        }
    }

    protected fun validatePassword(password: String?) {
        if (password.isNullOrBlank()) {
            throw InvalidAttributeValueException("The password cannot be empty")
        }
    }

    fun setEmail(emailAddress: String?) {
        validateEmail(emailAddress)
        this.email = emailAddress!!
    }

    fun setPassword(password: String) {
        validatePassword(password)
        val encryptor = BCryptPasswordEncoder()
        val myEncryptedPassword = encryptor.encode(password)
        this.password = myEncryptedPassword
    }
}