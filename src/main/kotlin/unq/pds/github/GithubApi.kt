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
            var url = element["url"]!!
            issue.url = "https://github.com/${url.substring(29, url.length)}"
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
            var url = element["url"]!!
            pr.url = "https://github.com/${url.substring(29, url.length)}"
            pr.status = element["state"]!!
            list.add(pr)
        }
        return list
    }

    fun getRepositoryTags(owner: String, nameRepository: String): MutableList<Tag>? {
        val objects: Array<Any>? = executeRequest(owner, nameRepository, "tags").body
        val list = mutableListOf<Tag>()
        for (i in objects!!.indices) {
            val element = objects[i] as LinkedHashMap<String, String>
            val tag = Tag()
            tag.nodeId = element["node_id"]!!
            tag.name = element["name"]!!
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
        val token = System.getenv("TOKEN")
        validation(ownerRepository, nameRepository)
        val url = "https://api.github.com/repos/$ownerRepository/$nameRepository/$operation?state=all&direction=asc"
        val headers = HttpHeaders()
        headers.set("Accept", "application/vnd.github+json")
        headers.set("Authorization", "Bearer $token")
        val request: HttpEntity<*> = HttpEntity<Any?>(headers)
        return restTemplate.exchange(
            url, HttpMethod.GET, request,
            Array<Any>::class.java
        )
    }

    fun getRepositoryCommits(owner: String, nameRepository: String): MutableList<Commit>? {
        val objects: Array<Any>? = executeRequest(owner, nameRepository, "commits").body
        val list = mutableListOf<Commit>()
        for (i in objects!!.indices) {
            val element = objects[i] as LinkedHashMap<String, String>
            val commit = Commit()
            commit.nodeId = element["node_id"]!!
            var commitFind = element["commit"]!! as LinkedHashMap<String, String>
            commit.name = commitFind["message"]!!
            commit.url = element["html_url"]!!
            list.add(commit)
        }
        return list
    }

    private fun validation(ownerRepository: String, nameRepository: String) {
        if (ownerRepository.isNullOrBlank()) throw InvalidAttributeValueException("Created repository cannot be empty")
        if (ownerRepository.isNullOrBlank()) throw InvalidAttributeValueException("Name repository cannot be empty")
        if (nameRepository.isNullOrBlank()) throw InvalidAttributeValueException("Name repository cannot be empty")
        if (Validator.containsSpecialCharacterGithub(nameRepository)) throw InvalidAttributeValueException("The name repository cannot contain special characters except - and _")
    }
}


