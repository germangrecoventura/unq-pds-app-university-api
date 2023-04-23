package unq.pds.model

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "tag")
class Tag {
    @Id
    var nodeId: String = ""
    var name: String = ""
    var zipUrl = ""
    var tarUrl = ""
}
