package unq.pds.model

import unq.pds.api.Validator
import javax.management.InvalidAttributeValueException
import javax.persistence.*

@Entity
@Table(name = "Project")
class Project(
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
    private var id: Long? = null

    @OneToMany
    var repositories: MutableSet<Repository> = mutableSetOf()

    fun addRepository(repository: Repository) {
        if (isMyRepository(repository)) throw CloneNotSupportedException("The repository is already in the project")
        repositories.add(repository)
    }

    fun getId() = id

    init { this.validateCreation() }

    private fun validateCreation() {
        validateName(name)
    }

    private fun validateName(name: String) {
        if (name.isNullOrBlank()) throw InvalidAttributeValueException("Name cannot be empty")
        if (Validator.containsSpecialCharacterGithub(name)) throw InvalidAttributeValueException("The name cannot contain special characters except - and _")
    }

    private fun isMyRepository(repository: Repository): Boolean {
        return repositories.any { it.name == repository.name }
    }
}