package unq.pds.model.builder

import unq.pds.github.Repository


class BuilderRepository {
    private var id: Long? = 5
    private var name: String? = "App-university"

    fun build(): Repository {
        return Repository(id!!, name!!)
    }

    fun withId(id: Long?): BuilderRepository {
        this.id = id
        return this
    }

    fun withName(name: String?): BuilderRepository {
        this.name = name
        return this
    }


    companion object {
        fun aRepository(): BuilderRepository {
            return BuilderRepository()
        }
    }
}