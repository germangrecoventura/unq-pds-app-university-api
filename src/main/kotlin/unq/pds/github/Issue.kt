package unq.pds.github

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name= "issue")
class Issue {
    @Id
    var id: Int = 5
    var title: String = "unq-pds-app-university-api"
    var url = "url"
    var status = "ESTADO"
}