package unq.pds.services.builder

import unq.pds.api.dtos.RepositoryDTO

class BuilderRepositoryDTO {
    private var name: String? = "unq-pds-app-university-api"
    private var projectId: Long = 1

    fun build(): RepositoryDTO {
        val repositoryDTO = RepositoryDTO()
        repositoryDTO.name = name
        repositoryDTO.projectId = projectId
        return repositoryDTO
    }

    fun withName(name: String?): BuilderRepositoryDTO {
        this.name = name
        return this
    }

    fun withProjectId(projectId: Long): BuilderRepositoryDTO {
        this.projectId = projectId
        return this
    }

    companion object {
        fun aRepositoryDTO(): BuilderRepositoryDTO {
            return BuilderRepositoryDTO()
        }
    }
}