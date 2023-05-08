package unq.pds.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.api.dtos.CommentUpdateDTO
import unq.pds.model.Comment
import unq.pds.persistence.CommentDAO
import unq.pds.services.CommentService

@Service
@Transactional
open class CommentServiceImpl : CommentService {

    @Autowired
    private lateinit var commentDAO: CommentDAO

    override fun save(comment: String): Comment {
        return commentDAO.save(Comment(comment))
    }

    override fun update(comment: CommentUpdateDTO): Comment {
        var commentRecovery = findById(comment.id!!)
        commentRecovery.setComment(comment.comment!!)
        return commentDAO.save(commentRecovery)
    }

    override fun findById(id: Long): Comment {
        return commentDAO.findById(id).orElseThrow { NoSuchElementException("Not found the comment with id $id") }
    }

    override fun deleteById(id: Long) {
        try {
            commentDAO.deleteById(id)
        } catch (e: RuntimeException) {
            throw NoSuchElementException("The teacher with id $id is not registered")
        }
    }

    override fun count(): Int {
        return commentDAO.count().toInt()
    }


    override fun findAll(): List<Comment> {
        return commentDAO.findAll().toList()
    }

    override fun clearComments() {
        commentDAO.deleteAll()
    }
}
