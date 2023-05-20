package unq.pds.services.builder

import unq.pds.api.dtos.RepositoryDTO


class BuilderRepositoryDTO {
    private var name: String? = "unq-pds-app-university-api"
    private var owner: String? = "germangrecoventura"

    fun build(): RepositoryDTO {
        var repositoryDTO = RepositoryDTO()
        repositoryDTO.name = name
        repositoryDTO.owner = owner
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

    companion object {
        fun aRepositoryDTO(): BuilderRepositoryDTO {
            return BuilderRepositoryDTO()
        }
    }
}