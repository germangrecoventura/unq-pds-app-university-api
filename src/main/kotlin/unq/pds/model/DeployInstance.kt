package unq.pds.model

import org.springframework.security.web.util.UrlUtils
import javax.management.InvalidAttributeValueException
import javax.persistence.*

@Entity
@Table(name = "deployInstance")
class DeployInstance(
    name: String,
    url: String,
    comment: String
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

    @Column(nullable = false)
    var comment = comment
        set(value) {
            this.validateComment(value)
            field = value
        }

    fun getId() = id

    init { this.validateCreation() }

    private fun validateCreation() {
        validateName(name)
        validateUrl(url)
        validateComment(comment)
    }

    private fun validateName(name: String) {
        if (name.isNullOrBlank()) throw InvalidAttributeValueException("Name cannot be empty")
        if (name.any { !(it.isLetter() or it.isWhitespace())}) throw InvalidAttributeValueException("Name cannot contain special characters")
    }

    private fun validateUrl(url: String) {
        if (url.isNullOrBlank()) throw InvalidAttributeValueException("Url cannot be empty")
        if (!UrlUtils.isAbsoluteUrl(url)) throw InvalidAttributeValueException("The url is not valid")
    }

    private fun validateComment(comment: String) {
        if (comment.isNullOrBlank()) throw InvalidAttributeValueException("The comment cannot be empty")
    }
}