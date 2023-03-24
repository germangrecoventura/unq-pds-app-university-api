package unq.pds.model

import javax.persistence.*

@Entity
@Table(name = "teacher")
class Teacher(
    @Column(nullable = false) private var firstName: String?,
    @Column(nullable = false) private var lastName: String?,
    @Column(nullable = false) private var email: String?
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

}

