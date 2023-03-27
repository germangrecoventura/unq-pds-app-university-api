package unq.pds.model

import unq.pds.api.Validator
import javax.persistence.*

@Entity
@Table(name = "student")
class Student(
    @Column(nullable = false) private var firstName: String?,
    @Column(nullable = false) private var lastName: String?,
    @Column(nullable = false) private var email: String?
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private var id: Long? = null

    @Column(nullable = true)
    private var repository: String? = null

    init {
        validateCreate()
    }

    private fun validateCreate() {
        if (firstName.isNullOrBlank()) {
            throw RuntimeException("The firstname cannot be empty")
        }
        if (Validator.containsNumber(firstName)) {
            throw RuntimeException("The firstname can not contain numbers")
        }
        if (Validator.containsSpecialCharacter(firstName)) {
            throw RuntimeException("The firstname can not contain special characters")
        }
        if (lastName.isNullOrBlank()) {
            throw RuntimeException("The firstname cannot be empty")
        }
        if (Validator.containsNumber(lastName)) {
            throw RuntimeException("The firstname can not contain numbers")
        }
        if (Validator.containsSpecialCharacter(lastName)) {
            throw RuntimeException("The firstname can not contain special characters")
        }
        if (email.isNullOrBlank()) {
            throw RuntimeException("The email cannot be empty")
        }
        if (!Validator.isValidEMail(email)) {
            throw RuntimeException("The email is not valid")
        }
    }

    fun getId(): Long? {
        return id
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

    fun setFirstName(first_name: String?) {
        firstName = first_name
    }

    fun setLastName(last_name: String?) {
        lastName = last_name
    }

    fun setEmail(email_address: String?) {
        email = email_address
    }

    fun getRepositories(): String? {
        return repository
    }
}