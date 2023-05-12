package unq.pds.services.builder

import unq.pds.api.dtos.CommentCreateRequestDTO


class BuilderCommentCreateDTO {
    var idToComment: Long? = 1
    var nameRepository: String? = "unq-pds-app-university-api"
    var commentAdd: String? = "Exercise done correctly"

    fun build(): CommentCreateRequestDTO {
        var comment = CommentCreateRequestDTO()
        comment.idToComment = idToComment
        comment.nameRepository = nameRepository
        comment.comment = commentAdd
        return comment
    }

    fun withId(id: Long?): BuilderCommentCreateDTO {
        this.idToComment = id
        return this
    }

    fun withNameRepository(name: String?): BuilderCommentCreateDTO {
        this.nameRepository = name
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