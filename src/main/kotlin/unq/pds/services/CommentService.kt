package unq.pds.services

import unq.pds.api.dtos.CommentUpdateDTO
import unq.pds.model.Comment

interface CommentService {
    fun save(comment: String): Comment
    fun update(comment: CommentUpdateDTO): Comment
    fun deleteById(id: Long)
    fun count(): Int
    fun findById(id: Long): Comment
    fun findAll(): List<Comment>
    fun clearComments()
}