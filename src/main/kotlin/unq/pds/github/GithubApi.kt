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
        /*val url = "https://api.github.com/repos/germangrecoventura/unq-pds-app-university-api/issues?state=all&direction=asc"*/
        if (createdRepository.isNullOrBlank()) throw InvalidAttributeValueException("Created repository cannot be empty")
        if (nameRepository.isNullOrBlank()) throw InvalidAttributeValueException("Name repository cannot be empty")
        if (nameRepository.isNullOrBlank()) throw InvalidAttributeValueException("Name repository cannot be empty")
        if (Validator.containsSpecialCharacterGithub(nameRepository)) throw InvalidAttributeValueException("The name repository cannot contain special characters except - and _")


        val url = "https://api.github.com/repos/$createdRepository/$nameRepository/issues?state=all&direction=asc"
        val headers = HttpHeaders()
        headers.set("Accept", "application/vnd.github+json")
        headers.set("Authorization", "Bearer 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")
        val request: HttpEntity<*> = HttpEntity<Any?>(headers)
        val response: ResponseEntity<Array<Any>> = restTemplate.exchange(
            url, HttpMethod.GET, request,
            Array<Any>::class.java
        )

        val objects: Array<Any>? = response.body
        val list = mutableListOf<Issue>()
        for (i in objects!!.indices) {
            println(objects[i])
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
}


