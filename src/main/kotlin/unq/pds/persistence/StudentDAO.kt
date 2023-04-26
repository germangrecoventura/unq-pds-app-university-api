package unq.pds.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import unq.pds.model.Project
import unq.pds.model.ProjectOwner
import unq.pds.model.Student
import java.util.Optional

interface StudentDAO : JpaRepository<Student, Long> {
    fun findByEmail(email: String): Optional<Student>

    @Query(
        """
            FROM ProjectOwner po
            JOIN po.projects ps
            WHERE ?1 IN ps
        """
    )
    fun projectOwnerOfTheProject(project: Project): Optional<ProjectOwner>
}

