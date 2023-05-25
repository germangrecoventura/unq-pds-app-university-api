package unq.pds.model.builder

import unq.pds.model.Project

class ProjectBuilder {
    private var name: String = "unq-pds-app-university-api"
    private var ownerGithub: String? = "germangrecoventura"
    private var tokenGithub: String? = System.getenv("TOKEN_GITHUB")

    fun build(): Project {
        return Project(name, ownerGithub, tokenGithub)
    }

    fun withName(name: String): ProjectBuilder {
        this.name = name
        return this
    }

    fun withOwnerGithub(ownerGithub: String?): ProjectBuilder {
        this.ownerGithub = ownerGithub
        return this
    }

    fun withTokenGithub(tokenGithub: String?): ProjectBuilder {
        this.tokenGithub = tokenGithub
        return this
    }

    companion object {
        fun aProject(): ProjectBuilder {
            return ProjectBuilder()
        }
    }
}