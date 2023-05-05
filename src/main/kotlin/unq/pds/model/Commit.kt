package unq.pds.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "commit")
class Commit {
    @Id
    var nodeId: String = ""
    @Column
    var name: String = ""
    @Column
    var url: String = ""
}