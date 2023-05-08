package unq.pds.persistence

import org.springframework.data.jpa.repository.JpaRepository
import unq.pds.model.Comment

interface CommentDAO : JpaRepository<Comment, Long>