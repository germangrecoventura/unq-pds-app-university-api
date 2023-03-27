package unq.pds.model

import javax.persistence.*

@Entity
@Table(name = "matter")
class Matter(
    @Column(nullable = false, unique = true) var name: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    init { this.validarCreacion() }

    private fun validarCreacion() {
        if (this.name.isBlank()) throw RuntimeException("Name cannot be empty")
    }
}