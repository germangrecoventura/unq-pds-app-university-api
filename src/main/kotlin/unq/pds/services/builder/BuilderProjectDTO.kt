package unq.pds.services.builder

import unq.pds.api.dtos.ProjectDTO

class BuilderProjectDTO {
    private var id: Long? = null
    private var name: String? = "unq-pds-app-university-api"
    private var ownerGithub: String? = "germangrecoventura"
    private var tokenGithub: String? = System.getenv("TOKEN_GITHUB")

    fun build(): ProjectDTO {
        val project = ProjectDTO()
        project.id = id
        project.name = name
        project.ownerGithub = ownerGithub
        project.tokenGithub = tokenGithub
        return project
    }

    fun withId(id: Long?): BuilderProjectDTO {
        this.id = id
        return this
    }

    fun withName(name: String?): BuilderProjectDTO {
        this.name = name
        return this
    }

    fun withOwner(owner: String?): BuilderProjectDTO {
        this.ownerGithub = owner
        return this
    }

    fun withToken(token: String?): BuilderProjectDTO {
        this.tokenGithub = token
        return this
    }

    companion object {
        fun aProjectDTO(): BuilderProjectDTO {
            return BuilderProjectDTO()
        }
    }
}