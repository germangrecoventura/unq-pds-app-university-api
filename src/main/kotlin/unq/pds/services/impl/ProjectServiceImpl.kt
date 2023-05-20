package unq.pds.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.pds.api.dtos.ProjectDTO
import unq.pds.model.Project
import unq.pds.model.exceptions.RepositoryHasAlreadyBeenAddedException
import unq.pds.persistence.ProjectDAO
import unq.pds.services.ProjectService
import unq.pds.services.RepositoryService

@Service
@Transactional
open class ProjectServiceImpl : ProjectService {

    @Autowired private lateinit var projectDAO: ProjectDAO
    @Autowired private lateinit var repositoryService: RepositoryService

    override fun save(project: Project): Project {
        return projectDAO.save(project)
    }

    override fun update(project: ProjectDTO): Project {
        if (project.id != null && projectDAO.existsById(project.id!!)) {
            val projectFind = projectDAO.findById(project.id!!).get()
            projectFind.name = project.name!!
            return projectDAO.save(projectFind)
        }
         else throw NoSuchElementException("Project does not exist")
    }

    override fun read(projectId: Long): Project {
        return projectDAO.findById(projectId).orElseThrow { NoSuchElementException("There is no project with that id") }
    }

    override fun delete(projectId: Long) {
        if (projectDAO.existsById(projectId)) projectDAO.deleteById(projectId)
         else throw NoSuchElementException("There is no project with that id")
    }

    override fun addRepository(projectId: Long, repositoryId: Long): Project {
        val project = this.read(projectId)
        val repository = repositoryService.findById(repositoryId)
        if (projectDAO.projectWithRepository(repository).isPresent) {
            throw RepositoryHasAlreadyBeenAddedException()
        }
        project.addRepository(repository)

        return projectDAO.save(project)
    }

    override fun readAll(): List<Project> {
        return projectDAO.findAll().toList()
    }

    override fun count(): Int {
        return projectDAO.count().toInt()
    }

    override fun clearProjects() {
        projectDAO.deleteAll()
    }
}