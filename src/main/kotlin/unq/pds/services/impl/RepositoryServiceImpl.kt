package unq.pds.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.api.GithubApi
import unq.pds.api.dtos.RepositoryDTO
import unq.pds.model.Repository
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
        val repositoryId = githubApi.getRepository(repositoryDTO.owner!!, repositoryDTO.name!!)
        if (repositoryDAO.existsById(repositoryId as Long)) throw AlreadyRegisteredException("repository")

        val issues = githubApi.getRepositoryIssues(repositoryDTO.owner!!, repositoryDTO.name!!)
        val pullRequests = githubApi.getRepositoryPulls(repositoryDTO.owner!!, repositoryDTO.name!!)
        val tags = githubApi.getRepositoryTags(repositoryDTO.owner!!, repositoryDTO.name!!)
        val branches = githubApi.getRepositoryBranches(repositoryDTO.owner!!, repositoryDTO.name!!)
        val commits = githubApi.getRepositoryCommits(repositoryDTO.owner!!, repositoryDTO.name!!)

        val repository = Repository(repositoryId, repositoryDTO.name!!, repositoryDTO.owner!!)
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