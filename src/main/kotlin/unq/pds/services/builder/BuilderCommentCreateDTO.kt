package unq.pds.services.builder

import unq.pds.api.dtos.CommentCreateRequestDTO


class BuilderCommentCreateDTO {
    var repositoryId: Long? = 1
    var commentAdd: String? = "Exercise done correctly"

    fun build(): CommentCreateRequestDTO {
        var comment = CommentCreateRequestDTO()
        comment.repositoryId = repositoryId
        comment.comment = commentAdd
        return comment
    }

    fun withId(id: Long?): BuilderCommentCreateDTO {
        this.repositoryId = id
        return this
    }


    fun withComment(comment: String?): BuilderCommentCreateDTO {
        this.commentAdd = comment
        return this
    }

    companion object {
        fun aCommentDTO(): BuilderCommentCreateDTO {
            return BuilderCommentCreateDTO()
        }
    }
}