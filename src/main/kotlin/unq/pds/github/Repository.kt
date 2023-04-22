package unq.pds.github

import unq.pds.api.Validator
import javax.management.InvalidAttributeValueException
import javax.persistence.*

@Entity
@Table(name = "repository")
class Repository(id: Long, name: String, created: String) {
    @Id
    var id: Long = id
        set(value) {
            this.validateId(value)
            field = value
        }

    @Column(unique = true)
    var name = name
        set(value) {
            this.validateName(value)
            field = value
        }

    @Column
    var created = created

    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    var issues: MutableList<Issue> = mutableListOf()
    /*var commits: MutableSet<String> = mutableSetOf()
    var branches: MutableSet<String> = mutableSetOf()
    var tags: MutableSet<String> = mutableSetOf()
    var prs: MutableSet<String> = mutableSetOf()*/

    private fun validateId(id: Long) {
        if (id.toString().isNullOrBlank()) throw InvalidAttributeValueException("Id cannot be empty")
    }

    private fun validateName(name: String) {
        if (name.isNullOrBlank()) throw InvalidAttributeValueException("Name cannot be empty")
        if (Validator.containsSpecialCharacterGithub(name)) throw InvalidAttributeValueException("The name cannot contain special characters except - and _")
    }

    init {
        this.validateCreation()
    }

    private fun validateCreation() {
        validateId(id)
        validateName(name)
    }
}