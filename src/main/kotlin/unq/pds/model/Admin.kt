package unq.pds.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "admin_university")
@JsonPropertyOrder("id", "email", "password")
class Admin(email: String, password: String) : User(email, password) {

    override fun getRole(): String {
        return "ADMIN"
    }

}