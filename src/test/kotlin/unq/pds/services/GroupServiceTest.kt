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
        Assertions.assertNotNull(group.id)
    }

    @Test
    fun `should recover a group when it exists`() {
        val group = groupService.save(aGroup().build())
        val recoverGroup = groupService.recover(group.id!!)
        Assertions.assertEquals(group.id, recoverGroup.id)
        Assertions.assertEquals(group.name, recoverGroup.name)
    }

    @Test
    fun `should throw an exception when trying to recover a group with an invalid id`() {
        try {
            groupService.recover(-1)
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
        groupService.delete(group.id!!)
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
        val groupWithAMember = groupService.addMember(group.id!!, member.getId()!!)
        Assertions.assertEquals(1, groupWithAMember.members.size)
    }

    @Test
    fun `should remove a member from a group when it was previously added and both exist`() {
        val group = groupService.save(aGroup().build())
        val member = studentService.save(aStudentDTO().build())
        Assertions.assertEquals(0, group.members.size)
        val groupWithAMember = groupService.addMember(group.id!!, member.getId()!!)
        Assertions.assertEquals(1, groupWithAMember.members.size)
        val groupWithoutMembers = groupService.removeMember(group.id!!, member.getId()!!)
        Assertions.assertEquals(0, groupWithoutMembers.members.size)
    }

    @AfterEach
    fun tearDown() {
        groupService.clearGroups()
        studentService.clearStudents()
    }
}