package unq.pds.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import io.swagger.v3.oas.annotations.media.Schema
import javax.management.InvalidAttributeValueException
import javax.persistence.*

@Entity
@Table(name = "Project")
@JsonPropertyOrder("id", "name", "repositories")
class Project(
    name: String
) {
    @Column(nullable = false)
    @JsonProperty
    @Schema(example = "unq pds")
    var name = name
        set(value) {
            this.validateName(value)
            field = value
        }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    @Schema(example = "1")
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
    }

    private fun isMyRepository(repository: Repository): Boolean {
        return repositories.any { it.name == repository.name }
    }
}