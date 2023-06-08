package unq.pds.services

import unq.pds.api.dtos.ProjectDTO
import unq.pds.model.Project

interface ProjectService {
    fun save(project: Project): Project
    fun update(project: ProjectDTO): Project
    fun read(projectId: Long): Project
    fun delete(projectId: Long)
    fun addRepository(projectId: Long, repositoryId: Long): Project
    fun ThereIsACommissionWhereIsteacherAndTheProjectExists(teacherEmail: String, projectId: Long): Boolean
    fun ThereIsAGroupWhereIsStudentAndTheProjectExists(studentEmail: String, projectId: Long): Boolean
    fun readAll(): List<Project>
    fun count(): Int
    fun clearProjects()
}