package dao

import java.util.*
import javax.persistence.EntityManager

open class HibernateDAO<T>(protected val entityType: Class<T>, protected val entityManager: EntityManager) {
    open fun save(entity: T): T {
        entityManager.persist(entity)
        entityManager.flush()
        return entity
    }

    open fun update(entity: T): T {
        entityManager.merge(entity)
        entityManager.flush()
        return entity
    }

    open fun find(id: UUID): T {
        return entityManager.find(entityType, id)
            ?: throw RuntimeException("The entity $entityType with id $id does not exist")
    }

    open fun findAll(): List<T> {
        val hql = "select p from ${entityType.simpleName} p"
        val query = entityManager.createQuery(hql, entityType)
        return query.resultList
    }

    open fun count(): Int {
        val hql = "select count (p) from ${entityType.simpleName} p"
        val query = entityManager.createQuery(hql)
        return (query.singleResult as Long).toInt()
    }
}
