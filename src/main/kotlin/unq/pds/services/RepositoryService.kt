package unq.pds.services

import unq.pds.api.dtos.RepositoryDTO
import unq.pds.model.Commit
import unq.pds.model.Issue
import unq.pds.model.PullRequest
import unq.pds.model.Repository

interface RepositoryService {
    fun save(repositoryDTO: RepositoryDTO): Repository
    fun update(repositoryDTO: RepositoryDTO): Repository
    fun findById(repositoryId: Long): Repository
    fun findByName(name: String): Repository
    fun findByAll(): List<Repository>
    fun lengthPagesPaginatedCommit(name: String, size: Int): Int
    fun findPaginatedCommit(name: String,page: Int, size: Int): List<Commit>
    fun lengthPagesPaginatedIssue(name: String, size: Int): Int
    fun findPaginatedIssue(name: String,page: Int, size: Int): List<Issue>
    fun lengthPagesPaginatedPullRequest(name: String, size: Int): Int
    fun findPaginatedPullRequest(name: String,page: Int, size: Int): List<PullRequest>
    fun deleteById(repositoryId: Long)
    fun count(): Int
    fun clearRepositories()
}