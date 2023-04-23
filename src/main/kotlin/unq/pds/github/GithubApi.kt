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
        val objects: Array<Any>? = executeRequest(createdRepository,nameRepository,"issues").body
        val list = mutableListOf<Issue>()
        for (i in objects!!.indices) {
            val ele = objects[i] as LinkedHashMap<String, String>
            val issue = Issue()
            issue.id = ele["id"] as Int
            issue.title = ele["title"]!!
            issue.url = ele["url"]!!
            issue.status = ele["state"]!!
            list.add(issue)
        }
        return list
    }

    fun getRepositoryPulls(createdRepository: String, nameRepository: String): MutableList<PullRequest>? {
        val objects: Array<Any>? = executeRequest(createdRepository,nameRepository,"pulls").body
        val list = mutableListOf<PullRequest>()
        for (i in objects!!.indices) {
            val ele = objects[i] as LinkedHashMap<String, String>
            val pr = PullRequest()
            pr.id = ele["id"] as Int
            pr.url = ele["url"]!!
            pr.status = ele["state"]!!
            list.add(pr)
        }
        return list
    }

    fun getRepositoryTags(createdRepository: String, nameRepository: String): MutableList<Tag>? {
        val objects: Array<Any>? = executeRequest(createdRepository,nameRepository,"tags").body
        val list = mutableListOf<Tag>()
        for (i in objects!!.indices) {
            val ele = objects[i] as LinkedHashMap<String, String>
            val tag = Tag()
            tag.id = ele["id"] as Int
            tag.name = ele["name"]!!
            tag.zipUrl = ele["zipball_url"]!!
            tag.tarUrl = ele["tarball_url"]!!
            list.add(tag)
        }
        return list
    }

    private fun executeRequest(createdRepository: String, nameRepository: String,operation: String): ResponseEntity<Array<Any>> {
        if (createdRepository.isNullOrBlank()) throw InvalidAttributeValueException("Created repository cannot be empty")
        if (nameRepository.isNullOrBlank()) throw InvalidAttributeValueException("Name repository cannot be empty")
        if (nameRepository.isNullOrBlank()) throw InvalidAttributeValueException("Name repository cannot be empty")
        if (Validator.containsSpecialCharacterGithub(nameRepository)) throw InvalidAttributeValueException("The name repository cannot contain special characters except - and _")


        val url = "https://api.github.com/repos/$createdRepository/$nameRepository/$operation?state=all&direction=asc"
        val headers = HttpHeaders()
        headers.set("Accept", "application/vnd.github+json")
        headers.set("Authorization", "Bearer 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")
        val request: HttpEntity<*> = HttpEntity<Any?>(headers)
        return restTemplate.exchange(
            url, HttpMethod.GET, request,
            Array<Any>::class.java
        )
    }
}


