package unq.pds.persistence

import org.springframework.data.jpa.repository.JpaRepository
import unq.pds.model.Group

interface GroupDAO : JpaRepository<Group, Long>