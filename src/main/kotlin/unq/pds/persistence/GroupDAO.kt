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
            JOIN g.projects ps
            JOIN g.members m
            WHERE ps.id = ?1 AND m.id = ?2
        """
    )
    fun thereIsAGroupWithThisProjectAndThisMember(projectId: Long, studentId: Long): Boolean
}