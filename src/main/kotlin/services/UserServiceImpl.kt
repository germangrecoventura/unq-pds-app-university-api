package services

import dao.HibernateUserDAO

class UserServiceImpl(private val userDAO: HibernateUserDAO) {

    fun count(): Int {
        return userDAO.count()
    }
}
