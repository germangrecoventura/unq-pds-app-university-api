package unq.pds.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import unq.pds.model.Commission

interface CommissionDAO : JpaRepository<Commission, Long> {
    @Query(
        """
            SELECT COUNT(c) = 1
            FROM Commission c
            JOIN c.teachers t
            WHERE c.id = ?1 AND t.email = ?2
        """
    )
    fun hasATeacherWithEmail(commissionId: Long, email: String): Boolean
}