package unq.pds.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name= "pulls")
class PullRequest {
    @Id
    var id: Int = 5
    @Column
    var url = ""
    @Column
    var status = ""
    @Column
    var title = ""
}