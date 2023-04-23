package unq.pds.model.builder

import unq.pds.model.Repository


class BuilderRepository {
    private var id: Long? = 5
    private var name: String? = "unq-pds-app-university-api"
    private var created: String? = "germangrecoventura"

    fun build(): Repository {
        return Repository(id!!, name!!, created!!)
    }

    fun withId(id: Long?): BuilderRepository {
        this.id = id
        return this
    }

    fun withName(name: String?): BuilderRepository {
        this.name = name
        return this
    }

    fun withCreated(created: String?): BuilderRepository {
        this.created = created
        return this
    }


    companion object {
        fun aRepository(): BuilderRepository {
            return BuilderRepository()
        }
    }
}