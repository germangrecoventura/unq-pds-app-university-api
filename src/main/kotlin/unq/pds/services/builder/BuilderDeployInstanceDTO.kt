package unq.pds.services.builder

import unq.pds.api.dtos.DeployInstanceDTO

class BuilderDeployInstanceDTO {
    private var id: Long? = null
    private var name: String? = "Railway"
    private var url: String? = "https://railway.app/project/unq-pds-app-university-api"

    fun build(): DeployInstanceDTO {
        val deployInstance = DeployInstanceDTO()
        deployInstance.id = id
        deployInstance.name = name
        deployInstance.url = url
        return deployInstance
    }

    fun withId(id: Long?): BuilderDeployInstanceDTO {
        this.id = id
        return this
    }

    fun withName(name: String?): BuilderDeployInstanceDTO {
        this.name = name
        return this
    }

    fun withUrl(url: String?): BuilderDeployInstanceDTO {
        this.url = url
        return this
    }

    companion object {
        fun aDeployInstanceDTO(): BuilderDeployInstanceDTO {
            return BuilderDeployInstanceDTO()
        }
    }
}