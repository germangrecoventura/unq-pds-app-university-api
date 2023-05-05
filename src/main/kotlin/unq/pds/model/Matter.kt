package unq.pds.model

import com.fasterxml.jackson.annotation.*
import io.swagger.v3.oas.annotations.media.Schema
import javax.management.InvalidAttributeValueException
import javax.persistence.*
import javax.validation.constraints.Pattern

@Entity
@Table(name = "matter")
@JsonPropertyOrder("id", "name")
class Matter(
     name: String
) {
    @Column(nullable = false, unique = true)
    @JsonProperty
    @Pattern(regexp = "[a-zA-Z0-9 ]+")
    @Schema(example = "Math")
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

    fun getId() = id

    init { this.validateCreation() }

    private fun validateCreation() {
        validateName(name)
    }

    private fun validateName(name: String) {
        if (name.isBlank()) throw InvalidAttributeValueException("Name cannot be empty")
        if (name.any { !(it.isLetterOrDigit() or it.isWhitespace())}) throw InvalidAttributeValueException("Name cannot contain special characters")
    }
}