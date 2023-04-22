package unq.pds.github

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name= "pulls")
class PullRequest {
    @Id
    var id: Int = 5
    var url = "url"
    var status = "ESTADO"
}