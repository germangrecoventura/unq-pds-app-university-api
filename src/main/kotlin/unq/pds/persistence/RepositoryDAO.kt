package unq.pds.persistence

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import unq.pds.model.Comment
import unq.pds.model.Commit
import unq.pds.model.Issue
import unq.pds.model.Repository
import java.util.*

interface RepositoryDAO : JpaRepository<Repository, Long>{
    fun findByName(name: String): Optional<Repository>

    @Query(
        """
            SELECT cs
            FROM Repository r
            JOIN r.commentsTeacher cs
            WHERE r.id = ?1
        """
    )
    fun commentsFromId(id: Long): List<Comment>

    @Query(
        """
            SELECT cs
            FROM Repository r
            JOIN r.commits cs
            WHERE r.name = ?1
        """
    )
    fun getCommitsByPageFromRepository(name: String, page: Pageable): List<Commit>

    @Query(
        """
            SELECT COUNT(cs)
            FROM Repository r
            JOIN r.commits cs
            WHERE r.name = ?1
        """
    )
    fun countCommitsFromRepository(name: String): Int

    @Query(
        """
            SELECT issues
            FROM Repository r
            JOIN r.issues issues
            WHERE r.name = ?1
        """
    )
    fun getIssuesByPageFromRepository(name: String, page: Pageable): List<Issue>

    @Query(
        """
            SELECT COUNT(issues)
            FROM Repository r
            JOIN r.issues issues
            WHERE r.name = ?1
        """
    )
    fun countIssuesFromRepository(name: String): Int
}