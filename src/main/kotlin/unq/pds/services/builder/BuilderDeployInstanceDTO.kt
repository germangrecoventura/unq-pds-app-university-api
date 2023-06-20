package unq.pds.services.builder

import unq.pds.api.dtos.DeployInstanceDTO

class BuilderDeployInstanceDTO {
    private var name: String? = "Railway"
    private var url: String? = "https://railway.app/project/unq-pds-app-university-api"
    private var comment: String? = "To start, go to..."
    private var projectId: Long? = null

    fun build(): DeployInstanceDTO {
        val deployInstance = DeployInstanceDTO()
        deployInstance.name = name
        deployInstance.url = url
        deployInstance.comment = comment
        deployInstance.projectId = projectId
        return deployInstance
    }

    fun withName(name: String?): BuilderDeployInstanceDTO {
        this.name = name
        return this
    }

    fun withUrl(url: String?): BuilderDeployInstanceDTO {
        this.url = url
        return this
    }

    fun withComment(comment: String?): BuilderDeployInstanceDTO {
        this.comment = comment
        return this
    }

    fun withProjectId(projectId: Long?): BuilderDeployInstanceDTO {
        this.projectId = projectId
        return this
    }

    companion object {
        fun aDeployInstanceDTO(): BuilderDeployInstanceDTO {
            return BuilderDeployInstanceDTO()
        }
    }
}