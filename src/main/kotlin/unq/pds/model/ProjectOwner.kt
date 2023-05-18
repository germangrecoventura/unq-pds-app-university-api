package unq.pds.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract class ProjectOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    @Schema(example = "1")
    private var id: Long? = null

    @OneToMany(fetch = FetchType.EAGER)
    var projects: MutableSet<Project> = mutableSetOf()

    fun addProject(project: Project) {
        if (isMyProject(project)) throw CloneNotSupportedException("The project has already been added")
        projects.add(project)
    }

    fun getId() = id

    private fun isMyProject(project: Project): Boolean {
        return projects.any { it.name == project.name }
    }
}