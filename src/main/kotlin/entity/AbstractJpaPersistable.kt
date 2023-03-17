package entity

import org.springframework.data.domain.Persistable
import org.springframework.data.util.ProxyUtils
import java.io.Serializable
import javax.persistence.*


/**
 * Abstract base class for entities. Allows parameterization of id type, chooses auto-generation and implements
 * [equals] and [hashCode] based on that id.
 *
 * This class was inspired by [org.springframework.data.jpa.domain.AbstractPersistable], which is part of the Spring Data project.
 */
@MappedSuperclass
abstract class AbstractJpaPersistable<T : Serializable> : Persistable<T> {

    companion object {
        private val serialVersionUID = -5554308939380869754L
    }

    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private var id: T? = null

    override fun getId(): T? {
        return id
    }

    /**
     * Must be [Transient] in order to ensure that no JPA provider complains because of a missing setter.
     *
     * @see org.springframework.data.domain.Persistable.isNew
     */
    @Transient
    override fun isNew() = null == getId()

    override fun toString() = "Entity of type ${this.javaClass.name} with id: $id"

    override fun equals(other: Any?): Boolean {
        other ?: return false

        if (this === other) return true

        if (javaClass != ProxyUtils.getUserClass(other)) return false

        other as AbstractJpaPersistable<*>

        return if (null == this.getId()) false else this.getId() == other.getId()
    }

    override fun hashCode(): Int {
        return 31 // TODO: Mejorar
    }
}
