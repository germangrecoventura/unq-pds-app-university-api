package unq.pds.persistence

import org.springframework.data.jpa.repository.JpaRepository
import unq.pds.model.DeployInstance

interface DeployInstanceDAO : JpaRepository<DeployInstance, Long>