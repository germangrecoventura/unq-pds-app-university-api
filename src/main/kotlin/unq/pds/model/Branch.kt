package unq.pds.model

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "branch")
class Branch {
    @Id
    var name: String = ""
}