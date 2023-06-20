package unq.pds.services

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.api.dtos.ProjectDTO
import unq.pds.model.builder.DeployInstanceBuilder.Companion.aDeployInstance
import unq.pds.model.builder.CommissionBuilder
import unq.pds.model.builder.MatterBuilder
import unq.pds.model.builder.ProjectBuilder.Companion.aProject
import unq.pds.model.exceptions.RepositoryHasAlreadyBeenAddedException
import unq.pds.services.builder.BuilderGroupDTO
import unq.pds.services.builder.BuilderProjectDTO.Companion.aProjectDTO
import unq.pds.services.builder.BuilderRepositoryDTO.Companion.aRepositoryDTO
import unq.pds.services.builder.BuilderStudentDTO
import unq.pds.services.builder.BuilderTeacherDTO

@SpringBootTest
class ProjectServiceTest {

    @Autowired
    lateinit var projectService: ProjectService

    @Autowired
    lateinit var repositoryService: RepositoryService

    @Autowired
    lateinit var studentService: StudentService

    @Autowired
    lateinit var deployInstanceService: DeployInstanceService

    @Autowired
    lateinit var commissionService: CommissionService

    @Autowired
    lateinit var matterService: MatterService

    @Autowired
    lateinit var teacherService: TeacherService

    @Autowired
    lateinit var groupService: GroupService

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
        val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        Assertions.assertEquals(0, project.repositories.size)
        val projectWithARepository = projectService.addRepository(project.getId()!!, repository.id)
        Assertions.assertEquals(1, projectWithARepository.repositories.size)
    }

    @Test
    fun `should throw an exception when trying to add the same repository to a project twice and both exist`() {
        val project = projectService.save(aProject().build())
        val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
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
        val project = projectService.save(aProject().build())
        val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        try {
            projectService.addRepository(-1, repository.id)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no project with that id", e.message)
        }
    }

    @Test
    fun `should add a deploy instance to a project when it was not previously added and both exist`() {
        val project = projectService.save(aProject().build())
        val deployInstance = deployInstanceService.save(aDeployInstance().build())
        Assertions.assertEquals(0, project.deployInstances.size)
        val projectWithADeployInstance = projectService.addDeployInstance(project.getId()!!, deployInstance.getId()!!)
        Assertions.assertEquals(1, projectWithADeployInstance.deployInstances.size)
    }

    @Test
    fun `should throw an exception when trying to add the same deploy instance to a project twice and both exist`() {
        val project = projectService.save(aProject().build())
        val deployInstance = deployInstanceService.save(aDeployInstance().build())
        projectService.addDeployInstance(project.getId()!!, deployInstance.getId()!!)

        val thrown: CloneNotSupportedException? =
            Assertions.assertThrows(CloneNotSupportedException::class.java) {
                projectService.addDeployInstance(
                    project.getId()!!,
                    deployInstance.getId()!!
                )
            }

        Assertions.assertEquals(
            "The deploy instance is already in the project",
            thrown!!.message
        )
    }

    @Test
    fun `should be true to have a commission with a teacher with email and a repository with id when both were added previously`() {
        matterService.save(MatterBuilder.aMatter().build())
        val commission = commissionService.save(CommissionBuilder.aCommission().build())
        val teacher = teacherService.save(BuilderTeacherDTO.aTeacherDTO().build())
        val student = studentService.save(BuilderStudentDTO.aStudentDTO().withEmail("test@gmail.com").build())
        val project = projectService.save(aProject().build())
        val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
        projectService.addRepository(project.getId()!!, repository.id)
        val group = groupService.save(BuilderGroupDTO.aGroupDTO().withMembers(listOf("test@gmail.com")).build())
        groupService.addProject(group.getId()!!, project.getId()!!)
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        commissionService.addStudent(commission.getId()!!, student.getId()!!)
        commissionService.addGroup(commission.getId()!!, group.getId()!!)
        Assertions.assertTrue(projectService.thereIsACommissionWhereIsteacherAndTheRepositoryExists(
            teacher.getEmail()!!, repository.id))
    }

    @Test
    fun `should be false to have a commission with a teacher with email and a repository with id when both were not added`() {
        matterService.save(MatterBuilder.aMatter().build())
        commissionService.save(CommissionBuilder.aCommission().build())
        Assertions.assertFalse(
            projectService.thereIsACommissionWhereIsteacherAndTheRepositoryExists(
                "emailFalso",
                -1
            )
        )
    }

    @Test
    fun `should throw an exception when trying to add a deploy instance to a project and the deploy instance does not exist`() {
        val project = projectService.save(aProject().build())
        try {
            projectService.addDeployInstance(project.getId()!!, -1)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("Not found the deploy instance with id -1", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to add a deploy instance to a project and the project does not exist`() {
        val deployInstance = deployInstanceService.save(aDeployInstance().build())
        try {
            projectService.addDeployInstance(-1, deployInstance.getId()!!)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no project with that id", e.message)
        }
    }

    @Test
    fun `should be false to have a commission with a teacher with email and a repository with id when there is no commissions`() {
        Assertions.assertFalse(
            projectService.thereIsACommissionWhereIsteacherAndTheRepositoryExists(
                "emailFalso",
                -1
            )
        )
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

    @AfterEach
    fun tearDown() {
        commissionService.clearCommissions()
        groupService.clearGroups()
        studentService.clearStudents()
        teacherService.clearTeachers()
        matterService.clearMatters()
        projectService.clearProjects()
        repositoryService.clearRepositories()
        deployInstanceService.clearDeployInstances()
    }
}