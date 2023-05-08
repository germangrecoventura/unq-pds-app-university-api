package unq.pds.services.impl

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import unq.pds.api.Validator
import unq.pds.api.dtos.RepositoryDTO
import unq.pds.model.*
import unq.pds.model.exceptions.AlreadyRegisteredException
import unq.pds.model.exceptions.NotAuthenticatedException
import unq.pds.persistence.RepositoryDAO
import unq.pds.persistence.StudentDAO
import unq.pds.services.RepositoryService
import javax.management.InvalidAttributeValueException


@Service
@Transactional
open class RepositoryServiceImpl : RepositoryService {

    private var restTemplate: RestTemplate = RestTemplate()
    private var token: String = ""

    @Autowired
    private lateinit var repositoryDAO: RepositoryDAO

    @Autowired
    private lateinit var studentDAO: StudentDAO
    override fun save(repositoryDTO: RepositoryDTO): Repository {
        val repositoryFind = getRepository(repositoryDTO.owner!!, repositoryDTO.name!!)
        if (repositoryDAO.existsById(repositoryFind!!.get("id").asLong())) {
            token = ""
            throw AlreadyRegisteredException("repository")
        }
        val issues = getRepositoryIssues(repositoryDTO.owner!!, repositoryDTO.name!!)
        val pullRequests = getRepositoryPulls(repositoryDTO.owner!!, repositoryDTO.name!!)
        val tags = getRepositoryTags(repositoryDTO.owner!!, repositoryDTO.name!!)
        val branches = getRepositoryBranches(repositoryDTO.owner!!, repositoryDTO.name!!)
        val commits = getRepositoryCommits(repositoryDTO.owner!!, repositoryDTO.name!!)

        val repository = Repository(
            repositoryFind.get("id").asLong(),
            repositoryDTO.name!!,
            repositoryDTO.owner!!,
            repositoryFind.get("html_url").asText()
        )
        repository.issues = issues!!
        repository.pullRequests = pullRequests!!
        repository.tags = tags!!
        repository.branches = branches!!
        repository.commits = commits!!

        token = ""

        return repositoryDAO.save(repository)
    }

    override fun update(repositoryDTO: RepositoryDTO): Repository {
        val repositoryFind = getRepository(repositoryDTO.owner!!, repositoryDTO.name!!)
        if (!repositoryDAO.existsById(repositoryFind!!.get("id").asLong())) {
            token = ""
            throw NoSuchElementException("Repository does not exist")
        }
        val issues = getRepositoryIssues(repositoryDTO.owner!!, repositoryDTO.name!!)
        val pullRequests = getRepositoryPulls(repositoryDTO.owner!!, repositoryDTO.name!!)
        val tags = getRepositoryTags(repositoryDTO.owner!!, repositoryDTO.name!!)
        val branches = getRepositoryBranches(repositoryDTO.owner!!, repositoryDTO.name!!)
        val commits = getRepositoryCommits(repositoryDTO.owner!!, repositoryDTO.name!!)

        val repository = Repository(
            repositoryFind.get("id").asLong(),
            repositoryDTO.name!!,
            repositoryDTO.owner!!,
            repositoryFind.get("html_url").asText()
        )
        repository.issues = issues!!
        repository.pullRequests = pullRequests!!
        repository.tags = tags!!
        repository.branches = branches!!
        repository.commits = commits!!
        repository.commentsTeacher = repositoryDAO.commentsFfromId(repositoryFind!!.get("id").asLong()).toMutableList()

        token = ""

        return repositoryDAO.save(repository)
    }

    override fun findById(repositoryId: Long): Repository {
        return repositoryDAO.findById(repositoryId)
            .orElseThrow { NoSuchElementException("Not found the repository with id $repositoryId") }
    }

    override fun findByName(name: String): Repository {
        return repositoryDAO.findByName(name)
            .orElseThrow { kotlin.NoSuchElementException("Not found the repository with name $name") }
    }

    override fun findByAll(): List<Repository> {
        return repositoryDAO.findAll().toList()
    }

    override fun deleteById(repositoryId: Long) {
        if (repositoryDAO.existsById(repositoryId)) repositoryDAO.deleteById(repositoryId)
        else throw NoSuchElementException("The repository with id $repositoryId is not registered")
    }


    override fun count(): Int {
        return repositoryDAO.count().toInt()
    }

    override fun clearRepositories() {
        repositoryDAO.deleteAll()
    }


