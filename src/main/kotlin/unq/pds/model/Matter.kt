package unq.pds.model

import javax.persistence.*

@Entity
@Table(name = "matter")
class Matter(
     name: String
) {
    @Column(nullable = false, unique = true)
    var name = name
        set(value) {
            this.validateName(value)
            field = value
        }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    init { this.validateCreation() }

    private fun validateCreation() {
        validateName(name)
    }

    private fun validateName(name: String) {
        if (name.isBlank()) throw RuntimeException("Name cannot be empty")
        if (name.any { !(it.isLetterOrDigit() or it.isWhitespace())}) throw RuntimeException("Name cannot contain special characters")
    }
}