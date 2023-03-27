package unq.pds.model

import javax.persistence.*


@Entity
@Table(name = "teacher")
class Teacher(
    @Column(nullable = false) private var firstName: String?,
    @Column(nullable = false) private var lastName: String?,
    @Column(nullable = false, unique = true) private var email: String?
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private var id: Long? = null

    fun getId(): Long? {
        return id;
    }

    fun getFirstName(): String? {
        return firstName;
    }

    fun getLastName(): String? {
        return lastName;
    }

    fun getEmail(): String? {
        return email;
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

}