    private fun getRepositoryIssues(ownerGithub: String, nameRepository: String): MutableList<Issue>? {
        val mapper = ObjectMapper()
        val root: JsonNode = mapper.readTree(
            executeRequest(
                ownerGithub,
                nameRepository,
                token,
                "issues"
            ).body
        )
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

    private fun getRepositoryPulls(ownerGithub: String, nameRepository: String): MutableList<PullRequest>? {
        val mapper = ObjectMapper()
        val root: JsonNode = mapper.readTree(
            executeRequest(
                ownerGithub,
                nameRepository,
                token,
                "pulls"
            ).body
        )
        val list = mutableListOf<PullRequest>()
        for (i in root) {
            val pr = PullRequest()
            pr.id = i.path("id").asInt()
            pr.url = i.path("html_url").asText()
            pr.status = i.path("state").asText()
            pr.title = i.path("title").asText()
            list.add(pr)
        }
        return list
    }

    private fun getRepositoryTags(ownerGithub: String, nameRepository: String): MutableList<Tag>? {
        val mapper = ObjectMapper()
        val root: JsonNode = mapper.readTree(
            executeRequest(
                ownerGithub,
                nameRepository,
                token,
                "tags"
            ).body
        )
        val list = mutableListOf<Tag>()
        for (i in root) {
            val tag = Tag()
            tag.nodeId = i.path("node_id").asText()
            tag.name = i.path("name").asText()
            tag.zipUrl = "https://github.com/$ownerGithub/$nameRepository/archive/refs/tags/${tag.name}.zip"
            tag.tarUrl = "https://github.com/$ownerGithub/$nameRepository/archive/refs/tags/${tag.name}.tar.gz"
            list.add(tag)
        }
        return list
    }

    private fun getRepositoryBranches(ownerRepository: String, nameRepository: String): MutableList<Branch>? {
        val url = "https://api.github.com/repos/$ownerRepository/$nameRepository/branches"
        val response: ResponseEntity<String> = makeRequest(url, token)
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
        token: String,
        operation: String
    ): ResponseEntity<String> {
        validation(ownerRepository, nameRepository)
        val url = "https://api.github.com/repos/$ownerRepository/$nameRepository/$operation?state=all"
        return makeRequest(url, token)
    }

    private fun getRepositoryCommits(ownerRepository: String, nameRepository: String): MutableList<Commit>? {
        val mapper = ObjectMapper()
        val root: JsonNode = mapper.readTree(
            executeRequest(
                ownerRepository,
                nameRepository,
                token,
                "commits"
            ).body
        )
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


    private fun getRepository(ownerRepository: String, nameRepository: String): JsonNode? {
        try {
            validation(ownerRepository, nameRepository)
            val owner = studentDAO.findByOwnerGithub(ownerRepository)
            if (owner.isEmpty) throw InvalidAttributeValueException("The student with owner $ownerRepository is not registered")
            if (owner.get().getTokenGithub()
                    .isNullOrBlank()
            ) throw InvalidAttributeValueException("The student with token is not registered")
            token = owner.get().getTokenGithub()!!
            val url = "https://api.github.com/repos/$ownerRepository/$nameRepository"
            val repository = makeRequest(url, token)
            val mapper = ObjectMapper()
            return mapper.readTree(repository.body)
        } catch (e: HttpClientErrorException.NotFound) {
            throw InvalidAttributeValueException("Owner or repository not found")
        } catch (e: HttpClientErrorException.Unauthorized) {
            throw NotAuthenticatedException()
        }
    }

    private fun makeRequest(url: String, token: String): ResponseEntity<String> {
        val headers = HttpHeaders()
        headers.set("Accept", "application/vnd.github+json")
        headers.set("Authorization", "Bearer $token")
        val request: HttpEntity<*> = HttpEntity<Any?>(headers)
        return restTemplate.exchange(
            url, HttpMethod.GET, request,
            String::class.java
        )
    }

    private fun validation(ownerRepository: String, nameRepository: String) {
        if (ownerRepository.isNullOrBlank()) throw InvalidAttributeValueException("Repository owner cannot be empty")
        if (nameRepository.isNullOrBlank()) throw InvalidAttributeValueException("Repository name cannot be empty")
        if (Validator.containsSpecialCharacterGithub(nameRepository)) throw InvalidAttributeValueException("The repository name cannot contain special characters except - and _")
    }
}