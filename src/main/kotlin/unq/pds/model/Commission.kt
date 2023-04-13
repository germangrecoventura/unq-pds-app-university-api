package unq.pds.model

import javax.management.InvalidAttributeValueException
import javax.persistence.*

@Entity
@Table(name = "commission")
class Commission(
    @Column(nullable = false) private var year: Int,
    @Column(nullable = false) private var fourMonthPeriod: FourMonthPeriod,
    @ManyToOne private var matter: Matter
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    @ManyToMany(fetch = FetchType.EAGER)
    var students: MutableSet<Student> = mutableSetOf()

    @ManyToMany(fetch = FetchType.EAGER)
    var teachers: MutableSet<Teacher> = mutableSetOf()

    @ManyToMany(fetch = FetchType.EAGER)
    var groupsStudents: MutableSet<Group> = mutableSetOf()

    fun addStudent(student: Student) {
        if (isMyStudent(student)) throw CloneNotSupportedException("The student is already in the commission")
        students.add(student)
    }

    fun removeStudent(student: Student) {
        if (!isMyStudent(student)) throw NoSuchElementException("The student is not in the commission")
        students.remove(student)
    }

    fun addTeacher(teacher: Teacher) {
        if (isMyTeacher(teacher)) throw CloneNotSupportedException("The teacher is already in the commission")
        teachers.add(teacher)
    }

    fun removeTeacher(teacher: Teacher) {
        if (!isMyTeacher(teacher)) throw NoSuchElementException("The teacher is not in the commission")
        teachers.remove(teacher)
    }

    fun addGroup(group: Group) {
        if (isMyGroup(group)) throw CloneNotSupportedException("The group is already in the commission")
        groupsStudents.add(group)
    }

    fun removeGroup(group: Group) {
        if (!isMyGroup(group)) throw NoSuchElementException("The group is not in the commission")
        groupsStudents.remove(group)
    }

    fun getYear() = year

    fun getFourMonthPeriod() = fourMonthPeriod

    fun getMatter() = matter

    init { this.validateCreation() }

    private fun validateCreation() {
        validateYear()
    }

    private fun validateYear() {
        if (year < 2000) throw InvalidAttributeValueException("Year should be greater than or equal to 2000")
    }

    private fun isMyStudent(student: Student): Boolean {
        return students.any { it.getEmail() == student.getEmail() }
    }

    private fun isMyTeacher(teacher: Teacher): Boolean {
        return teachers.any { it.getEmail() == teacher.getEmail() }
    }

    private fun isMyGroup(group: Group): Boolean {
        return groupsStudents.any { it.name == group.name &&
                                    it.members.size == group.members.size
                                    it.members.all { member -> isMemberOfTheGroup(member, group) } }
    }

    private fun isMemberOfTheGroup(member: Student, group: Group): Boolean {
        return group.members.any { it.getEmail() == member.getEmail() }
    }
}