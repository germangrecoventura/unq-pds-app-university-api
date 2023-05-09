package unq.pds.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import unq.pds.model.Comment
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
    fun commentsFromId(id:Long): List<Comment>
}