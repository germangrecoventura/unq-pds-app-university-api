package unq.pds.model

import javax.management.InvalidAttributeValueException
import javax.persistence.*

@Entity
@Table(name = "grupo")
class Group(
    name: String
) {
    @Column(nullable = false)
    var name = name
        set(value) {
            this.validateName(value)
            field = value
        }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    @ManyToMany
    var members: MutableSet<Student> = mutableSetOf()

    @Column(nullable = true)
    var repository: String? = null

    init { this.validateCreation() }

    private fun validateCreation() {
        validateName(name)
    }

    private fun validateName(name: String) {
        if (name.isBlank()) throw InvalidAttributeValueException("Name cannot be empty")
    }
}