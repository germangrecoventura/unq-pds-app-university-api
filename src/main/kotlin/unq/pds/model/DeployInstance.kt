package unq.pds.model

import javax.management.InvalidAttributeValueException
import javax.persistence.*

@Entity
@Table(name = "deployInstance")
class DeployInstance(
    name: String,
    url: String
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = null

    @Column(nullable = false)
    var name = name
        set(value) {
            this.validateName(value)
            field = value
        }

    @Column(nullable = false)
    var url = url
        set(value) {
            this.validateUrl(value)
            field = value
        }

    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    var comments: MutableList<Comment> = mutableListOf()

    fun addComment(comment: Comment) {
        comments.add(comment)
    }

    fun getId() = id

    init { this.validateCreation() }

    private fun validateCreation() {
        validateName(name)
        validateUrl(url)
    }

    private fun validateName(name: String) {
        if (name.isNullOrBlank()) throw InvalidAttributeValueException("Name cannot be empty")
        if (name.any { !(it.isLetter() or it.isWhitespace())}) throw InvalidAttributeValueException("Name cannot contain special characters")
    }

    private fun validateUrl(url: String) {
        if (url.isNullOrBlank()) throw InvalidAttributeValueException("Url cannot be empty")
    }
}