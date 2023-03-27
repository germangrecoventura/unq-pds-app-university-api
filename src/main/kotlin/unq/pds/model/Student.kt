package unq.pds.model

import unq.pds.api.Validator
import javax.persistence.*

@Entity
@Table(name = "student")
class Student(
    @Column(nullable = false) private var firstName: String?,
    @Column(nullable = false) private var lastName: String?,
    @Column(nullable = false, unique = true) private var email: String?
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

    fun getFirstName(): String? {
        return firstName
    }

    fun getLastName(): String? {
        return lastName
    }

    fun getEmail(): String? {
        return email
    }

    fun setId(idNew: Long?) {
        id = idNew
    }

    fun setFirstName(first_name: String?) {
        validatePerson(first_name, "firstname")
        firstName = first_name
    }

    fun setLastName(last_name: String?) {
        validatePerson(last_name, "lastname")
        lastName = last_name
    }

    fun setEmail(email_address: String?) {
        validateEmail(email_address)
        email = email_address
    }

    fun getRepositories(): String? {
        return repository
    }
}