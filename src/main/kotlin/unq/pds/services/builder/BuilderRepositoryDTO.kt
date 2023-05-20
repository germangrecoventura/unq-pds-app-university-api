package unq.pds.services.builder

import unq.pds.api.dtos.RepositoryDTO


class BuilderRepositoryDTO {
    private var name: String? = "unq-pds-app-university-api"
    private var owner: String? = "germangrecoventura"
    private var token: String? = System.getenv("TOKEN_GITHUB")

    fun build(): RepositoryDTO {
        var repositoryDTO = RepositoryDTO()
        repositoryDTO.name = name
        repositoryDTO.owner = owner
        repositoryDTO.token = token
        return repositoryDTO
    }

    fun withName(name: String?): BuilderRepositoryDTO {
        this.name = name
        return this
    }

    fun withOwner(owner: String?): BuilderRepositoryDTO {
        this.owner = owner
        return this
    }

    fun withToken(token: String?): BuilderRepositoryDTO {
        this.token = token
        return this
    }

    companion object {
        fun aRepositoryDTO(): BuilderRepositoryDTO {
            return BuilderRepositoryDTO()
        }
    }
}