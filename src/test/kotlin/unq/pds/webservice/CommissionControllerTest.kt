package unq.pds.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import unq.pds.Initializer
import unq.pds.model.builder.CommissionBuilder.Companion.aCommission
import unq.pds.model.builder.GroupBuilder.Companion.aGroup
import unq.pds.model.builder.MatterBuilder.Companion.aMatter
import unq.pds.services.*
import unq.pds.services.builder.BuilderAdminDTO.Companion.aAdminDTO
import unq.pds.services.builder.BuilderCommissionDTO.Companion.aCommissionDTO
import unq.pds.services.builder.BuilderLoginDTO.Companion.aLoginDTO
import unq.pds.services.builder.BuilderStudentDTO.Companion.aStudentDTO
import unq.pds.services.builder.BuilderTeacherDTO.Companion.aTeacherDTO
import javax.servlet.http.Cookie

@ExtendWith(SpringExtension::class)
@SpringBootTest
class CommissionControllerTest {
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var commissionService: CommissionService

    @Autowired
    lateinit var matterService: MatterService

    @Autowired
    lateinit var studentService: StudentService

    @Autowired
    lateinit var teacherService: TeacherService

    @Autowired
    lateinit var adminService: AdminService

    @Autowired
    lateinit var groupService: GroupService

    @Autowired
    lateinit var initializer: Initializer

    private val mapper = ObjectMapper()

