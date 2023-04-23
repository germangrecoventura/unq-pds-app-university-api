package unq.pds.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.api.dtos.RepositoryDTO
import unq.pds.github.GithubApi
import unq.pds.github.Repository
import unq.pds.model.exceptions.AlreadyRegisteredException
import unq.pds.persistence.RepositoryDAO
import unq.pds.services.RepositoryService
import java.util.*


@Service
@Transactional
open class RepositoryServiceImpl : RepositoryService {

    @Autowired
    private lateinit var repositoryDAO: RepositoryDAO

    @Autowired
    private lateinit var githubApi: GithubApi
    override fun save(repositoryDTO: RepositoryDTO): Repository {
        if (repositoryDAO.existsById(repositoryDTO.id)) throw AlreadyRegisteredException("repository")
        if (repositoryDAO.findByName(repositoryDTO.name!!).isPresent) throw CloneNotSupportedException("The name ${repositoryDTO.name} is already registered")

        val issues = githubApi.getRepositoryIssues(repositoryDTO.created!!, repositoryDTO.name!!)
        val pullRequests = githubApi.getRepositoryPulls(repositoryDTO.created!!, repositoryDTO.name!!)
        val tags = githubApi.getRepositoryTags(repositoryDTO.created!!, repositoryDTO.name!!)
        val branches = githubApi.getRepositoryBranches(repositoryDTO.created!!, repositoryDTO.name!!)
        val commits = githubApi.getRepositoryCommits(repositoryDTO.created!!, repositoryDTO.name!!)

        val repository = Repository(repositoryDTO.id!!, repositoryDTO.name!!, repositoryDTO.created!!)
        repository.issues = issues!!
        repository.pullRequests = pullRequests!!
        repository.tags = tags!!
        repository.branches = branches!!
        repository.commits = commits!!

        return repositoryDAO.save(repository)
    }

    override fun update(repository: Repository): Repository {
        var repositoryRecovery = findById(repository.id)
        repositoryRecovery.name = repository.name
        return repositoryDAO.save(repositoryRecovery)
    }

    override fun findById(repositoryId: Long): Repository {
        return repositoryDAO.findById(repositoryId)
            .orElseThrow { NoSuchElementException("Not found the repository with id $repositoryId") }
    }

    override fun findByName(name: String): Optional<Repository> {
        return repositoryDAO.findByName(name)
    }

    override fun findByAll(): List<Repository> {
        return repositoryDAO.findAll().toList()
    }

    override fun deleteById(repositoryId: Long) {
        if (repositoryDAO.existsById(repositoryId)) repositoryDAO.deleteById(repositoryId)
        else throw NoSuchElementException("The repository with id -1 is not registered")
    }


    override fun count(): Int {
        return repositoryDAO.count().toInt()
    }

    override fun clearRepositories() {
        repositoryDAO.deleteAll()
    }
}