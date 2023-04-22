package unq.pds.services.builder

import unq.pds.api.dtos.RepositoryDTO


class BuilderRepositoryDTO {
    private var id: Long? = 5
    private var name: String? = "App_university"

    fun build(): RepositoryDTO {
        var repositoryDTO = RepositoryDTO()
        repositoryDTO.id = id
        repositoryDTO.name = name
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

    companion object {
        fun aRepositoryDTO(): BuilderRepositoryDTO {
            return BuilderRepositoryDTO()
        }
    }
}