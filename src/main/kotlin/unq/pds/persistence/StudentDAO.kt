package unq.pds.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import unq.pds.model.Project
import unq.pds.model.ProjectOwner
import unq.pds.model.Student
import java.util.Optional

interface StudentDAO : JpaRepository<Student, Long> {
    fun findByEmail(email: String): Optional<Student>
    fun findByOwnerGithub(owner: String): Optional<Student>
    fun findByTokenGithub(token: String): Optional<Student>

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
            SELECT COUNT(s) = 1
            FROM Student s
            JOIN s.projects ps
            WHERE s.id = ?1 AND ps.id = ?2
        """
    )
    fun isHisProject(studentId: Long, projectId: Long): Boolean

    @Query(
        """
            SELECT COUNT(s) = 1
            FROM Student s
            JOIN s.projects ps
            JOIN ps.repositories rs
            WHERE s.id = ?1 AND rs.id = ?2
        """
    )
    fun isHisRepository(studentId: Long, repositoryId: Long): Boolean
}

