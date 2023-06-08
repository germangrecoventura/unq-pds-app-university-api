package unq.pds.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import org.jasypt.util.text.AES256TextEncryptor
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
    @JsonProperty @field:Schema(example = "QVNm6Z3nmXAqTzQUDWrGgTGLoyVKPw+z+RZ4784R4MZi5E2OpjqR01ChmR2qTmgo") private var password: String
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

    protected fun validateEmail(email_address: String?) {
        if (email_address.isNullOrBlank()) {
            throw InvalidAttributeValueException("The email cannot be empty")
        }
        if (!Validator.isValidEMail(email_address)) {
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
        val encryptor = AES256TextEncryptor()
        encryptor.setPassword(System.getenv("ENCRYPT_PASSWORD"))
        val myEncryptedPassword = encryptor.encrypt(password)
        this.password = myEncryptedPassword
    }

    fun comparePassword(password: String): Boolean {
        val encryptor = AES256TextEncryptor()
        encryptor.setPassword(System.getenv("ENCRYPT_PASSWORD"))
        val myEncryptedPassword = encryptor.decrypt(getPassword())
        return password == myEncryptedPassword
    }
}