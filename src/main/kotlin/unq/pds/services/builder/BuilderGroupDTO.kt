package unq.pds.services.builder

import unq.pds.api.dtos.GroupDTO

class BuilderGroupDTO {
    private var name: String? = "Group 2"
    private var members: List<String>? = listOf("german@gmail.com")
    private var nameProject: String? = "a project"
    private var ownerGithub: String? = "germangrecoventura"
    private var tokenGithub: String? = System.getenv("TOKEN_GITHUB")

    fun build(): GroupDTO {
        val group = GroupDTO()
        group.name = name
        group.members = members
        group.nameProject = nameProject
        group.ownerGithub = ownerGithub
        group.tokenGithub = tokenGithub
        return group
    }

    fun withName(name: String?): BuilderGroupDTO {
        this.name = name
        return this
    }

    fun withMembers(members: List<String>?): BuilderGroupDTO {
        this.members = members
        return this
    }

    fun withNameProject(nameProject: String?): BuilderGroupDTO {
        this.nameProject = nameProject
        return this
    }

    fun withOwnerGithub(ownerGithub: String?): BuilderGroupDTO {
        this.ownerGithub = ownerGithub
        return this
    }

    fun withTokenGithub(tokenGithub: String?): BuilderGroupDTO {
        this.tokenGithub = tokenGithub
        return this
    }

    companion object {
        fun aGroupDTO(): BuilderGroupDTO {
            return BuilderGroupDTO()
        }
    }
}