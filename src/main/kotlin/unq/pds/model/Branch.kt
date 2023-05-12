package unq.pds.model

import javax.persistence.*

@Entity
@Table(name = "branch")
class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = null

    var name: String = ""
}