package dao

import entity.User
import javax.persistence.EntityManager

class HibernateUserDAO(entityManager: EntityManager) : HibernateDAO<User>(User::class.java, entityManager)
