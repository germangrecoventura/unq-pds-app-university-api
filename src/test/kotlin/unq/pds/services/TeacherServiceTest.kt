package unq.pds.services

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.Initializer
import unq.pds.model.builder.GroupBuilder.Companion.aGroup
import unq.pds.model.builder.ProjectBuilder.Companion.aProject
import unq.pds.services.builder.BuilderCommentCreateDTO.Companion.aCommentDTO
import unq.pds.services.builder.BuilderRepositoryDTO.Companion.aRepositoryDTO
import unq.pds.services.builder.BuilderStudentDTO.Companion.aStudentDTO
import unq.pds.services.builder.BuilderTeacherDTO.Companion.aTeacherDTO
import unq.pds.services.impl.TeacherServiceImpl
import javax.management.InvalidAttributeValueException

@SpringBootTest
class TeacherServiceTest {

    @Autowired
    lateinit var teacherService: TeacherServiceImpl

    @Autowired
    lateinit var studentService: StudentService

    @Autowired
    lateinit var groupService: GroupService

    @Autowired
    lateinit var initializer: Initializer

    private var token: String = System.getenv("GITHUB_TOKEN")

    @Autowired
    lateinit var repositoryService: RepositoryService

    @Autowired
    lateinit var projectService: ProjectService

    @BeforeEach
    fun tearDown() {
        initializer.cleanDataBase()
    }

    @Test
    fun `should be create a teacher when when it has valid credentials`() {
        var student = teacherService.save(aTeacherDTO().build())
        Assertions.assertTrue(student.getId() != null)
    }

    @Test
    fun `should throw an exception if firstname is null`() {
        var request = aTeacherDTO().withFirstName(null).build()

        Assertions.assertThrows(RuntimeException::class.java) { teacherService.save(request) }
    }

