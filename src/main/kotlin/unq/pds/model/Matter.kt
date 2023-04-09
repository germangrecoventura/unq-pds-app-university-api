package unq.pds.model

import com.fasterxml.jackson.annotation.*
import io.swagger.v3.oas.annotations.media.Schema
import javax.persistence.*

@Entity
@Table(name = "matter")
@JsonPropertyOrder("id", "name")
class Matter(
     name: String
) {
    @Column(nullable = false, unique = true) @JsonProperty @field:Schema(example = "Matematica")
    var name = name
        set(value) {
            this.validateName(value)
            field = value
        }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    @Schema(example = "1")
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