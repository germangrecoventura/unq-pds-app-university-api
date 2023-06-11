package unq.pds.services.builder

import unq.pds.api.dtos.DeployInstanceCommentDTO

class BuilderDeployInstanceCommentDTO {
    var deployInstanceId: Long? = 1
    var commentAdd: String? = "To start, go to..."

    fun build(): DeployInstanceCommentDTO {
        val comment = DeployInstanceCommentDTO()
        comment.deployInstanceId = deployInstanceId
        comment.comment = commentAdd
        return comment
    }

    fun withId(id: Long?): BuilderDeployInstanceCommentDTO {
        this.deployInstanceId = id
        return this
    }

    fun withComment(comment: String?): BuilderDeployInstanceCommentDTO {
        this.commentAdd = comment
        return this
    }

    companion object {
        fun aDeployInstanceCommentDTO(): BuilderDeployInstanceCommentDTO {
            return BuilderDeployInstanceCommentDTO()
        }
    }
}