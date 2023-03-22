package unq.pds.model

import javax.persistence.*

@Entity
@Table(name = "user")
class User(@Column(nullable = false, length = 50, unique = true) var firstName: String) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null

}
