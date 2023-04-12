package unq.pds.model

import javax.management.InvalidAttributeValueException
import javax.persistence.*

@Entity
@Table(name = "grupo")
class Group(
    name: String
) {
    @Column(nullable = false)
    var name = name
        set(value) {
            this.validateName(value)
            field = value
        }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    @ManyToMany
    var members: MutableSet<Student> = mutableSetOf()

    @Column(nullable = true)
    var repository: String? = null

    fun addMember(member: Student) {
        if (isMember(member)) throw CloneNotSupportedException("The member is already in the group")
        members.add(member)
    }

    fun removeMember(member: Student) {
        if (!isMember(member)) throw NoSuchElementException("The member is not in the group")
        members.remove(member)
    }

    init { this.validateCreation() }

    private fun validateCreation() {
        validateName(name)
    }

    private fun validateName(name: String) {
        if (name.isBlank()) throw InvalidAttributeValueException("Name cannot be empty")
    }

    private fun isMember(member: Student): Boolean {
        return members.any { it.getEmail() == member.getEmail() }
    }
}