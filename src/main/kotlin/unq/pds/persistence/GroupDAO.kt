package unq.pds.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import unq.pds.model.Group
import unq.pds.model.Project
import unq.pds.model.ProjectOwner
import java.util.*

interface GroupDAO : JpaRepository<Group, Long> {

    @Query(
        """
            FROM ProjectOwner po
            JOIN po.projects ps
            WHERE ?1 IN ps
        """
    )
    fun projectOwnerOfTheProject(project: Project): Optional<ProjectOwner>

    @Query(
        """
            SELECT COUNT(g) = 1
            FROM Group g
            JOIN g.members m
            WHERE g.id = ?1 AND m.email = ?2
        """
    )
    fun hasAMemberWithEmail(groupId: Long, email: String): Boolean
}