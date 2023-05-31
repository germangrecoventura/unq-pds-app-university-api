package unq.pds.model.builder

import unq.pds.model.DeployInstance

class DeployInstanceBuilder {
    private var name: String = "Railway"
    private var url: String = "https://railway.app/project/unq-pds-app-university-api"

    fun build(): DeployInstance {
        return DeployInstance(name, url)
    }

    fun withName(name: String): DeployInstanceBuilder {
        this.name = name
        return this
    }

    fun withUrl(url: String): DeployInstanceBuilder {
        this.url = url
        return this
    }

    companion object {
        fun aDeployInstance(): DeployInstanceBuilder {
            return DeployInstanceBuilder()
        }
    }
}