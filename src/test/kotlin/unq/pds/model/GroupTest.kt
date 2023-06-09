package unq.pds.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import unq.pds.model.builder.BuilderStudent.Companion.aStudent
import unq.pds.model.builder.GroupBuilder.Companion.aGroup
import unq.pds.model.builder.ProjectBuilder.Companion.aProject
import unq.pds.model.exceptions.GroupWithEmptyMemberException
import javax.management.InvalidAttributeValueException

class GroupTest {

    @Test
    fun `should throw an exception when group name is empty`() {
        try {
            aGroup().withName("").build()
        } catch (e: InvalidAttributeValueException) {
            Assertions.assertEquals("Name cannot be empty", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to set an empty group name`() {
        val group = aGroup().build()
        try {
            group.name = ""
        } catch (e: InvalidAttributeValueException) {
            Assertions.assertEquals("Name cannot be empty", e.message)
        }
    }

    @Test
    fun `should add a member when it has not been added previously`() {
        val group = aGroup().build()
        Assertions.assertEquals(0, group.members.size)
        group.addMember(aStudent().build())
        Assertions.assertEquals(1, group.members.size)
    }

    @Test
    fun `should throw an exception when trying to add the same member to the group twice`() {
        val group = aGroup().build()
        group.addMember(aStudent().build())
        try {
            group.addMember(aStudent().build())
        } catch (e: CloneNotSupportedException) {
            Assertions.assertEquals("The member is already in the group", e.message)
        }
    }

    @Test
    fun `should remove a member when it has been previously added`() {
        val group = aGroup().build()
        val member = aStudent().build()
        val member2 = aStudent().withEmail("lucas@gmail.com").build()
        Assertions.assertEquals(0, group.members.size)
        group.addMember(member)
        group.addMember(member2)
        Assertions.assertEquals(2, group.members.size)
        group.removeMember(member)
        Assertions.assertEquals(1, group.members.size)
    }

    @Test
    fun `should throw an exception when trying to remove the only member of a group`() {
        val group = aGroup().build()
        val member = aStudent().build()
        group.addMember(member)
        try {
            group.removeMember(member)
        } catch (e: GroupWithEmptyMemberException) {
            Assertions.assertEquals("The group cannot run out of students", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to remove a member who does not belong to the group`() {
        val group = aGroup().build()
        try {
            group.removeMember(aStudent().build())
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("The member is not in the group", e.message)
        }
    }

    @Test
    fun `should add a project when it has not been added previously`() {
        val group = aGroup().build()
        Assertions.assertEquals(0, group.projects.size)
        group.addProject(aProject().build())
        Assertions.assertEquals(1, group.projects.size)
    }

    @Test
    fun `should throw an exception when trying to add the same project to the group twice`() {
        val group = aGroup().build()
        group.addProject(aProject().build())
        try {
            group.addProject(aProject().build())
        } catch (e: CloneNotSupportedException) {
            Assertions.assertEquals("The project has already been added", e.message)
        }
    }

    @Test
    fun `should be true to have a member with email when the member was added previously`() {
        val group = aGroup().build()
        val student = aStudent().build()
        group.addMember(student)
        Assertions.assertTrue(group.hasAMemberWithEmail(student.getEmail()!!))
    }

    @Test
    fun `should be false to have a member with email when it was not added`() {
        val group = aGroup().build()
        val student = aStudent().build()
        group.addMember(student)
        Assertions.assertFalse(group.hasAMemberWithEmail("emailFalso"))
    }
}