package unq.pds.services

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.model.builder.CommissionBuilder.Companion.aCommission
import unq.pds.model.builder.GroupBuilder.Companion.aGroup
import unq.pds.services.builder.BuilderStudentDTO.Companion.aStudentDTO
import unq.pds.services.builder.BuilderTeacherDTO.Companion.aTeacherDTO

@SpringBootTest
class CommissionServiceTest {

    @Autowired lateinit var commissionService: CommissionService
    @Autowired lateinit var matterService: MatterService
    @Autowired lateinit var studentService: StudentService
    @Autowired lateinit var teacherService: TeacherService
    @Autowired lateinit var groupService: GroupService

    @Test
    fun `should be create a commission when it has valid credentials`() {
        val commission = commissionService.save(aCommission().build())
        Assertions.assertNotNull(commission.getId())
    }

    @Test
    fun `should recover a commission when it exists`() {
        val commission = commissionService.save(aCommission().build())
        val recoverCommission = commissionService.read(commission.getId()!!)
        Assertions.assertEquals(commission.getId(), recoverCommission.getId())
        Assertions.assertEquals(commission.getYear(), recoverCommission.getYear())
        Assertions.assertEquals(commission.getFourMonthPeriod(), recoverCommission.getFourMonthPeriod())
        Assertions.assertEquals(commission.getMatter().name, recoverCommission.getMatter().name)
    }

    @Test
    fun `should throw an exception when trying to recover a commission with an invalid id`() {
        try {
            commissionService.read(-1)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no commission with that id", e.message)
        }
    }

    @Test
    fun `should update a commission when it exists`() {
        val commission = commissionService.save(aCommission().build())
        val updatedCommission = commissionService.update(commission)
        Assertions.assertEquals(commission.getYear(), updatedCommission.getYear())
        Assertions.assertEquals(commission.getFourMonthPeriod(), updatedCommission.getFourMonthPeriod())
        Assertions.assertEquals(commission.getMatter().name, updatedCommission.getMatter().name)
    }

    @Test
    fun `should throw an exception when trying to update a commission without persisting`() {
        try {
            commissionService.update(aCommission().build())
        } catch (e:NoSuchElementException) {
            Assertions.assertEquals("Commission does not exist", e.message)
        }
    }

    @Test
    fun `should delete a commission when it exists`() {
        val commission = commissionService.save(aCommission().build())
        commissionService.delete(commission.getId()!!)
        Assertions.assertEquals(0, commissionService.count())
    }

    @Test
    fun `should throw an exception when trying to delete a commission with an invalid id`() {
        try {
            commissionService.delete(-1)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no commission with that id", e.message)
        }
    }

