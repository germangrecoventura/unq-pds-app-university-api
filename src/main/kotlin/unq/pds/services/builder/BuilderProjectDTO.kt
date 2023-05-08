package unq.pds.services.builder

import unq.pds.api.dtos.ProjectDTO

class BuilderProjectDTO {
    private var name: String? = "unq-pds-app-university-api"

    fun build(): ProjectDTO {
        val project = ProjectDTO()
        project.name = name
        return project
    }

    fun withName(name: String?): BuilderProjectDTO {
        this.name = name
        return this
    }

    companion object {
        fun aProjectDTO(): BuilderProjectDTO {
            return BuilderProjectDTO()
        }
    }
}