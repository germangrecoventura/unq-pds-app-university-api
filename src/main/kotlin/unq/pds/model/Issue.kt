package unq.pds.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name= "issue")
class Issue {
    @Id
    var id: Int = 5
    @Column
    var title: String = ""
    @Column
    var url = ""
    @Column
    var status = ""
}