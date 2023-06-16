package unq.pds.services

import unq.pds.api.dtos.ProjectDTO
import unq.pds.model.Project

interface ProjectService {
    fun save(project: Project): Project
    fun update(project: ProjectDTO): Project
    fun read(projectId: Long): Project
    fun delete(projectId: Long)
    fun addRepository(projectId: Long, repositoryId: Long): Project
    fun addDeployInstance(projectId: Long, deployInstanceId: Long): Project
    fun thereIsACommissionWhereIsteacherAndTheProjectExists(teacherEmail: String, projectId: Long): Boolean
    fun thereIsACommissionWhereIsteacherAndTheRepositoryExists(teacherEmail: String, repositoryId: Long): Boolean
    fun thereIsAGroupWhereIsStudentAndTheProjectExists(studentEmail: String, projectId: Long): Boolean
    fun isFoundRepository(projectId: Long, name: String): Boolean
    fun readAll(): List<Project>
    fun count(): Int
    fun clearProjects()
}