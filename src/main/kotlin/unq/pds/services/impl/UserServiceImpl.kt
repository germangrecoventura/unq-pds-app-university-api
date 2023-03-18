package unq.pds.services.impl

import unq.pds.persistence.UserDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.model.User
import unq.pds.services.UserService

@Service
@Transactional
open class UserServiceImpl : UserService {

    @Autowired private lateinit var userDAO: UserDAO

    override fun create(user: User): User {
        return userDAO.save(user)
    }

    override fun count(): Int {
        return userDAO.count().toInt()
    }

    override fun clearUsers() {
        userDAO.deleteAll()
    }
}
