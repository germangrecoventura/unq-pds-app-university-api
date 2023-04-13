package unq.pds.persistence

import org.springframework.data.jpa.repository.JpaRepository
import unq.pds.model.Commission

interface CommissionDAO : JpaRepository<Commission, Long>