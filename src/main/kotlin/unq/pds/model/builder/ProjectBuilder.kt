package unq.pds.model.builder

import unq.pds.model.Project

class ProjectBuilder {
    private var name: String = "unq-pds-app-university-api"

    fun build(): Project {
        return Project(name)
    }

    fun withName(name: String): ProjectBuilder {
        this.name = name
        return this
    }

    companion object {
        fun aProject(): ProjectBuilder {
            return ProjectBuilder()
        }
    }
}