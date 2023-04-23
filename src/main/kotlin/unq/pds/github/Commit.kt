package unq.pds.github

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "commit")
class Commit {
    @Id
    var nodeId: String = ""
    var name: String = ""
    var url: String = ""
}