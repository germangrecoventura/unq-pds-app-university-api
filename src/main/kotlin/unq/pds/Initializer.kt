package unq.pds

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import unq.pds.model.builder.MatterBuilder.Companion.aMatter
import unq.pds.services.*
import unq.pds.services.builder.BuilderStudentDTO
import unq.pds.services.builder.BuilderTeacherDTO.Companion.aTeacherDTO

@Component
class Initializer {
    @Autowired
    lateinit var studentService: StudentService

    @Autowired
    lateinit var teacherService: TeacherService

    @Autowired
    lateinit var matterService: MatterService

    @Autowired
    lateinit var groupsService: GroupService

    @Autowired
    lateinit var commissionService: CommissionService

    fun cleanDataBase() {
        studentService.clearStudents()
        teacherService.clearTeachers()
        matterService.clearMatters()
        groupsService.clearGroups()
        commissionService.clearCommissions()
    }

    fun loadData() {
        loadStudents()
        loadTeachers()
        loadMatters()
    }


    private fun loadStudents() {
        val students = mutableListOf(
            BuilderStudentDTO.aStudentDTO().build(),
            BuilderStudentDTO.aStudentDTO().withFirstName("Lucas").withLastName("Ziegemann")
                .withEmail("lucas@gmail.com").build()
        )
        val mutListIterator = students.listIterator()
        while (mutListIterator.hasNext()) {
            studentService.save(mutListIterator.next())
        }
    }

    private fun loadTeachers() {
        val teachers = mutableListOf(
            aTeacherDTO().withFirstName("Maximiliano").withLastName("Rugna")
                .withEmail("maxi@gmail.com").build(),
            aTeacherDTO().withFirstName("Gustavo").withLastName("Pilla")
                .withEmail("gustavo@gmail.com").build(),
        )
        val mutListIterator = teachers.listIterator()
        while (mutListIterator.hasNext()) {
            teacherService.save(mutListIterator.next())
        }
    }

    private fun loadMatters() {
        val matters = mutableListOf(
            aMatter().withName("Math").build(),
            aMatter().withName("Software development practice").build(),
            aMatter().withName("Applications development").build()
        )
        val mutListIterator = matters.listIterator()
        while (mutListIterator.hasNext()) {
            matterService.save(mutListIterator.next())
        }
    }
}
