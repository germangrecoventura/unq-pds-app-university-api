package unq.pds.services

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.Initializer
import unq.pds.api.dtos.GroupDTO
import unq.pds.api.dtos.GroupUpdateDTO
import unq.pds.model.builder.ProjectBuilder.Companion.aProject
import unq.pds.model.exceptions.ProjectAlreadyHasAnOwnerException
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
    lateinit var initializer: Initializer

    @BeforeEach
    fun tearDown() {
        initializer.cleanDataBase()
    }

    @Test
    fun `should be create a group when it has valid credentials`() {
        val student = studentService.save(aStudentDTO().build())
        val groupDTO = GroupDTO()
        groupDTO.name = "Test"
        groupDTO.nameProject = "Test project"
        groupDTO.members = listOf(student.getEmail()!!)
        val group = groupService.save(groupDTO)
        Assertions.assertNotNull(group.getId())
    }

    @Test
    fun `should recover a group when it exists`() {
        val student = studentService.save(aStudentDTO().build())
        val groupDTO = GroupDTO()
        groupDTO.name = "Test"
        groupDTO.nameProject = "Test project"
        groupDTO.members = listOf(student.getEmail()!!)
        val group = groupService.save(groupDTO)
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
        val student = studentService.save(aStudentDTO().build())
        val groupDTOCreate = GroupDTO()
        groupDTOCreate.name = "Test"
        groupDTOCreate.nameProject = "Test project"
        groupDTOCreate.members = listOf(student.getEmail()!!)
        val group = groupService.save(groupDTOCreate)
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
        val student = studentService.save(aStudentDTO().build())
        val groupDTOCreate = GroupDTO()
        groupDTOCreate.name = "Test"
        groupDTOCreate.nameProject = "Test project"
        groupDTOCreate.members = listOf(student.getEmail()!!)
        val group = groupService.save(groupDTOCreate)
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
        val student = studentService.save(aStudentDTO().withEmail("test@gmail.com").build())
        val groupDTOCreate = GroupDTO()
        groupDTOCreate.name = "Test"
        groupDTOCreate.nameProject = "Test project"
        groupDTOCreate.members = listOf(student.getEmail()!!)
        val group = groupService.save(groupDTOCreate)
        val member = studentService.save(aStudentDTO().build())
        Assertions.assertEquals(1, group.members.size)
        val groupWithAMember = groupService.addMember(group.getId()!!, member.getId()!!)
        Assertions.assertEquals(2, groupWithAMember.members.size)
    }

    @Test
    fun `should throw an exception when trying to add the same member to the group twice and both exist`() {
        val student = studentService.save(aStudentDTO().withEmail("test@gmail.com").build())
        val groupDTOCreate = GroupDTO()
        groupDTOCreate.name = "Test"
        groupDTOCreate.nameProject = "Test project"
        groupDTOCreate.members = listOf(student.getEmail()!!)
        val group = groupService.save(groupDTOCreate)
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
        val student = studentService.save(aStudentDTO().build())
        val groupDTOCreate = GroupDTO()
        groupDTOCreate.name = "Test"
        groupDTOCreate.nameProject = "Test project"
        groupDTOCreate.members = listOf(student.getEmail()!!)
        val group = groupService.save(groupDTOCreate)
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
        val student = studentService.save(aStudentDTO().withEmail("test@gmail.com").build())
        val groupDTOCreate = GroupDTO()
        groupDTOCreate.name = "Test"
        groupDTOCreate.nameProject = "Test project"
        groupDTOCreate.members = listOf(student.getEmail()!!)
        val group = groupService.save(groupDTOCreate)
        Assertions.assertEquals(1, group.members.size)
        val groupWithoutMembers = groupService.removeMember(group.getId()!!, student.getId()!!)
        Assertions.assertEquals(0, groupWithoutMembers.members.size)
    }

    @Test
    fun `should throw an exception when trying to remove a member who does not belong to the group and both exist`() {
        val student = studentService.save(aStudentDTO().withEmail("test@gmail.com").build())
        val groupDTOCreate = GroupDTO()
        groupDTOCreate.name = "Test"
        groupDTOCreate.nameProject = "Test project"
        groupDTOCreate.members = listOf(student.getEmail()!!)
        val group = groupService.save(groupDTOCreate)
        val member = studentService.save(aStudentDTO().build())
        try {
            groupService.removeMember(group.getId()!!, member.getId()!!)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("The member is not in the group", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to remove a member of a group and the member does not exist`() {
        val student = studentService.save(aStudentDTO().build())
        val groupDTOCreate = GroupDTO()
        groupDTOCreate.name = "Test"
        groupDTOCreate.nameProject = "Test project"
        groupDTOCreate.members = listOf(student.getEmail()!!)
        val group = groupService.save(groupDTOCreate)
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
        val student = studentService.save(aStudentDTO().build())
        val groupDTOCreate = GroupDTO()
        groupDTOCreate.name = "Test"
        groupDTOCreate.nameProject = "Test project"
        groupDTOCreate.members = listOf(student.getEmail()!!)
        val group = groupService.save(groupDTOCreate)
        val project = projectService.save(aProject().build())
        Assertions.assertEquals(1, group.projects.size)
        val groupWithAProject = groupService.addProject(group.getId()!!, project.getId()!!)
        Assertions.assertEquals(2, groupWithAProject.projects.size)
    }

    @Test
    fun `should throw an exception when trying to add a project to a group and the project does not exist`() {
        val student = studentService.save(aStudentDTO().build())
        val groupDTOCreate = GroupDTO()
        groupDTOCreate.name = "Test"
        groupDTOCreate.nameProject = "Test project"
        groupDTOCreate.members = listOf(student.getEmail()!!)
        val group = groupService.save(groupDTOCreate)
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
        val student = studentService.save(aStudentDTO().build())
        val groupDTOCreate = GroupDTO()
        groupDTOCreate.name = "Test"
        groupDTOCreate.nameProject = "Test project"
        groupDTOCreate.members = listOf(student.getEmail()!!)
        val group = groupService.save(groupDTOCreate)

        val student2 = studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())
        val groupDTOCreate2 = GroupDTO()
        groupDTOCreate2.name = "Prueba"
        groupDTOCreate2.nameProject = "Test New"
        groupDTOCreate2.members = listOf(student2.getEmail()!!)
        val group2 = groupService.save(groupDTOCreate2)

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
        val groupDTOCreate = GroupDTO()
        groupDTOCreate.name = "Test"
        groupDTOCreate.nameProject = "Test project"
        groupDTOCreate.members = listOf(student.getEmail()!!)
        val group = groupService.save(groupDTOCreate)
        Assertions.assertTrue(groupService.hasAMemberWithEmail(group.getId()!!, student.getEmail()!!))
    }

    @Test
    fun `should be false to have a member with email when it was not added`() {
        val student = studentService.save(aStudentDTO().build())
        val groupDTOCreate = GroupDTO()
        groupDTOCreate.name = "Test"
        groupDTOCreate.nameProject = "Test project"
        groupDTOCreate.members = listOf(student.getEmail()!!)
        val group = groupService.save(groupDTOCreate)
        Assertions.assertFalse(groupService.hasAMemberWithEmail(group.getId()!!, "emailFalso"))
    }

    @Test
    fun `should be true to have a group with a student with id and a project with id when both were added previously`() {
        val student = studentService.save(aStudentDTO().build())
        val groupDTOCreate = GroupDTO()
        groupDTOCreate.name = "Test"
        groupDTOCreate.nameProject = "Test project"
        groupDTOCreate.members = listOf(student.getEmail()!!)
        val group = groupService.save(groupDTOCreate)
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
        val student = studentService.save(aStudentDTO().build())
        val groupDTOCreate = GroupDTO()
        groupDTOCreate.name = "Test"
        groupDTOCreate.nameProject = "Test project"
        groupDTOCreate.members = listOf(student.getEmail()!!)
        groupService.save(groupDTOCreate)
        Assertions.assertFalse(groupService.thereIsAGroupWithThisProjectAndThisMember(-1, -1))
    }

    @Test
    fun `should be false to have a group with a student with id and a project with id when the project was not been added`() {
        val student = studentService.save(aStudentDTO().build())
        val groupDTOCreate = GroupDTO()
        groupDTOCreate.name = "Test"
        groupDTOCreate.nameProject = "Test project"
        groupDTOCreate.members = listOf(student.getEmail()!!)
        groupService.save(groupDTOCreate)
        Assertions.assertFalse(
            groupService.thereIsAGroupWithThisProjectAndThisMember(
                -1,
                student.getId()!!
            )
        )
    }

    @Test
    fun `should be false to have a group with a student with id and a project with id when the student was not been added`() {
        val student = studentService.save(aStudentDTO().build())
        val groupDTOCreate = GroupDTO()
        groupDTOCreate.name = "Test"
        groupDTOCreate.nameProject = "Test project"
        groupDTOCreate.members = listOf(student.getEmail()!!)
        val group = groupService.save(groupDTOCreate)
        val project = projectService.save(aProject().build())
        groupService.addProject(group.getId()!!, project.getId()!!)
        Assertions.assertFalse(groupService.thereIsAGroupWithThisProjectAndThisMember(project.getId()!!, -1))
    }

    @Test
    fun `should recover an empty list of groups when recover all and there is no persistence`() {
        Assertions.assertEquals(0, groupService.readAll().size)
    }

    @Test
    fun `should recover a list with two groups when recover all and there are exactly two persisted`() {
        val student1 = studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())
        val groupDTOCreate1 = GroupDTO()
        groupDTOCreate1.name = "The programmers"
        groupDTOCreate1.nameProject = "Test project"
        groupDTOCreate1.members = listOf(student1.getEmail()!!)
        groupService.save(groupDTOCreate1)
        val student2 = studentService.save(aStudentDTO().build())
        val groupDTOCreate2 = GroupDTO()
        groupDTOCreate2.name = "The group"
        groupDTOCreate2.nameProject = "Test project"
        groupDTOCreate2.members = listOf(student2.getEmail()!!)
        groupService.save(groupDTOCreate2)
        val groups = groupService.readAll()

        Assertions.assertEquals(2, groups.size)
        Assertions.assertTrue(groups.any { it.name == "The programmers" })
        Assertions.assertTrue(groups.any { it.name == "The group" })
    }
}