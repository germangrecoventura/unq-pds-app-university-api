package unq.pds.api

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import unq.pds.model.*
import javax.management.InvalidAttributeValueException

@Component
class GithubApi {
    private var restTemplate: RestTemplate = RestTemplate()

    fun getRepositoryIssues(owner: String, nameRepository: String): MutableList<Issue>? {
        val mapper = ObjectMapper()
        val root: JsonNode = mapper.readTree(executeRequest(owner, nameRepository, "issues").body)
        val list = mutableListOf<Issue>()
        for (i in root) {
            val issue = Issue()
            issue.id = i.path("id").asInt()
            issue.title = i.path("title").asText()
            var url = i.path("url").asText()
            issue.url = "https://github.com/${url.substring(29, url.length)}"
            issue.status = i.path("state").asText()
            list.add(issue)
        }
        return list
    }

    fun getRepositoryPulls(owner: String, nameRepository: String): MutableList<PullRequest>? {
        val mapper = ObjectMapper()
        val root: JsonNode = mapper.readTree(executeRequest(owner, nameRepository, "pulls").body)
        val list = mutableListOf<PullRequest>()
        for (i in root) {
            val pr = PullRequest()
            pr.id = i.path("id").asInt()
            var url = i.path("url").asText()
            pr.url = "https://github.com/${url.substring(29, url.length)}"
            pr.status = i.path("state").asText()
            list.add(pr)
        }
        return list
    }

    fun getRepositoryTags(owner: String, nameRepository: String): MutableList<Tag>? {
        val mapper = ObjectMapper()
        val root: JsonNode = mapper.readTree(executeRequest(owner, nameRepository, "tags").body)
        val list = mutableListOf<Tag>()
        for (i in root) {
            val tag = Tag()
            tag.nodeId = i.path("node_id").asText()
            tag.name = i.path("name").asText()
            tag.zipUrl = "https://github.com/$owner/$nameRepository/archive/refs/tags/${tag.name}.zip"
            tag.tarUrl = "https://github.com/$owner/$nameRepository/archive/refs/tags/${tag.name}.tar.gz"
            list.add(tag)
        }
        return list
    }

    fun getRepositoryBranches(ownerRepository: String, nameRepository: String): MutableList<Branch>? {
        val token = System.getenv("TOKEN")
        validation(ownerRepository, nameRepository)
        val url = "https://api.github.com/repos/$ownerRepository/$nameRepository/branches"
        val headers = HttpHeaders()
        headers.set("Accept", "application/vnd.github+json")
        headers.set("Authorization", "Bearer $token")
        val request: HttpEntity<*> = HttpEntity<Any?>(headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            url, HttpMethod.GET, request,
            String::class.java
        )
        val mapper = ObjectMapper()
        val root: JsonNode = mapper.readTree(response.body)
        val list = mutableListOf<Branch>()
        for (i in root) {
            val branch = Branch()
            branch.name = i.path("name").asText()
            list.add(branch)
        }
        return list
    }

    private fun executeRequest(
        ownerRepository: String,
        nameRepository: String,
        operation: String
    ): ResponseEntity<String> {
        val token = System.getenv("TOKEN")
        validation(ownerRepository, nameRepository)
        val url = "https://api.github.com/repos/$ownerRepository/$nameRepository/$operation?state=all&direction=asc"
        val headers = HttpHeaders()
        headers.set("Accept", "application/vnd.github+json")
        headers.set("Authorization", "Bearer $token")
        val request: HttpEntity<*> = HttpEntity<Any?>(headers)
        return restTemplate.exchange(
            url, HttpMethod.GET, request,
            String::class.java
        )
    }

    fun getRepositoryCommits(owner: String, nameRepository: String): MutableList<Commit>? {
        val mapper = ObjectMapper()
        val root: JsonNode = mapper.readTree(executeRequest(owner, nameRepository, "commits").body)
        val list = mutableListOf<Commit>()
        for (i in root) {
            val commit = Commit()
            commit.nodeId = i.path("node_id").asText()
            commit.name = i.path("commit").path("message").asText()
            commit.url = i.path("html_url").asText()
            list.add(commit)
        }
        return list
    }


    fun getRepository(ownerRepository: String, nameRepository: String): Any {
        try {
            val token = System.getenv("TOKEN")
            validation(ownerRepository, nameRepository)
            val url = "https://api.github.com/repos/$ownerRepository/$nameRepository"
            val headers = HttpHeaders()
            headers.set("Accept", "application/vnd.github+json")
            headers.set("Authorization", "Bearer $token")
            val request: HttpEntity<*> = HttpEntity<Any?>(headers)
            val repository = restTemplate.exchange(
                url, HttpMethod.GET, request,
                String::class.java
            )
            val mapper = ObjectMapper()
            val root: JsonNode = mapper.readTree(repository.body)
            return root.get("id").asLong()
        } catch (e: HttpClientErrorException.NotFound) {
            throw InvalidAttributeValueException("Owner or repository not found")
        }
    }

    private fun validation(ownerRepository: String, nameRepository: String) {
        if (ownerRepository.isNullOrBlank()) throw InvalidAttributeValueException("Created repository cannot be empty")
        if (ownerRepository.isNullOrBlank()) throw InvalidAttributeValueException("Name repository cannot be empty")
        if (nameRepository.isNullOrBlank()) throw InvalidAttributeValueException("Name repository cannot be empty")
        if (Validator.containsSpecialCharacterGithub(nameRepository)) throw InvalidAttributeValueException("The name repository cannot contain special characters except - and _")
    }
}


