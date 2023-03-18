package unq.pds.persistence

import unq.pds.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserDAO : JpaRepository<User, Long>