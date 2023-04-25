package unq.pds.model

import com.fasterxml.jackson.annotation.*
import io.swagger.v3.oas.annotations.media.Schema
import javax.management.InvalidAttributeValueException
import javax.persistence.*

@Entity
@Table(name = "groupApp")
@JsonPropertyOrder("id", "name", "members", "projects")
class Group (name: String): ProjectOwner() {

    @Column(nullable = false)
    @JsonProperty
    @Schema(example = "Group 1")
    var name = name
        set(value) {
            this.validateName(value)
            field = value
        }

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonProperty
    var members: MutableSet<Student> = mutableSetOf()

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