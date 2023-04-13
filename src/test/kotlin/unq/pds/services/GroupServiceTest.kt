package unq.pds.services

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.model.builder.GroupBuilder.Companion.aGroup
import unq.pds.services.builder.BuilderStudentDTO.Companion.aStudentDTO

@SpringBootTest
class GroupServiceTest {

    @Autowired lateinit var groupService: GroupService
    @Autowired lateinit var studentService: StudentService

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
            Assertions.assertEquals("Group does not exists", e.message)
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
    fun `should add a member to a group when it was not previously added and both exists`() {
        val group = groupService.save(aGroup().build())
        val member = studentService.save(aStudentDTO().build())
        Assertions.assertEquals(0, group.members.size)
        val groupWithAMember = groupService.addMember(group.getId()!!, member.getId()!!)
        Assertions.assertEquals(1, groupWithAMember.members.size)
    }

    @Test
    fun `should throw an exception when trying to add the same member to the group twice and both exists`() {
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
    fun `should throw an exception when trying to add a member to a group and the member does not exists`() {
        val group = groupService.save(aGroup().build())
        try {
            groupService.addMember(group.getId()!!, -1)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("Not found the student with id -1", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to add a member to a group and the group does not exists`() {
        val member = studentService.save(aStudentDTO().build())
        try {
            groupService.addMember(-1, member.getId()!!)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no group with that id", e.message)
        }
    }

    @Test
    fun `should remove a member from a group when it was previously added and both exists`() {
        val group = groupService.save(aGroup().build())
        val member = studentService.save(aStudentDTO().build())
        Assertions.assertEquals(0, group.members.size)
        val groupWithAMember = groupService.addMember(group.getId()!!, member.getId()!!)
        Assertions.assertEquals(1, groupWithAMember.members.size)
        val groupWithoutMembers = groupService.removeMember(group.getId()!!, member.getId()!!)
        Assertions.assertEquals(0, groupWithoutMembers.members.size)
    }

    @Test
    fun `should throw an exception when trying to remove a member who does not belong to the group and both exists`() {
        val group = groupService.save(aGroup().build())
        val member = studentService.save(aStudentDTO().build())
        try {
            groupService.removeMember(group.getId()!!, member.getId()!!)
        } catch (e:NoSuchElementException) {
            Assertions.assertEquals("The member is not in the group", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to remove a member of a group and the member does not exists`() {
        val group = groupService.save(aGroup().build())
        try {
            groupService.removeMember(group.getId()!!, -1)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("Not found the student with id -1", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to remove a member of a group and the group does not exists`() {
        val member = studentService.save(aStudentDTO().build())
        try {
            groupService.removeMember(-1, member.getId()!!)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no group with that id", e.message)
        }
    }

    @AfterEach
    fun tearDown() {
        groupService.clearGroups()
        studentService.clearStudents()
    }
}