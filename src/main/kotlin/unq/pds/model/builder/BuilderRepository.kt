package unq.pds.model.builder

import unq.pds.model.Repository


class BuilderRepository {
    private var id: Long? = 5
    private var name: String? = "unq-pds-app-university-api"
    private var owner: String? = "germangrecoventura"
    private var url: String? = "https://github.com/germangrecoventura/unq-pds-app-university-api"

    fun build(): Repository {
        return Repository(id!!, name!!, owner!!,url!!)
    }

    fun withId(id: Long?): BuilderRepository {
        this.id = id
        return this
    }

    fun withName(name: String?): BuilderRepository {
        this.name = name
        return this
    }

    fun withOwner(owner: String?): BuilderRepository {
        this.owner = owner
        return this
    }

    fun withUrl(url: String?): BuilderRepository {
        this.url = url
        return this
    }


    companion object {
        fun aRepository(): BuilderRepository {
            return BuilderRepository()
        }
    }
}