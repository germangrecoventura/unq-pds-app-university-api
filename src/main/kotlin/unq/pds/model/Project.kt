package unq.pds.model

import com.fasterxml.jackson.annotation.*
import io.swagger.v3.oas.annotations.media.Schema
import org.jasypt.util.text.AES256TextEncryptor
import javax.management.InvalidAttributeValueException
import javax.persistence.*

@Entity
@Table(name = "Project")
@JsonPropertyOrder("id", "name", "repositories")
class Project(
    name: String,
    @Column(nullable = true)
    @JsonProperty @field:Schema(example = "germangrecoventura") private var ownerGithub: String? = null,
    @Column(nullable = true)
    @JsonProperty @field:Schema(example = "") private var tokenGithub: String? = null
) {
    @Column(nullable = false)
    @JsonProperty
    @Schema(example = "unq pds")
    var name = name
        set(value) {
            this.validateName(value)
            field = value
        }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    @Schema(example = "1")
    private var id: Long? = null

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var repositories: MutableSet<Repository> = mutableSetOf()

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var deployInstances: MutableSet<DeployInstance> = mutableSetOf()

    fun addRepository(repository: Repository) {
        if (isMyRepository(repository)) throw CloneNotSupportedException("The repository is already in the project")
        repositories.add(repository)
    }

    fun addDeployInstance(deployInstance: DeployInstance) {
        if (isMyDeployInstance(deployInstance)) throw CloneNotSupportedException("The deploy instance is already in the project")
        deployInstances.add(deployInstance)
    }

    fun getId() = id

    fun getOwnerGithub() = ownerGithub

    fun getTokenGithub() = tokenGithub

    fun setOwnerGithub(ownerGithub: String?) {
        this.ownerGithub = ownerGithub
    }

    fun setTokenGithub(token: String?) {
        val encryptor = AES256TextEncryptor()
        encryptor.setPassword(System.getenv("ENCRYPT_PASSWORD"))
        val myEncryptedToken = encryptor.encrypt(token)
        this.tokenGithub = myEncryptedToken
    }

    init { this.validateCreation() }

    private fun validateCreation() {
        validateName(name)
    }

    private fun validateName(name: String) {
        if (name.isNullOrBlank()) throw InvalidAttributeValueException("Name cannot be empty")
    }

    private fun isMyRepository(repository: Repository): Boolean {
        return repositories.any { it.name == repository.name }
    }

    private fun isMyDeployInstance(deployInstance: DeployInstance): Boolean {
        return deployInstances.any { it.name == deployInstance.name && it.url == deployInstance.url }
    }
}