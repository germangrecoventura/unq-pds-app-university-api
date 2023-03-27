package unq.pds.persistence

import org.springframework.data.jpa.repository.JpaRepository
import unq.pds.model.Matter

interface MatterDAO : JpaRepository<Matter, Long>