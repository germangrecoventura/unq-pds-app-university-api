package unq.pds.model

import javax.persistence.*

@Entity
@Table(name = "Project")
class Project(
    @ManyToOne var repository: Repository
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private var id: Long? = null

    @Column(nullable = false)
    var name: String = repository.name

    fun getId() = id
}