    @Test
    fun `should throw an exception if firstname is empty`() {
        var request = aTeacherDTO().withFirstName("").build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { teacherService.save(request) }

        Assertions.assertEquals(
            "The firstname cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if the firstname has any special characters`() {
        var request = aTeacherDTO().withFirstName("J@").build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { teacherService.save(request) }

        Assertions.assertEquals(
            "The firstname can not contain special characters",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if the firstname has any number`() {
        var request = aTeacherDTO().withFirstName("Jav1er").build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { teacherService.save(request) }

        Assertions.assertEquals(
            "The firstname can not contain numbers",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if lastname is null`() {
        var request = aTeacherDTO().withLastName(null).build()

        Assertions.assertThrows(RuntimeException::class.java) { teacherService.save(request) }
    }

    @Test
    fun `should throw an exception if lastname is empty`() {
        var request = aTeacherDTO().withLastName("").build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { teacherService.save(request) }

        Assertions.assertEquals(
            "The lastname cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if the lastname has any special characters`() {
        var request = aTeacherDTO().withLastName("Gr#co").build()

        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { teacherService.save(request) }

        Assertions.assertEquals(
            "The lastname can not contain special characters",
            thrown!!.message
        )

    }

    @Test
    fun `should throw an exception if the lastname has any number`() {
        var request = aTeacherDTO().withLastName("Gr3c0").build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { teacherService.save(request) }

        Assertions.assertEquals(
            "The lastname can not contain numbers",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception if email is null`() {
        var request = aTeacherDTO().withEmail(null).build()

        Assertions.assertThrows(RuntimeException::class.java) { teacherService.save(request) }
    }

    @Test
    fun `should throw an exception if email is empty`() {
        var request = aTeacherDTO().withEmail("").build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { teacherService.save(request) }

        Assertions.assertEquals(
            "The email cannot be empty",
            thrown!!.message
        )
    }

    @Test
    fun `should throw an exception when the email is already registered`() {
        var request1 = aTeacherDTO().withEmail("repetido@gmail.com").build()
        var request2 = aTeacherDTO().withEmail("repetido@gmail.com").build()

        teacherService.save(request1)
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) { teacherService.save(request2) }

        Assertions.assertEquals(
            "The email is already registered",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when the email is not valid`() {
        var request = aTeacherDTO().withEmail("juanPerezgmail.com").build()
        val thrown: InvalidAttributeValueException? =
            Assertions.assertThrows(InvalidAttributeValueException::class.java) { teacherService.save(request) }

        Assertions.assertEquals(
            "The email is not valid",
            thrown!!.message
        )
    }

    @Test
    fun `should update teacher name when firstname is valid`() {
        var request = aTeacherDTO().withEmail("prueba@gmail.com").build()
        var teacher = teacherService.save(request)
        teacher.setFirstName("Juan")
        var studentUpdated = teacherService.update(teacher)
        Assertions.assertTrue(studentUpdated.getFirstName() == teacher.getFirstName())
    }


    @Test
    fun `should update teacher lastname when lastname is valid`() {
        var request = aTeacherDTO().withEmail("prueba@gmail.com").build()
        var teacher = teacherService.save(request)
        teacher.setLastName("Perez")

        var studentUpdated = teacherService.update(teacher)

        Assertions.assertTrue(studentUpdated.getLastName() == teacher.getLastName())
    }

    @Test
    fun `should update teacher email when email is valid`() {
        var request = aTeacherDTO().withEmail("prueba@gmail.com").build()
        var teacher = teacherService.save(request)
        teacher.setEmail("juanPerez@gmail.com")
        var teacherUpdated = teacherService.update(teacher)

        Assertions.assertTrue(teacherUpdated.getEmail() == teacher.getEmail())
    }

    @Test
    fun `should not update the teacher if the email already exists`() {
        var request = aTeacherDTO().withEmail("prueba@gmail.com").build()
        teacherService.save(request)
        var request2 = aTeacherDTO().withEmail("jose@gmail.com").build()
        var teacher2 = teacherService.save(request2)
        teacher2.setEmail("prueba@gmail.com")
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) { teacherService.update(teacher2) }


        Assertions.assertEquals(
            "The email is already registered",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when update a non-existent teacher`() {
        var teacher = teacherService.save(aTeacherDTO().build())
        teacher.setId(-5)

        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) { teacherService.update(teacher) }


        Assertions.assertEquals(
            "Not found the teacher with id -5",
            thrown.message
        )
    }


    @Test
    fun `should delete a teacher if it exists`() {
        var teacher = teacherService.save(aTeacherDTO().build())
        teacher.getId()?.let { teacherService.deleteById(it) }
        Assertions.assertTrue(teacherService.count() == 0)
    }


    @Test
    fun `should throw an exception when deleting a non-existent teacher`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) { teacherService.deleteById(-1) }

        Assertions.assertEquals(
            "The teacher with id -1 is not registered",
            thrown.message
        )
    }

    @Test
    fun `should return a teacher when searched for by id`() {
        var teacher = teacherService.save(aTeacherDTO().build())
        var teacherRecovery = teacherService.findById(teacher.getId()!!)

        Assertions.assertTrue(teacherRecovery.getId() == teacher.getId())
    }

    @Test
    fun `should throw an exception if the teacher does not exist`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) { teacherService.findById(-1) }

        Assertions.assertEquals(
            "Not found the teacher with id -1",
            thrown.message
        )
    }

    @Test
    fun `should return a teacher when searched for by email`() {
        var teacher = teacherService.save(aTeacherDTO().build())
        var teacherRecovery = teacherService.findByEmail(teacher.getEmail())

        Assertions.assertTrue(teacherRecovery.getId() == teacher.getId())
    }

    @Test
    fun `should throw an exception if the email does not exist`() {
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) { teacherService.findByEmail("german@gmial.com") }

        Assertions.assertEquals(
            "Not found the teacher with email german@gmial.com",
            thrown.message
        )
    }

    @Test
    fun `should recover an empty list of teachers when recover all and there is no persistence`() {
        Assertions.assertEquals(0, teacherService.readAll().size)
    }

    @Test
    fun `should recover a list with two teachers when recover all and there are exactly two persisted`() {
        teacherService.save(aTeacherDTO().build())
        teacherService.save(aTeacherDTO().withEmail("germanF@gmail.com").build())
        val teachers = teacherService.readAll()

        Assertions.assertEquals(2, teachers.size)
        Assertions.assertTrue(teachers.any { it.getEmail() == "german@gmail.com" })
        Assertions.assertTrue(teachers.any { it.getEmail() == "germanF@gmail.com" })
    }

    @Test
    fun `should throw an exception when the student to add the comment does not exist`() {

        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) {
                teacherService.addCommentToStudent(
                    aCommentDTO().withId(
                        -1
                    ).build()
                )
            }

        Assertions.assertEquals(
            "Not found the student with id -1",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when the repository to add the comment does not exist in student`() {
        val student = studentService.save(aStudentDTO().build())
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) {
                teacherService.addCommentToStudent(
                    aCommentDTO().withId(
                        student.getId()
                    ).withNameRepository("prueba").build()
                )
            }

        Assertions.assertEquals(
            "Not found the repository with name prueba",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when the student doesn't have any project with the repository`() {
        val student = studentService.save(aStudentDTO().withTokenGithub(token).build())
        val repository = repositoryService.save(aRepositoryDTO().build())


        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) {
                teacherService.addCommentToStudent(
                    aCommentDTO().withId(
                        student.getId()
                    ).withNameRepository(repository.name).build()
                )
            }

        Assertions.assertEquals(
            "Not found the repository with student",
            thrown.message
        )
    }

    @Test
    fun `should add a comment to a project with an existing user repository`() {
        val student = studentService.save(aStudentDTO().withTokenGithub(token).build())
        val repository = repositoryService.save(aRepositoryDTO().build())
        var project = projectService.save(aProject().build())
        project = projectService.addRepository(project.getId()!!, repository.id)
        studentService.addProject(student.getId()!!, project.getId()!!)
        teacherService.addCommentToStudent(
            aCommentDTO().withId(
                student.getId()
            ).withNameRepository(repository.name).build()
        )
        val repositoryFind = repositoryService.findById(repository.id)
        Assertions.assertTrue(repositoryFind.commentsTeacher.size == 1)
    }

    @Test
    fun `should throw an exception when the group to add the comment does not exist`() {

        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) {
                teacherService.addCommentToGroup(
                    aCommentDTO().withId(
                        -1
                    ).build()
                )
            }

        Assertions.assertEquals(
            "Not found the group with id -1",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when the repository to add the comment does not exist in group`() {
        val group = groupService.save(aGroup().build())
        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) {
                teacherService.addCommentToGroup(
                    aCommentDTO().withId(
                        group.getId()
                    ).withNameRepository("prueba").build()
                )
            }

        Assertions.assertEquals(
            "Not found the repository with name prueba",
            thrown.message
        )
    }

    @Test
    fun `should throw an exception when the group doesn't have any project with the repository`() {
        val group = groupService.save(aGroup().build())
        studentService.save(aStudentDTO().withTokenGithub(token).build())
        val repository = repositoryService.save(aRepositoryDTO().build())


        val thrown: RuntimeException =
            Assertions.assertThrows(RuntimeException::class.java) {
                teacherService.addCommentToGroup(
                    aCommentDTO().withId(
                        group.getId()
                    ).withNameRepository(repository.name).build()
                )
            }

        Assertions.assertEquals(
            "Not found the repository with group",
            thrown.message
        )
    }

    @Test
    fun `should add a comment to a project with an existing group repository`() {
        val group = groupService.save(aGroup().build())
        studentService.save(aStudentDTO().withTokenGithub(token).build())
        val repository = repositoryService.save(aRepositoryDTO().build())
        var project = projectService.save(aProject().build())
        project = projectService.addRepository(project.getId()!!, repository.id)
        groupService.addProject(group.getId()!!, project.getId()!!)
        teacherService.addCommentToGroup(
            aCommentDTO().withId(
                group.getId()
            ).withNameRepository(repository.name).build()
        )
        val repositoryFind = repositoryService.findById(repository.id)
        Assertions.assertTrue(repositoryFind.commentsTeacher.size == 1)
    }

}