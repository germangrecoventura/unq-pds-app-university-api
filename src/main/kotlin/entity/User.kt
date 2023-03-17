package entity

import java.util.*
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "user")
class User(
    var firstName: String,
) : AbstractJpaPersistable<UUID>()
