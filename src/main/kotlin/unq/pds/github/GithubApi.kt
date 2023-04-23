package unq.pds.github

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import unq.pds.api.Validator
import javax.management.InvalidAttributeValueException

@Component
class GithubApi {
    private var restTemplate: RestTemplate = RestTemplate()

    fun getRepositoryIssues(createdRepository: String, nameRepository: String): MutableList<Issue>? {
        val objects: Array<Any>? = executeRequest(createdRepository, nameRepository, "issues").body
        val list = mutableListOf<Issue>()
        for (i in objects!!.indices) {
            val element = objects[i] as LinkedHashMap<String, String>
            val issue = Issue()
            issue.id = element["id"] as Int
            issue.title = element["title"]!!
            issue.url = element["url"]!!
            issue.status = element["state"]!!
            list.add(issue)
        }
        return list
    }

    fun getRepositoryPulls(createdRepository: String, nameRepository: String): MutableList<PullRequest>? {
        val objects: Array<Any>? = executeRequest(createdRepository, nameRepository, "pulls").body
        val list = mutableListOf<PullRequest>()
        for (i in objects!!.indices) {
            val element = objects[i] as LinkedHashMap<String, String>
            val pr = PullRequest()
            pr.id = element["id"] as Int
            pr.url = element["url"]!!
            pr.status = element["state"]!!
            list.add(pr)
        }
        return list
    }

    fun getRepositoryTags(createdRepository: String, nameRepository: String): MutableList<Tag>? {
        val objects: Array<Any>? = executeRequest(createdRepository, nameRepository, "tags").body
        val list = mutableListOf<Tag>()
        for (i in objects!!.indices) {
            val element = objects[i] as LinkedHashMap<String, String>
            val tag = Tag()
            tag.id = element["id"] as Int
            tag.name = element["name"]!!
            tag.zipUrl = element["zipball_url"]!!
            tag.tarUrl = element["tarball_url"]!!
            list.add(tag)
        }
        return list
    }

    fun getRepositoryBranches(ownerRepository: String, nameRepository: String): MutableList<Branch>? {
        validation(ownerRepository, nameRepository)
        val url = "https://api.github.com/repos/$ownerRepository/$nameRepository/branches"
        val headers = HttpHeaders()
        headers.set("Accept", "application/vnd.github+json")
        headers.set("Authorization", "Bearer ${"ACA VA EL TOKEN"}")
        val request: HttpEntity<*> = HttpEntity<Any?>(headers)
        val response: ResponseEntity<Array<Any>> = restTemplate.exchange(
            url, HttpMethod.GET, request,
            Array<Any>::class.java
        )

        val objects: Array<Any>? = response.body
        val list = mutableListOf<Branch>()
        for (i in objects!!.indices) {
            val element = objects[i] as LinkedHashMap<String, String>
            val branch = Branch()
            branch.name = element["name"]!!
            list.add(branch)
        }
        return list
    }

    private fun executeRequest(
        ownerRepository: String,
        nameRepository: String,
        operation: String
    ): ResponseEntity<Array<Any>> {
        validation(ownerRepository, nameRepository)
        val url = "https://api.github.com/repos/$ownerRepository/$nameRepository/$operation?state=all&direction=asc"
        val headers = HttpHeaders()
        headers.set("Accept", "application/vnd.github+json")
        headers.set("Authorization", "Bearer ${"ACA VA EL TOKEN"}")
        val request: HttpEntity<*> = HttpEntity<Any?>(headers)
        return restTemplate.exchange(
            url, HttpMethod.GET, request,
            Array<Any>::class.java
        )
    }

    private fun validation(ownerRepository: String, nameRepository: String) {
        if (ownerRepository.isNullOrBlank()) throw InvalidAttributeValueException("Created repository cannot be empty")
        if (ownerRepository.isNullOrBlank()) throw InvalidAttributeValueException("Name repository cannot be empty")
        if (nameRepository.isNullOrBlank()) throw InvalidAttributeValueException("Name repository cannot be empty")
        if (Validator.containsSpecialCharacterGithub(nameRepository)) throw InvalidAttributeValueException("The name repository cannot contain special characters except - and _")
    }
}


