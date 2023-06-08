package unq.pds.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import io.swagger.v3.oas.annotations.media.Schema
import unq.pds.model.exceptions.GroupWithEmptyMemberException
import javax.management.InvalidAttributeValueException
import javax.persistence.*

@Entity
@Table(name = "groupApp")
@JsonPropertyOrder("id", "name", "members", "projects")
class Group(name: String) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    @Schema(example = "1")
    private var id: Long? = null

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

    @Schema(example = "[]")
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JsonProperty
    var projects: MutableSet<Project> = mutableSetOf()

    fun addMember(member: Student) {
        if (isMember(member)) throw CloneNotSupportedException("The member is already in the group")
        members.add(member)
    }

    fun removeMember(member: Student) {
        if (!isMember(member)) throw NoSuchElementException("The member is not in the group")
        if (members.count() == 1) return throw GroupWithEmptyMemberException()
        members.remove(member)
    }

    fun addProject(project: Project) {
        if (isMyProject(project)) throw CloneNotSupportedException("The project has already been added")
        projects.add(project)
    }

    private fun isMyProject(project: Project): Boolean {
        return projects.any { it.name == project.name }
    }

    fun hasAMemberWithEmail(email: String): Boolean {
        return members.any { it.getEmail() == email }
    }

    init {
        this.validateCreation()
    }

    private fun validateCreation() {
        validateName(name)
    }

    private fun validateName(name: String) {
        if (name.isNullOrBlank()) throw InvalidAttributeValueException("Name cannot be empty")
    }

    private fun isMember(member: Student): Boolean {
        return members.any { it.getEmail() == member.getEmail() }
    }

    fun getId(): Long? {
        return id
    }
}