    @BeforeEach
    fun setUp() {
        initializer.cleanDataBase()
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to create commission`() {
        val cookie = cookiesAdmin()
        matterService.save(aMatter().build())
        mockMvc.perform(
            MockMvcRequestBuilders.post("/commissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommissionDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 401 status when trying to create a commission and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/commissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommissionDTO().build()))
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to create commission`() {
        val cookie = cookiesStudent()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/commissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommissionDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to create commission`() {
        val cookie = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/commissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommissionDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 400 status when the commission has a year prior to 2000`() {
        val cookie = cookiesAdmin()
        matterService.save(aMatter().build())
        mockMvc.perform(
            MockMvcRequestBuilders.post("/commissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommissionDTO().withYear(1999).build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when the commission has a matter that does not exist`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/commissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommissionDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 200 status when student looking for a commission if it exists`() {
        val cookie = cookiesStudent()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/commissions").accept(MediaType.APPLICATION_JSON)
                .param("id", commission.getId().toString())
                .cookie(cookie)
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when teacher looking for a commission if it exists`() {
        val cookie = cookiesTeacher()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/commissions").accept(MediaType.APPLICATION_JSON)
                .param("id", commission.getId().toString())
                .cookie(cookie)
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when admin looking for a commission if it exists`() {
        val cookie = cookiesAdmin()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/commissions").accept(MediaType.APPLICATION_JSON)
                .param("id", commission.getId().toString())
                .cookie(cookie)
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 401 status when trying to get a commission and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/commissions").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 400 status when the id is null`() {
        val cookie = cookiesAdmin()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/commissions").accept(MediaType.APPLICATION_JSON)
                .param("id", null)
                .cookie(cookie)
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when you are looking for a commission does not exist`() {
        val cookie = cookiesAdmin()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/commissions").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1")
                .cookie(cookie)
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 401 status when trying to delete a commission and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/commissions").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to delete commissions`() {
        val cookie = cookiesStudent()
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/commissions").accept(MediaType.APPLICATION_JSON)
                .param("id", "1").cookie(cookie)
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to delete commissions`() {
        val cookie = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/commissions").accept(MediaType.APPLICATION_JSON)
                .param("id", "1").cookie(cookie)
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to delete commissions`() {
        val cookie = cookiesAdmin()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/commissions").accept(MediaType.APPLICATION_JSON)
                .param("id", commission.getId().toString()).cookie(cookie)
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when trying to delete a commission and the id is null`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/commissions").accept(MediaType.APPLICATION_JSON)
                .param("id", null)
                .cookie(cookie)
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status you want to delete a commission that does not exist`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/commissions").accept(MediaType.APPLICATION_JSON)
                .param("id", "2")
                .cookie(cookie)
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to get all commissions`() {
        val cookie = cookiesStudent()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/commissions/getAll").accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to get all commissions`() {
        val cookie = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/commissions/getAll").accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to get all commissions`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/commissions/getAll").accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 401 status when trying to get all commissions and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/commissions/getAll").accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when trying to add student to a commission and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addStudent/{commissionId}/{studentId}",
                1, 1
            )
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does have not permissions to add student to a commission`() {
        val cookie = cookiesStudent()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().withEmail("prueba@gmail.com").withOwnerGithub("prueba").build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addStudent/{commissionId}/{studentId}",
                commission.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does have not permissions to add student to a commission except yourself`() {
        val cookie = cookiesTeacher()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addStudent/{commissionId}/{studentId}",
                commission.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to add student to a commission except yourself`() {
        val cookie = cookiesTeacher()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().build())
        val teacher = teacherService.findByEmail("german@gmail.com")
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addStudent/{commissionId}/{studentId}",
                commission.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to add student to a commission`() {
        val cookie = cookiesAdmin()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addStudent/{commissionId}/{studentId}",
                commission.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when add a non-existent commission`() {
        val cookie = cookiesAdmin()
        val student = studentService.save(aStudentDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addStudent/{commissionId}/{studentId}",
                -1,
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when add a non-existent student`() {
        val cookie = cookiesAdmin()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addStudent/{commissionId}/{studentId}",
                commission.getId(),
                -1
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 400 status when add student to a commission and it has already been added`() {
        val cookie = cookiesAdmin()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().build())
        commissionService.addStudent(commission.getId()!!, student.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addStudent/{commissionId}/{studentId}",
                commission.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 401 status when trying to remove student of a commission and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeStudent/{commissionId}/{studentId}",
                1, 1
            )
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does have not permissions to remove student of a commission`() {
        val cookie = cookiesStudent()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().withEmail("prueba@gmail.com").withOwnerGithub("prueba").build())
        commissionService.addStudent(commission.getId()!!, student.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeStudent/{commissionId}/{studentId}",
                commission.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does have not permissions to remove student of a commission except yourself`() {
        val cookie = cookiesTeacher()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().build())
        commissionService.addStudent(commission.getId()!!, student.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeStudent/{commissionId}/{studentId}",
                commission.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to remove student of a commission except yourself`() {
        val cookie = cookiesTeacher()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().build())
        val teacher = teacherService.findByEmail("german@gmail.com")
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        commissionService.addStudent(commission.getId()!!, student.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeStudent/{commissionId}/{studentId}",
                commission.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when admin remove an existing student of a commission`() {
        val cookie = cookiesAdmin()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().build())
        commissionService.addStudent(commission.getId()!!, student.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeStudent/{commissionId}/{studentId}",
                commission.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when remove a non-existent student`() {
        val cookie = cookiesAdmin()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeStudent/{commissionId}/{studentId}",
                commission.getId(),
                -1
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when non-existent commission to remove student`() {
        val cookie = cookiesAdmin()
        val student = studentService.save(aStudentDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeStudent/{commissionId}/{studentId}",
                -1,
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when trying to remove a student who does not belong to a commission`() {
        val cookie = cookiesAdmin()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeStudent/{commissionId}/{studentId}",
                commission.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 401 status when trying to add teacher to a commission and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addTeacher/{commissionId}/{teacherId}",
                1, 1
            )
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does have not permissions to add teacher to a commission`() {
        val cookie = cookiesStudent()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val teacher = teacherService.save(aTeacherDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addTeacher/{commissionId}/{teacherId}",
                commission.getId(),
                teacher.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does have not permissions to add teacher to a commission except yourself`() {
        val cookie = cookiesTeacher()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val teacher = teacherService.findByEmail("german@gmail.com")
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addTeacher/{commissionId}/{teacherId}",
                commission.getId(),
                teacher.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to add teacher to a commission`() {
        val cookie = cookiesAdmin()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val teacher = teacherService.save(aTeacherDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addTeacher/{commissionId}/{teacherId}",
                commission.getId(),
                teacher.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when add teacher to a commission and it has already been added`() {
        val cookie = cookiesAdmin()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val teacher = teacherService.save(aTeacherDTO().build())
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addTeacher/{commissionId}/{teacherId}",
                commission.getId(),
                teacher.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when trying to add a teacher in a non-existent commission`() {
        val cookie = cookiesAdmin()
        val teacher = teacherService.save(aTeacherDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addTeacher/{commissionId}/{teacherId}",
                -1,
                teacher.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when add a non-existent teacher`() {
        val cookie = cookiesAdmin()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addTeacher/{commissionId}/{teacherId}",
                commission.getId(),
                -1
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 401 status when trying to remove teacher to a commission and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeTeacher/{commissionId}/{teacherId}",
                1, 1
            )
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does have not permissions to remove teacher to a commission`() {
        val cookie = cookiesStudent()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val teacher = teacherService.save(aTeacherDTO().build())
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeTeacher/{commissionId}/{teacherId}",
                commission.getId(),
                teacher.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does have not permissions to remove teacher to a commission except yourself`() {
        val cookie = cookiesTeacher()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val teacher = teacherService.findByEmail("german@gmail.com")
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeTeacher/{commissionId}/{teacherId}",
                commission.getId(),
                teacher.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to remove teacher to a commission`() {
        val cookie = cookiesAdmin()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val teacher = teacherService.save(aTeacherDTO().build())
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeTeacher/{commissionId}/{teacherId}",
                commission.getId(),
                teacher.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when trying to remove a teacher who does not belong to a commission`() {
        val cookie = cookiesAdmin()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val teacher = teacherService.save(aTeacherDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeTeacher/{commissionId}/{teacherId}",
                commission.getId(),
                teacher.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when trying to remove a teacher in a non-existent commission`() {
        val cookie = cookiesAdmin()
        val teacher = teacherService.save(aTeacherDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeTeacher/{commissionId}/{teacherId}",
                -1,
                teacher.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when remove a non-existent teacher`() {
        val cookie = cookiesAdmin()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeTeacher/{commissionId}/{teacherId}",
                commission.getId(),
                -1
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 401 status when trying to add group to a commission and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addGroup/{commissionId}/{groupId}",
                1, 1
            )
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does have not permissions to add group to a commission`() {
        val cookie = cookiesStudent()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val group = groupService.save(aGroup().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addGroup/{commissionId}/{groupId}",
                commission.getId(),
                group.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does have not permissions to add group to a commission except yourself`() {
        val cookie = cookiesTeacher()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val group = groupService.save(aGroup().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addGroup/{commissionId}/{groupId}",
                commission.getId(),
                group.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to add group to a commission except yourself`() {
        val cookie = cookiesTeacher()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val group = groupService.save(aGroup().build())
        val teacher = teacherService.findByEmail("german@gmail.com")
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addGroup/{commissionId}/{groupId}",
                commission.getId(),
                group.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to add group to a commission`() {
        val cookie = cookiesAdmin()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val group = groupService.save(aGroup().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addGroup/{commissionId}/{groupId}",
                commission.getId(),
                group.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when trying to remove a group in a non-existent commission`() {
        val cookie = cookiesAdmin()
        val group = groupService.save(aGroup().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addGroup/{commissionId}/{groupId}",
                -1,
                group.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when add a non-existent group`() {
        val cookie = cookiesAdmin()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addGroup/{commissionId}/{groupId}",
                commission.getId(),
                -1
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 400 status when add group to a commission and it has already been added`() {
        val cookie = cookiesAdmin()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val group = groupService.save(aGroup().build())
        commissionService.addGroup(commission.getId()!!, group.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addGroup/{commissionId}/{groupId}",
                commission.getId(),
                group.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 401 status when trying to remove group of a commission and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeGroup/{commissionId}/{groupId}",
                1, 1
            )
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does have not permissions to remove group of a commission`() {
        val cookie = cookiesStudent()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val group = groupService.save(aGroup().build())
        commissionService.addGroup(commission.getId()!!, group.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeGroup/{commissionId}/{groupId}",
                commission.getId(),
                group.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does have not permissions to remove group of a commission except yourself`() {
        val cookie = cookiesTeacher()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val group = groupService.save(aGroup().build())
        commissionService.addGroup(commission.getId()!!, group.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeGroup/{commissionId}/{groupId}",
                commission.getId(),
                group.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to remove group of a commission except yourself`() {
        val cookie = cookiesTeacher()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val group = groupService.save(aGroup().build())
        val teacher = teacherService.findByEmail("german@gmail.com")
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        commissionService.addGroup(commission.getId()!!, group.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeGroup/{commissionId}/{groupId}",
                commission.getId(),
                group.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when admin remove an existing group of a commission`() {
        val cookie = cookiesAdmin()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val group = groupService.save(aGroup().build())
        commissionService.addGroup(commission.getId()!!, group.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeGroup/{commissionId}/{groupId}",
                commission.getId(),
                group.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when remove a non-existent group`() {
        val cookie = cookiesAdmin()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeGroup/{commissionId}/{groupId}",
                commission.getId(),
                -1
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when non-existent commission to remove group`() {
        val cookie = cookiesAdmin()
        val group = groupService.save(aGroup().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeGroup/{commissionId}/{groupId}",
                -1,
                group.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when trying to remove a group who does not belong to a commission`() {
        val cookie = cookiesAdmin()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val group = groupService.save(aGroup().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeGroup/{commissionId}/{groupId}",
                commission.getId(),
                group.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    private fun cookiesStudent(): Cookie? {
        val student = studentService.save(aStudentDTO().build())
        val login = aLoginDTO().withEmail(student.getEmail()).withPassword("funciona").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(status().isOk)

        return response.andReturn().response.cookies[0]
    }

    private fun cookiesTeacher(): Cookie? {
        val teacher = teacherService.save(aTeacherDTO().build())
        val login = aLoginDTO().withEmail(teacher.getEmail()).withPassword("funciona").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(status().isOk)

        return response.andReturn().response.cookies[0]
    }

    private fun cookiesAdmin(): Cookie? {
        val admin = adminService.save(aAdminDTO().build())
        val login = aLoginDTO().withEmail(admin.getEmail()).withPassword("funciona").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(status().isOk)

        return response.andReturn().response.cookies[0]
    }
}