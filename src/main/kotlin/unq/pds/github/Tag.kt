package unq.pds.github

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "tag")
class Tag {
    @Id
    var id: Int = 5
    var name: String = ""
    var zipUrl = ""
    var tarUrl = ""
}
