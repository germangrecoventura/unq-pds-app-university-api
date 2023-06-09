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

    @Query(
        """
            SELECT COUNT(c) > 0
            FROM Commission c
            JOIN c.teachers t
            JOIN c.groupsStudents gs
            WHERE t.email = ?1 AND gs.id = ?2
        """
    )
    fun thereIsACommissionWithATeacherWithEmailAndGroupWithId(email: String, groupId: Long): Boolean

    @Query(
        """
            SELECT COUNT(c) > 0
            FROM Commission c
            JOIN c.teachers t
            JOIN c.students st
            WHERE t.email = ?1 AND st.id = ?2
        """
    )
    fun thereIsACommissionWithATeacherWithEmailAndStudentWithId(email: String, studentId: Long): Boolean
}