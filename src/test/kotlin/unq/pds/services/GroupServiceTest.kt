package unq.pds.services

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.Initializer
import unq.pds.model.builder.GroupBuilder.Companion.aGroup
import unq.pds.model.builder.ProjectBuilder.Companion.aProject
import unq.pds.model.exceptions.ProjectAlreadyHasAnOwnerException
import unq.pds.services.builder.BuilderStudentDTO.Companion.aStudentDTO

@SpringBootTest
class GroupServiceTest {

    @Autowired lateinit var groupService: GroupService
    @Autowired lateinit var studentService: StudentService
    @Autowired lateinit var projectService: ProjectService
    @Autowired lateinit var initializer: Initializer

    @BeforeEach
    fun tearDown() {
        initializer.cleanDataBase()
    }

    @Test
    fun `should be create a group when it has valid credentials`() {
        val group = groupService.save(aGroup().build())
        Assertions.assertNotNull(group.getId())
    }

    @Test
    fun `should recover a group when it exists`() {
        val group = groupService.save(aGroup().build())
        val recoverGroup = groupService.read(group.getId()!!)
        Assertions.assertEquals(group.getId(), recoverGroup.getId())
        Assertions.assertEquals(group.name, recoverGroup.name)
    }

    @Test
    fun `should throw an exception when trying to recover a group with an invalid id`() {
        try {
            groupService.read(-1)
        } catch (e:NoSuchElementException) {
            Assertions.assertEquals("There is no group with that id", e.message)
        }
    }

    @Test
    fun `should update a group when it exists`() {
        val group = groupService.save(aGroup().build())
        group.name = "Group 3"
        val updatedGroup = groupService.update(group)
        Assertions.assertEquals(group.name, updatedGroup.name)
    }

    @Test
    fun `should throw an exception when trying to update a group without persisting`() {
        try {
            groupService.update(aGroup().build())
        } catch (e:NoSuchElementException) {
            Assertions.assertEquals("Group does not exist", e.message)
        }
    }

    @Test
    fun `should delete a group when it exists`() {
        val group = groupService.save(aGroup().build())
        groupService.delete(group.getId()!!)
        Assertions.assertEquals(0, groupService.count())
    }

    @Test
    fun `should throw an exception when trying to delete a group with an invalid id`() {
        try {
            groupService.delete(-1)
        } catch (e:NoSuchElementException) {
            Assertions.assertEquals("There is no group with that id", e.message)
        }
    }

    @Test
    fun `should add a member to a group when it was not previously added and both exist`() {
        val group = groupService.save(aGroup().build())
        val member = studentService.save(aStudentDTO().build())
        Assertions.assertEquals(0, group.members.size)
        val groupWithAMember = groupService.addMember(group.getId()!!, member.getId()!!)
        Assertions.assertEquals(1, groupWithAMember.members.size)
    }

    @Test
    fun `should throw an exception when trying to add the same member to the group twice and both exist`() {
        val group = groupService.save(aGroup().build())
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
        val group = groupService.save(aGroup().build())
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
        val group = groupService.save(aGroup().build())
        val member = studentService.save(aStudentDTO().build())
        Assertions.assertEquals(0, group.members.size)
        val groupWithAMember = groupService.addMember(group.getId()!!, member.getId()!!)
        Assertions.assertEquals(1, groupWithAMember.members.size)
        val groupWithoutMembers = groupService.removeMember(group.getId()!!, member.getId()!!)
        Assertions.assertEquals(0, groupWithoutMembers.members.size)
    }

    @Test
    fun `should throw an exception when trying to remove a member who does not belong to the group and both exist`() {
        val group = groupService.save(aGroup().build())
        val member = studentService.save(aStudentDTO().build())
        try {
            groupService.removeMember(group.getId()!!, member.getId()!!)
        } catch (e:NoSuchElementException) {
            Assertions.assertEquals("The member is not in the group", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to remove a member of a group and the member does not exist`() {
        val group = groupService.save(aGroup().build())
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
        val group = groupService.save(aGroup().build())
        val project = projectService.save(aProject().build())
        Assertions.assertEquals(0, group.projects.size)
        val groupWithAProject = groupService.addProject(group.getId()!!, project.getId()!!)
        Assertions.assertEquals(1, groupWithAProject.projects.size)
    }

    @Test
    fun `should throw an exception when trying to add a project to a group and the project does not exist`() {
        val group = groupService.save(aGroup().build())
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
        val group = groupService.save(aGroup().build())
        val group2 = groupService.save(aGroup().withName("Group 2").build())
        val project = projectService.save(aProject().build())
        groupService.addProject(group.getId()!!, project.getId()!!)
        try {
            groupService.addProject(group2.getId()!!, project.getId()!!)
        } catch (e: ProjectAlreadyHasAnOwnerException) {
            Assertions.assertEquals("The project already has an owner", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to add a project to a student and the project already has an owner`() {
        val group = groupService.save(aGroup().build())
        val student = studentService.save(aStudentDTO().build())
        val project = projectService.save(aProject().build())
        groupService.addProject(group.getId()!!, project.getId()!!)
        try {
            studentService.addProject(student.getId()!!, project.getId()!!)
        } catch (e: ProjectAlreadyHasAnOwnerException) {
            Assertions.assertEquals("The project already has an owner", e.message)
        }
    }

    @Test
    fun `should be true to have a group with a student with id and a project with id when both were added previously`() {
        val group = groupService.save(aGroup().build())
        val student = studentService.save(aStudentDTO().build())
        val project = projectService.save(aProject().build())
        groupService.addProject(group.getId()!!, project.getId()!!)
        groupService.addMember(group.getId()!!, student.getId()!!)
        Assertions.assertTrue(groupService.thereIsAGroupWithThisProjectAndThisMember(project.getId()!!,
            student.getId()!!))
    }

    @Test
    fun `should be false to have a group with a student with id and a project with id when both were not added`() {
        groupService.save(aGroup().build())
        Assertions.assertFalse(groupService.thereIsAGroupWithThisProjectAndThisMember(-1,-1))
    }

    @Test
    fun `should be false to have a group with a student with id and a project with id when the project was not been added`() {
        val group = groupService.save(aGroup().build())
        val student = studentService.save(aStudentDTO().build())
        groupService.addMember(group.getId()!!, student.getId()!!)
        Assertions.assertFalse(groupService.thereIsAGroupWithThisProjectAndThisMember(-1,
            student.getId()!!))
    }

    @Test
    fun `should be false to have a group with a student with id and a project with id when the student was not been added`() {
        val group = groupService.save(aGroup().build())
        val project = projectService.save(aProject().build())
        groupService.addProject(group.getId()!!, project.getId()!!)
        Assertions.assertFalse(groupService.thereIsAGroupWithThisProjectAndThisMember(project.getId()!!,-1))
    }

    @Test
    fun `should recover an empty list of groups when recover all and there is no persistence`() {
        Assertions.assertEquals(0, groupService.readAll().size)
    }

    @Test
    fun `should recover a list with two groups when recover all and there are exactly two persisted`() {
        groupService.save(aGroup().build())
        groupService.save(aGroup().withName("The group").build())
        val groups = groupService.readAll()

        Assertions.assertEquals(2, groups.size)
        Assertions.assertTrue(groups.any { it.name == "The programmers" })
        Assertions.assertTrue(groups.any { it.name == "The group" })
    }
}