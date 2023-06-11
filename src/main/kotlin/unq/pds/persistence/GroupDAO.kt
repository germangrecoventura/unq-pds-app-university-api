package unq.pds.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import unq.pds.model.Group
import unq.pds.model.Project
import java.util.*

interface GroupDAO : JpaRepository<Group, Long> {
    @Query(
        """
             FROM Group po
             JOIN po.projects ps
             WHERE ?1 IN ps
         """
    )
    fun projectOwnerOfTheProject(project: Project): Optional<Group>

    @Query(
        """
             SELECT COUNT(g) = 1
             FROM Group g
             JOIN g.projects ps
             JOIN g.members m
             WHERE ps.id = ?1 AND m.id = ?2
         """
    )
    fun thereIsAGroupWithThisProjectAndThisMember(projectId: Long, studentId: Long): Boolean

    @Query(
        """
            SELECT COUNT(g) > 0
            FROM Group g
            JOIN g.members m
            JOIN g.projects pj
            JOIN pj.deployInstances deployIns
            WHERE m.email = ?1 AND deployIns.id = ?2
        """
    )
    fun thereIsAGroupWhereIsStudentAndTheDeployInstanceExists(studentEmail: String, deployInstanceId: Long): Boolean

    @Query(
        """
             SELECT COUNT(g) = 1
             FROM Group g
             JOIN g.members m
             WHERE g.id = ?1 AND m.email = ?2
         """
    )
    fun hasAMemberWithEmail(groupId: Long, email: String): Boolean

    @Query(
        """
             SELECT COUNT(g) = 1
             FROM Group g
             JOIN g.projects ps
             JOIN ps.repositories rs
             WHERE g.id = ?1 AND rs.id = ?2
         """
    )
    fun hasThisRepository(groupId: Long, repositoryId: Long): Boolean

    @Query(
        """
             SELECT COUNT(m) 
             FROM Group g
             JOIN g.members m
         """
    )
    fun countMembers(): Int
}
