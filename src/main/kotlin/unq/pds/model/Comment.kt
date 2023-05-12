package unq.pds.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import javax.management.InvalidAttributeValueException
import javax.persistence.*

@Entity
@Table(name = "comment_teacher")
@JsonPropertyOrder("id", "date", "comment")
class Comment(
    @Column(nullable = false)
    @JsonProperty @field:Schema(example = "Exercise done correctly") private var comment: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    @Schema(example = "1")
    private var id: Long? = null

    @Column(nullable = false)
    @JsonProperty
    private var date: LocalDateTime = LocalDateTime.now()

    init {
        this.validateCreate()
    }

    private fun validateCreate() {
        validateComment(comment)
    }

    private fun validateComment(comment: String?) {
        if (comment.isNullOrBlank()) {
            throw InvalidAttributeValueException("The comment cannot be empty")
        }
    }

    fun getId(): Long? {
        return id
    }

    fun getComment(): String? {
        return comment
    }


    fun setComment(comment: String) {
        validateComment(comment)
        this.comment = comment
    }
}