    @Test
    fun `should add a student to a commission when it was not previously added and both exist`() {
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().build())
        Assertions.assertEquals(0, commission.students.size)
        val commissionWithAStudent = commissionService.addStudent(commission.getId()!!, student.getId()!!)
        Assertions.assertEquals(1, commissionWithAStudent.students.size)
    }

    @Test
    fun `should throw an exception when trying to add the same student to a commission twice and both exist`() {
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().build())
        commissionService.addStudent(commission.getId()!!, student.getId()!!)
        try {
            commissionService.addStudent(commission.getId()!!, student.getId()!!)
        } catch (e: CloneNotSupportedException) {
            Assertions.assertEquals("The student is already in the commission", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to add a student to a commission and the student does not exist`() {
        val commission = commissionService.save(aCommission().build())
        try {
            commissionService.addStudent(commission.getId()!!, -1)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("Not found the student with id -1", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to add a student to a commission and the commission does not exist`() {
        val student = studentService.save(aStudentDTO().build())
        try {
            commissionService.addStudent(-1, student.getId()!!)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no commission with that id", e.message)
        }
    }

    @Test
    fun `should remove a student from a commission when it was previously added and both exist`() {
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().build())
        Assertions.assertEquals(0, commission.students.size)
        val commissionWithAStudent = commissionService.addStudent(commission.getId()!!, student.getId()!!)
        Assertions.assertEquals(1, commissionWithAStudent.students.size)
        val commissionWithoutStudents = commissionService.removeStudent(commission.getId()!!, student.getId()!!)
        Assertions.assertEquals(0, commissionWithoutStudents.students.size)
    }

    @Test
    fun `should throw an exception when trying to remove a student who does not belong to a commission and both exist`() {
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().build())
        try {
            commissionService.removeStudent(commission.getId()!!, student.getId()!!)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("The student is not in the commission", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to remove a student of a commission and the student does not exist`() {
        val commission = commissionService.save(aCommission().build())
        try {
            commissionService.removeStudent(commission.getId()!!, -1)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("Not found the student with id -1", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to remove a student of a commission and the commission does not exist`() {
        val student = studentService.save(aStudentDTO().build())
        try {
            commissionService.removeStudent(-1, student.getId()!!)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no commission with that id", e.message)
        }
    }

    @Test
    fun `should add a teacher to a commission when it was not previously added and both exist`() {
        val commission = commissionService.save(aCommission().build())
        val teacher = teacherService.save(aTeacherDTO().build())
        Assertions.assertEquals(0, commission.teachers.size)
        val commissionWithATeacher = commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        Assertions.assertEquals(1, commissionWithATeacher.teachers.size)
    }

    @Test
    fun `should throw an exception when trying to add the same teacher to a commission twice and both exist`() {
        val commission = commissionService.save(aCommission().build())
        val teacher = teacherService.save(aTeacherDTO().build())
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        try {
            commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        } catch (e: CloneNotSupportedException) {
            Assertions.assertEquals("The teacher is already in the commission", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to add a teacher to a commission and the teacher does not exist`() {
        val commission = commissionService.save(aCommission().build())
        try {
            commissionService.addTeacher(commission.getId()!!, -1)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("Not found the teacher with id -1", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to add a teacher to a commission and the commission does not exist`() {
        val teacher = teacherService.save(aTeacherDTO().build())
        try {
            commissionService.addTeacher(-1, teacher.getId()!!)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no commission with that id", e.message)
        }
    }

    @Test
    fun `should remove a teacher from a commission when it was previously added and both exist`() {
        val commission = commissionService.save(aCommission().build())
        val teacher = teacherService.save(aTeacherDTO().build())
        Assertions.assertEquals(0, commission.teachers.size)
        val commissionWithATeacher = commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        Assertions.assertEquals(1, commissionWithATeacher.teachers.size)
        val commissionWithoutTeachers = commissionService.removeTeacher(commission.getId()!!, teacher.getId()!!)
        Assertions.assertEquals(0, commissionWithoutTeachers.teachers.size)
    }

    @Test
    fun `should throw an exception when trying to remove a teacher who does not belong to a commission and both exist`() {
        val commission = commissionService.save(aCommission().build())
        val teacher = teacherService.save(aTeacherDTO().build())
        try {
            commissionService.removeTeacher(commission.getId()!!, teacher.getId()!!)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("The teacher is not in the commission", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to remove a teacher of a commission and the teacher does not exist`() {
        val commission = commissionService.save(aCommission().build())
        try {
            commissionService.removeTeacher(commission.getId()!!, -1)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("Not found the teacher with id -1", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to remove a teacher of a commission and the commission does not exist`() {
        val teacher = teacherService.save(aTeacherDTO().build())
        try {
            commissionService.removeTeacher(-1, teacher.getId()!!)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no commission with that id", e.message)
        }
    }

    @Test
    fun `should add a group to a commission when it was not previously added and both exist`() {
        val commission = commissionService.save(aCommission().build())
        val group = groupService.save(aGroup().build())
        Assertions.assertEquals(0, commission.groupsStudents.size)
        val commissionWithAGroup = commissionService.addGroup(commission.getId()!!, group.getId()!!)
        Assertions.assertEquals(1, commissionWithAGroup.groupsStudents.size)
    }

    @Test
    fun `should throw an exception when trying to add the same group to a commission twice and both exist`() {
        val commission = commissionService.save(aCommission().build())
        val group = groupService.save(aGroup().build())
        commissionService.addGroup(commission.getId()!!, group.getId()!!)
        try {
            commissionService.addGroup(commission.getId()!!, group.getId()!!)
        } catch (e: CloneNotSupportedException) {
            Assertions.assertEquals("The group is already in the commission", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to add a group to a commission and the group does not exist`() {
        val commission = commissionService.save(aCommission().build())
        try {
            commissionService.addGroup(commission.getId()!!, -1)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no group with that id", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to add a group to a commission and the commission does not exist`() {
        val group = groupService.save(aGroup().build())
        try {
            commissionService.addGroup(-1, group.getId()!!)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no commission with that id", e.message)
        }
    }

    @Test
    fun `should remove a group from a commission when it was previously added and both exist`() {
        val commission = commissionService.save(aCommission().build())
        val group = groupService.save(aGroup().build())
        Assertions.assertEquals(0, commission.groupsStudents.size)
        val commissionWithAGroup = commissionService.addGroup(commission.getId()!!, group.getId()!!)
        Assertions.assertEquals(1, commissionWithAGroup.groupsStudents.size)
        val commissionWithoutGroups = commissionService.removeGroup(commission.getId()!!, group.getId()!!)
        Assertions.assertEquals(0, commissionWithoutGroups.groupsStudents.size)
    }

    @Test
    fun `should throw an exception when trying to remove a group who does not belong to a commission and both exist`() {
        val commission = commissionService.save(aCommission().build())
        val group = groupService.save(aGroup().build())
        try {
            commissionService.removeGroup(commission.getId()!!, group.getId()!!)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("The group is not in the commission", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to remove a group of a commission and the group does not exist`() {
        val commission = commissionService.save(aCommission().build())
        try {
            commissionService.removeGroup(commission.getId()!!, -1)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no group with that id", e.message)
        }
    }

    @Test
    fun `should throw an exception when trying to remove a group of a commission and the commission does not exist`() {
        val group = groupService.save(aGroup().build())
        try {
            commissionService.removeGroup(-1, group.getId()!!)
        } catch (e: NoSuchElementException) {
            Assertions.assertEquals("There is no commission with that id", e.message)
        }
    }

    @AfterEach
    fun tearDown() {
        commissionService.clearCommissions()
        groupService.clearGroups()
        matterService.clearMatters()
        studentService.clearStudents()
        teacherService.clearTeachers()
    }
}