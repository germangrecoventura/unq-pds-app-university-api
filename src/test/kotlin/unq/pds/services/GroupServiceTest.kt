package unq.pds.services

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.Initializer
import unq.pds.api.dtos.GroupUpdateDTO
import unq.pds.model.builder.DeployInstanceBuilder.Companion.aDeployInstance
import unq.pds.model.builder.ProjectBuilder.Companion.aProject
import unq.pds.model.exceptions.ProjectAlreadyHasAnOwnerException
import unq.pds.services.builder.BuilderGroupDTO.Companion.aGroupDTO
import unq.pds.services.builder.BuilderStudentDTO.Companion.aStudentDTO

@SpringBootTest
class GroupServiceTest {

    @Autowired
    lateinit var groupService: GroupService

    @Autowired
    lateinit var studentService: StudentService

    @Autowired
    lateinit var projectService: ProjectService

    @Autowired
    lateinit var deployInstanceService: DeployInstanceService

    @Autowired
    lateinit var initializer: Initializer

    @BeforeEach
    fun tearDown() {
        initializer.cleanDataBase()
    }

    @Test
    fun `should be create a group when it has valid credentials`() {
        studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().build())
        Assertions.assertNotNull(group.getId())
    }

    @Test
    fun `should recover a group when it exists`() {
        studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().build())
        val recoverGroup = groupService.read(group.getId()!!)
        Assertions.assertEquals(group.getId(), recoverGroup.getId())
        Assertions.assertEquals(group.name, recoverGroup.name)
    }

    @Test
    fun `should throw an exception when trying to recover a group with an invalid id`() {
        try {
            groupService.read(-1)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no group with that id", e.message)
        }
    }

    @Test
    fun `should update a group when it exists`() {
        studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().build())
        val groupDTO = GroupUpdateDTO()
        groupDTO.id = group.getId()
        groupDTO.name = "Group 3"
        val updatedGroup = groupService.update(groupDTO)
        Assertions.assertEquals(groupDTO.name, updatedGroup.name)
    }

    @Test
    fun `should throw an exception when trying to update a group without persisting`() {
        try {
            groupService.update(GroupUpdateDTO())
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("Group does not exist", e.message)
        }
    }

    @Test
    fun `should delete a group when it exists`() {
        studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().build())
        groupService.delete(group.getId()!!)
        Assertions.assertEquals(0, groupService.count())
    }

    @Test
    fun `should throw an exception when trying to delete a group with an invalid id`() {
        try {
            groupService.delete(-1)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no group with that id", e.message)
        }
    }

    @Test
    fun `should add a member to a group when it was not previously added and both exist`() {
        studentService.save(aStudentDTO().withEmail("test@gmail.com").build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("test@gmail.com")).build())
        val member = studentService.save(aStudentDTO().build())
        Assertions.assertEquals(1, group.members.size)
        val groupWithAMember = groupService.addMember(group.getId()!!, member.getId()!!)
        Assertions.assertEquals(2, groupWithAMember.members.size)
    }

    @Test
    fun `should throw an exception when trying to add the same member to the group twice and both exist`() {
        studentService.save(aStudentDTO().withEmail("test@gmail.com").build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("test@gmail.com")).build())
        val member = studentService.save(aStudentDTO().build())
        groupService.addMember(group.getId()!!, member.getId()!!)
        try {
            groupService.addMember(group.getId()!!, member.getId()!!)
        } catch (e: CloneNotSupportedException) {
            Assertions.assertEquals("The member is already in the group", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to add a member to a group and the member does not exist`() {
        studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().build())
        try {
            groupService.addMember(group.getId()!!, -1)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("Not found the student with id -1", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to add a member to a group and the group does not exist`() {
        val member = studentService.save(aStudentDTO().build())
        try {
            groupService.addMember(-1, member.getId()!!)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no group with that id", e.message)
        }
    }

    @Test
    fun `should remove a member from a group when it was previously added and both exist`() {
        val student1 = studentService.save(aStudentDTO().withEmail("test@gmail.com").build())
        studentService.save(aStudentDTO().withEmail("german@gmail.com").build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("test@gmail.com","german@gmail.com")).build())
        Assertions.assertEquals(2, group.members.size)
        val groupWithoutMembers = groupService.removeMember(group.getId()!!, student1.getId()!!)
        Assertions.assertEquals(1, groupWithoutMembers.members.size)
    }

    @Test
    fun `should throw an exception when trying to remove a member who does not belong to the group and both exist`() {
        studentService.save(aStudentDTO().withEmail("test@gmail.com").build())
        val group = groupService.save(aGroupDTO().withMembers(listOf("test@gmail.com")).build())
        val member = studentService.save(aStudentDTO().build())
        try {
            groupService.removeMember(group.getId()!!, member.getId()!!)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("The member is not in the group", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to remove a member of a group and the member does not exist`() {
        studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().build())
        try {
            groupService.removeMember(group.getId()!!, -1)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("Not found the student with id -1", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to remove a member of a group and the group does not exist`() {
        val member = studentService.save(aStudentDTO().build())
        try {
            groupService.removeMember(-1, member.getId()!!)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no group with that id", e.message)
        }
    }

    @Test
    fun `should add a project to a group when it was not previously added and both exist`() {
        studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().build())
        val project = projectService.save(aProject().build())
        Assertions.assertEquals(1, group.projects.size)
        val groupWithAProject = groupService.addProject(group.getId()!!, project.getId()!!)
        Assertions.assertEquals(2, groupWithAProject.projects.size)
    }

    @Test
    fun `should throw an exception when trying to add a project to a group and the project does not exist`() {
        studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().build())
        try {
            groupService.addProject(group.getId()!!, -1)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no project with that id", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to add a project to a group and the group does not exist`() {
        val project = projectService.save(aProject().build())
        try {
            groupService.addProject(-1, project.getId()!!)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no group with that id", e.message)
        }
    }


    @Test
    fun `should throw an exception when trying to add a project to a group and the project already has an owner`() {
        studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().build())

        studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())
        val group2 = groupService.save(aGroupDTO().withMembers(listOf("prueba@gmail.com")).build())

        val project = projectService.save(aProject().build())
        groupService.addProject(group.getId()!!, project.getId()!!)
        try {
            groupService.addProject(group2.getId()!!, project.getId()!!)
        } catch (e: ProjectAlreadyHasAnOwnerException) {
            Assertions.assertEquals("The project already has an owner", e.message)
        }
    }


    @Test
    fun `should be true to have a member with email when the member was added previously`() {
        val student = studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().build())
        Assertions.assertTrue(groupService.hasAMemberWithEmail(group.getId()!!, student.getEmail()!!))
    }

    @Test
    fun `should be false to have a member with email when it was not added`() {
        studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().build())
        Assertions.assertFalse(groupService.hasAMemberWithEmail(group.getId()!!, "emailFalso"))
    }

    @Test
    fun `should be true to have a group with a student with id and a project with id when both were added previously`() {
        val student = studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().build())
        val project = projectService.save(aProject().build())
        groupService.addProject(group.getId()!!, project.getId()!!)
        Assertions.assertTrue(
            groupService.thereIsAGroupWithThisProjectAndThisMember(
                project.getId()!!,
                student.getId()!!
            )
        )
    }

    @Test
    fun `should be false to have a group with a student with id and a project with id when both were not added`() {
        studentService.save(aStudentDTO().build())
        groupService.save(aGroupDTO().build())
        Assertions.assertFalse(groupService.thereIsAGroupWithThisProjectAndThisMember(-1, -1))
    }

    @Test
    fun `should be false to have a group with a student with id and a project with id when the project was not been added`() {
        val student = studentService.save(aStudentDTO().build())
        groupService.save(aGroupDTO().build())
        Assertions.assertFalse(
            groupService.thereIsAGroupWithThisProjectAndThisMember(
                -1,
                student.getId()!!
            )
        )
    }

    @Test
    fun `should be false to have a group with a student with id and a project with id when the student was not been added`() {
        studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().build())
        val project = projectService.save(aProject().build())
        groupService.addProject(group.getId()!!, project.getId()!!)
        Assertions.assertFalse(groupService.thereIsAGroupWithThisProjectAndThisMember(project.getId()!!, -1))
    }

    @Test
    fun `should be true to have a group with a student with email and a deploy instance with id when both were added previously`() {
        val student = studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().build())
        val project = projectService.save(aProject().build())
        val deployInstance = deployInstanceService.save(aDeployInstance().build())
        groupService.addProject(group.getId()!!, project.getId()!!)
        projectService.addDeployInstance(project.getId()!!, deployInstance.getId()!!)
        Assertions.assertTrue(
            groupService.thereIsAGroupWhereIsStudentAndTheDeployInstanceExists(
                student.getEmail()!!,
                deployInstance.getId()!!
            )
        )
    }

    @Test
    fun `should be false to have a group with a student with email and a deploy instance with id when both were not added`() {
        studentService.save(aStudentDTO().build())
        groupService.save(aGroupDTO().build())
        Assertions.assertFalse(groupService.thereIsAGroupWhereIsStudentAndTheDeployInstanceExists("emailFalso@gmail.com", -1))
    }

    @Test
    fun `should be false to have a group with a student with email and a deploy instance with id when the deploy instance was not been added`() {
        val student = studentService.save(aStudentDTO().build())
        groupService.save(aGroupDTO().build())
        Assertions.assertFalse(
            groupService.thereIsAGroupWhereIsStudentAndTheDeployInstanceExists(
                student.getEmail()!!, -1)
        )
    }

    @Test
    fun `should be false to have a group with a student with email and a deploy instance with id when the student was not been added`() {
        studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().build())
        val project = projectService.save(aProject().build())
        groupService.addProject(group.getId()!!, project.getId()!!)
        val deployInstance = deployInstanceService.save(aDeployInstance().build())
        projectService.addDeployInstance(project.getId()!!, deployInstance.getId()!!)
        Assertions.assertFalse(groupService.thereIsAGroupWhereIsStudentAndTheDeployInstanceExists(
            "emailFalso@gmail.com", deployInstance.getId()!!))
    }

    @Test
    fun `should recover an empty list of groups when recover all and there is no persistence`() {
        Assertions.assertEquals(0, groupService.readAll().size)
    }

    @Test
    fun `should recover a list with two groups when recover all and there are exactly two persisted`() {
        studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())
        groupService.save(aGroupDTO().withName("The programmers").withMembers(listOf("prueba@gmail.com")).build())
        studentService.save(aStudentDTO().build())
        groupService.save(aGroupDTO().withName("The group").build())
        val groups = groupService.readAll()

        Assertions.assertEquals(2, groups.size)
        Assertions.assertTrue(groups.any { it.name == "The programmers" })
        Assertions.assertTrue(groups.any { it.name == "The group" })
    }
}