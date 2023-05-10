package unq.pds.services

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.Initializer
import unq.pds.api.dtos.ProjectDTO
import unq.pds.model.builder.ProjectBuilder.Companion.aProject
import unq.pds.model.exceptions.RepositoryHasAlreadyBeenAddedException
import unq.pds.services.builder.BuilderProjectDTO.Companion.aProjectDTO
import unq.pds.services.builder.BuilderRepositoryDTO.Companion.aRepositoryDTO
import unq.pds.services.builder.BuilderStudentDTO

@SpringBootTest
class ProjectServiceTest {

    @Autowired
    lateinit var projectService: ProjectService

    @Autowired
    lateinit var repositoryService: RepositoryService

    @Autowired
    lateinit var initializer: Initializer

    private var token: String = ""

    @Autowired
    lateinit var studentService: StudentService

    @BeforeEach
    fun tearDown() {
        initializer.cleanDataBase()
    }

    @Test
    fun `should be create a project when it has valid credentials`() {
        val project = projectService.save(aProject().build())
        Assertions.assertNotNull(project.getId())
    }

    @Test
    fun `should recover a project when it exists`() {
        val project = projectService.save(aProject().build())
        val recoverProject = projectService.read(project.getId()!!)
        Assertions.assertEquals(project.getId(), recoverProject.getId())
        Assertions.assertEquals(project.name, recoverProject.name)
    }

    @Test
    fun `should throw an exception when trying to recover a project with an invalid id`() {
        try {
            projectService.read(-1)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no project with that id", e.message)
        }
    }

    @Test
    fun `should update a project when it exists`() {
        val project = projectService.save(aProject().build())
        val projectDTO = ProjectDTO()
        projectDTO.id = project.getId()
        projectDTO.name = "unq-pdes-app"
        val updatedProject = projectService.update(projectDTO)
        Assertions.assertEquals(projectDTO.name, updatedProject.name)
    }

    @Test
    fun `should throw an exception when trying to update a project without persisting`() {
        try {
            projectService.update(aProjectDTO().build())
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("Project does not exist", e.message)
        }
    }

    @Test
    fun `should delete a project when it exists`() {
        val project = projectService.save(aProject().build())
        projectService.delete(project.getId()!!)
        Assertions.assertEquals(0, projectService.count())
    }

    @Test
    fun `should throw an exception when trying to delete a project with an invalid id`() {
        try {
            projectService.delete(-1)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no project with that id", e.message)
        }
    }

    @Test
    fun `should add a repository to a project when it was not previously added and both exist`() {
        val project = projectService.save(aProject().build())
        studentService.save(BuilderStudentDTO.aStudentDTO().withTokenGithub(token).build())
        val repository = repositoryService.save(aRepositoryDTO().build())
        Assertions.assertEquals(0, project.repositories.size)
        val projectWithARepository = projectService.addRepository(project.getId()!!, repository.id)
        Assertions.assertEquals(1, projectWithARepository.repositories.size)
    }

    @Test
    fun `should throw an exception when trying to add the same repository to a project twice and both exist`() {
        val project = projectService.save(aProject().build())
        studentService.save(BuilderStudentDTO.aStudentDTO().withTokenGithub(token).build())
        val repository = repositoryService.save(aRepositoryDTO().build())
        projectService.addRepository(project.getId()!!, repository.id)

        val thrown: RepositoryHasAlreadyBeenAddedException? =
            Assertions.assertThrows(RepositoryHasAlreadyBeenAddedException::class.java) {
                projectService.addRepository(
                    project.getId()!!,
                    repository.id
                )
            }

        Assertions.assertEquals(
            "The repository has already been added to a project",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception when trying to add a repository to a project and the repository does not exist`() {
        val project = projectService.save(aProject().build())
        try {
            projectService.addRepository(project.getId()!!, -1)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("Not found the repository with id -1", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to add a repository to a project and the project does not exist`() {
        studentService.save(BuilderStudentDTO.aStudentDTO().withTokenGithub(token).build())
        val repository = repositoryService.save(aRepositoryDTO().build())
        try {
            projectService.addRepository(-1, repository.id)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no project with that id", e.message)
        }
    }

    @Test
    fun `should recover an empty list of projects when recover all and there is no persistence`() {
        Assertions.assertEquals(0, projectService.readAll().size)
    }

    @Test
    fun `should recover a list with two projects when recover all and there are exactly two persisted`() {
        projectService.save(aProject().build())
        projectService.save(aProject().withName("unq-pdes-app").build())
        val projects = projectService.readAll()

        Assertions.assertEquals(2, projects.size)
        Assertions.assertTrue(projects.any { it.name == "unq-pds-app-university-api" })
        Assertions.assertTrue(projects.any { it.name == "unq-pdes-app" })
    }
}