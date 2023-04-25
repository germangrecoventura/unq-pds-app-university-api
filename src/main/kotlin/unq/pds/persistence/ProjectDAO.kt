package unq.pds.persistence

import org.springframework.data.jpa.repository.JpaRepository
import unq.pds.model.Project

interface ProjectDAO : JpaRepository<Project, Long>