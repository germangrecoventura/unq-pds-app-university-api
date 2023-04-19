package unq.pds

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import unq.pds.services.*
import unq.pds.services.builder.BuilderStudentDTO

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
}
