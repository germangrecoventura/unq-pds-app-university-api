package unq.pds.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import unq.pds.model.Project
import unq.pds.model.Repository
import java.util.Optional

interface ProjectDAO : JpaRepository<Project, Long> {

    @Query(
        """
            FROM Project p
            JOIN p.repositories rs
            WHERE ?1 IN rs
        """
    )
    fun projectWithRepository(repository: Repository): Optional<Project>
}