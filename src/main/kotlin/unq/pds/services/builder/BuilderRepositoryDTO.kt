package unq.pds.services.builder

import unq.pds.api.dtos.RepositoryDTO


class BuilderRepositoryDTO {
    private var id: Long? = 5
    private var name: String? = "unq-pds-app-university-api"
    private var created: String? = "germangrecoventura"

    fun build(): RepositoryDTO {
        var repositoryDTO = RepositoryDTO()
        repositoryDTO.id = id
        repositoryDTO.name = name
        repositoryDTO.created = created
        return repositoryDTO
    }

    fun withId(id: Long?): BuilderRepositoryDTO {
        this.id = id
        return this
    }

    fun withName(name: String?): BuilderRepositoryDTO {
        this.name = name
        return this
    }

    fun withCreated(created: String?): BuilderRepositoryDTO {
        this.created = created
        return this
    }

    companion object {
        fun aRepositoryDTO(): BuilderRepositoryDTO {
            return BuilderRepositoryDTO()
        }
    }
}