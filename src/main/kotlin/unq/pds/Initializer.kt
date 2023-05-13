package unq.pds

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import unq.pds.model.Matter
import unq.pds.model.builder.CommissionBuilder.Companion.aCommission
import unq.pds.model.builder.GroupBuilder.Companion.aGroup
import unq.pds.model.builder.MatterBuilder.Companion.aMatter
import unq.pds.services.*
import unq.pds.services.builder.BuilderAdminDTO.Companion.aAdminDTO
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
    lateinit var groupService: GroupService

    @Autowired
    lateinit var commissionService: CommissionService

    @Autowired
    lateinit var repositoryService: RepositoryService

    @Autowired
    lateinit var projectService: ProjectService

    @Autowired
    lateinit var adminService: AdminService

    fun cleanDataBase() {
        commissionService.clearCommissions()
        groupService.clearGroups()
        studentService.clearStudents()
        teacherService.clearTeachers()
        matterService.clearMatters()
        projectService.clearProjects()
        repositoryService.clearRepositories()
        adminService.clearAdmins()
    }

    fun loadData() {
        loadAdmins()
        loadStudents()
        loadTeachers()
        loadMatters()
        loadGroups()
        loadCommissions()
    }

    private fun loadAdmins() {
        adminService.save(aAdminDTO().build())

    }


    private fun loadStudents() {
        val students = mutableListOf(
            BuilderStudentDTO.aStudentDTO().withTokenGithub("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").build(),            BuilderStudentDTO.aStudentDTO().withFirstName("Lucas").withLastName("Ziegemann")
                .withEmail("lucas@gmail.com").withOwnerGithub("prueba").build()
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

    private fun loadGroups() {
        val groups = mutableListOf(
            aGroup().build(),
            aGroup().withName("The developers").build()
        )
        val mutListIterator = groups.listIterator()
        while (mutListIterator.hasNext()) {
            groupService.save(mutListIterator.next())
        }
    }

    private fun loadCommissions() {
        val commissions = mutableListOf(
            aCommission().withMatter(Matter("Math")).build(),
            aCommission().withMatter(Matter("Software development practice")).build()
        )
        val mutListIterator = commissions.listIterator()
        while (mutListIterator.hasNext()) {
            commissionService.save(mutListIterator.next())
        }
    }
}
