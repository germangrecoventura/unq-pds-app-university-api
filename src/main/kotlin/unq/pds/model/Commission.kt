package unq.pds.model

import javax.management.InvalidAttributeValueException
import javax.persistence.*

@Entity
@Table(name = "commission")
class Commission(
    @Column(nullable = false) private var year: Int,
    @Column(nullable = false) private var fourMonthPeriod: FourMonthPeriod,
    @ManyToOne private var matter: Matter
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    @ManyToMany(fetch = FetchType.EAGER)
    var students: MutableSet<Student> = mutableSetOf()

    @ManyToMany(fetch = FetchType.EAGER)
    var teachers: MutableSet<Teacher> = mutableSetOf()

    @ManyToMany(fetch = FetchType.EAGER)
    var groupsStudents: MutableSet<Group> = mutableSetOf()

    fun getYear() = year

    fun getFourMonthPeriod() = fourMonthPeriod

    fun getMatter() = matter

    init { this.validateCreation() }

    private fun validateCreation() {
        validateYear()
    }

    private fun validateYear() {
        if (year < 2000) throw InvalidAttributeValueException("Year should be greater than or equal to 2000")
    }
}