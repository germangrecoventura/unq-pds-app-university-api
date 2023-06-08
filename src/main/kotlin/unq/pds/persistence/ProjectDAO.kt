package unq.pds.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import unq.pds.model.Project
import unq.pds.model.Repository
import java.util.*

interface ProjectDAO : JpaRepository<Project, Long> {

    @Query(
        """
            FROM Project p
            JOIN p.repositories rs
            WHERE ?1 IN rs
        """
    )
    fun projectWithRepository(repository: Repository): Optional<Project>

    @Query(
        """
            SELECT COUNT(c) > 0
            FROM Commission c
            JOIN c.teachers t
            JOIN c.groupsStudents gs
            JOIN gs.projects pj
            WHERE t.email = ?1 AND pj.id = ?2
        """
    )
    fun thereIsACommissionWhereIsteacherAndTheProjectExists(teacherEmail: String, projectId: Long): Boolean

    @Query(
        """
            SELECT COUNT(g) > 0
            FROM Group g
            JOIN g.members m
            JOIN g.projects pj
            WHERE m.email = ?1 AND pj.id = ?2
        """
    )
    fun thereIsAGroupWhereIsStudentAndTheProjectExists(studentEmail: String, projectId: Long): Boolean
}