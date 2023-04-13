package unq.pds.model

import javax.management.InvalidAttributeValueException
import javax.persistence.*

@Entity
@Table(name = "commission")
class Commission(
    @Column(nullable = false) private var year: Int,
    @Column(nullable = false) private var fourMonthPeriod: FourMonthPeriod,
    @Column(nullable = false) private var matter: Matter
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    @ManyToMany
    var students: MutableSet<Student> = mutableSetOf()

    @ManyToMany
    var groupsStudents: MutableSet<Group> = mutableSetOf()

    init { this.validateCreation() }

    private fun validateCreation() {
        validateYear()
    }

    private fun validateYear() {
        if (year < 2000) throw InvalidAttributeValueException("Year should be greater or equal to 2000")
    }
}