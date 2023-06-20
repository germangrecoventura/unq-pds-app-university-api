package unq.pds.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
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
import unq.pds.model.builder.CommissionBuilder.Companion.aCommission
import unq.pds.model.builder.MatterBuilder.Companion.aMatter
import unq.pds.services.*
import unq.pds.services.builder.BuilderAdminDTO.Companion.aAdminDTO
import unq.pds.services.builder.BuilderCommissionDTO.Companion.aCommissionDTO
import unq.pds.services.builder.BuilderGroupDTO.Companion.aGroupDTO
import unq.pds.services.builder.BuilderLoginDTO.Companion.aLoginDTO
import unq.pds.services.builder.BuilderStudentDTO.Companion.aStudentDTO
import unq.pds.services.builder.BuilderTeacherDTO.Companion.aTeacherDTO

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

    private val mapper = ObjectMapper()

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }


    @Test
    fun `should throw a 200 status when a admin does have permissions to create commission`() {
        matterService.save(aMatter().build())
        mockMvc.perform(
            MockMvcRequestBuilders.post("/commissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommissionDTO().build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 401 status when trying to create a commission and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/commissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommissionDTO().build()))
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to create commission`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/commissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommissionDTO().build()))
                .header("Authorization", headerStudent())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to create commission`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/commissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommissionDTO().build()))
                .header("Authorization", headerTeacher())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 400 status when the commission has a year prior to 2000`() {
        matterService.save(aMatter().build())
        mockMvc.perform(
            MockMvcRequestBuilders.post("/commissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommissionDTO().withYear(1999).build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when the commission has a matter that does not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/commissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommissionDTO().build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 200 status when student looking for a commission if it exists`() {
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/commissions").accept(MediaType.APPLICATION_JSON)
                .param("id", commission.getId().toString())
                .header("Authorization", headerStudent())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when teacher looking for a commission if it exists`() {
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/commissions").accept(MediaType.APPLICATION_JSON)
                .param("id", commission.getId().toString())
                .header("Authorization", headerTeacher())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when admin looking for a commission if it exists`() {
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/commissions").accept(MediaType.APPLICATION_JSON)
                .param("id", commission.getId().toString())
                .header("Authorization", headerAdmin())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 401 status when trying to get a commission and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/commissions").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
                .header("Authorization", "")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 400 status when the id is null`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/commissions").accept(MediaType.APPLICATION_JSON)
                .param("id", null)
                .header("Authorization", headerAdmin())
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when you are looking for a commission does not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/commissions").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1")
                .header("Authorization", headerAdmin())
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 401 status when trying to delete a commission and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/commissions").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
                .header("Authorization", "")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to delete commissions`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/commissions").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
                .header("Authorization", headerStudent())
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to delete commissions`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/commissions").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
                .header("Authorization", headerTeacher())
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to delete commissions`() {
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/commissions").accept(MediaType.APPLICATION_JSON)
                .param("id", commission.getId().toString())
                .header("Authorization", headerAdmin())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when trying to delete a commission and the id is null`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/commissions").accept(MediaType.APPLICATION_JSON)
                .param("id", null)
                .header("Authorization", headerAdmin())
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status you want to delete a commission that does not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/commissions").accept(MediaType.APPLICATION_JSON)
                .param("id", "2")
                .header("Authorization", headerAdmin())
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to get all commissions`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/commissions/getAll").accept(MediaType.APPLICATION_JSON)
                .header("Authorization", headerStudent())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to get all commissions`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/commissions/getAll").accept(MediaType.APPLICATION_JSON)
                .header("Authorization", headerTeacher())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to get all commissions`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/commissions/getAll").accept(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 401 status when trying to get all commissions and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/commissions/getAll").accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "")
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
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does have not permissions to add student to a commission`() {
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addStudent/{commissionId}/{studentId}",
                commission.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerStudent())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does have not permissions to add student to a commission except yourself`() {
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
                .header("Authorization", headerTeacher())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to add student to a commission except yourself`() {
        val header = headerTeacher()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().build())
        val teacher = teacherService.findByEmail("docente@gmail.com")
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addStudent/{commissionId}/{studentId}",
                commission.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to add student to a commission`() {
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
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when add a non-existent commission`() {
        val student = studentService.save(aStudentDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addStudent/{commissionId}/{studentId}",
                -1,
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when add a non-existent student`() {
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addStudent/{commissionId}/{studentId}",
                commission.getId(),
                -1
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 400 status when add student to a commission and it has already been added`() {
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
                .header("Authorization", headerAdmin())
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
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does have not permissions to remove student of a commission`() {
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())
        commissionService.addStudent(commission.getId()!!, student.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeStudent/{commissionId}/{studentId}",
                commission.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerStudent())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does have not permissions to remove student of a commission except yourself`() {
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
                .header("Authorization", headerTeacher())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to remove student of a commission except yourself`() {
        val header = headerTeacher()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().build())
        val teacher = teacherService.findByEmail("docente@gmail.com")
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        commissionService.addStudent(commission.getId()!!, student.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeStudent/{commissionId}/{studentId}",
                commission.getId(),
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when admin remove an existing student of a commission`() {
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
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when remove a non-existent student`() {
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeStudent/{commissionId}/{studentId}",
                commission.getId(),
                -1
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when non-existent commission to remove student`() {
        val student = studentService.save(aStudentDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeStudent/{commissionId}/{studentId}",
                -1,
                student.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when trying to remove a student who does not belong to a commission`() {
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
                .header("Authorization", headerAdmin())
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
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does have not permissions to add teacher to a commission`() {
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val teacher = teacherService.save(aTeacherDTO().withEmail("priea@gmail.com").build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addTeacher/{commissionId}/{teacherId}",
                commission.getId(),
                teacher.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerStudent())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does have not permissions to add teacher to a commission except yourself`() {
        val header = headerTeacher()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val teacher = teacherService.findByEmail("docente@gmail.com")
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addTeacher/{commissionId}/{teacherId}",
                commission.getId(),
                teacher.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to add teacher to a commission`() {
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
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when add teacher to a commission and it has already been added`() {
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
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when trying to add a teacher in a non-existent commission`() {
        val teacher = teacherService.save(aTeacherDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addTeacher/{commissionId}/{teacherId}",
                -1,
                teacher.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when add a non-existent teacher`() {
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addTeacher/{commissionId}/{teacherId}",
                commission.getId(),
                -1
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
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
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does have not permissions to remove teacher to a commission`() {
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val teacher = teacherService.save(aTeacherDTO().withEmail("pruebaaa@gmail.com").build())
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeTeacher/{commissionId}/{teacherId}",
                commission.getId(),
                teacher.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerStudent())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does have not permissions to remove teacher to a commission except yourself`() {
        val header = headerTeacher()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val teacher = teacherService.findByEmail("docente@gmail.com")
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeTeacher/{commissionId}/{teacherId}",
                commission.getId(),
                teacher.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to remove teacher to a commission`() {
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
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when trying to remove a teacher who does not belong to a commission`() {
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
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when trying to remove a teacher in a non-existent commission`() {
        val teacher = teacherService.save(aTeacherDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeTeacher/{commissionId}/{teacherId}",
                -1,
                teacher.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when remove a non-existent teacher`() {
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeTeacher/{commissionId}/{teacherId}",
                commission.getId(),
                -1
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
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
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does have not permissions to add group to a commission`() {
        val header = headerStudent()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val group = groupService.save(aGroupDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addGroup/{commissionId}/{groupId}",
                commission.getId(),
                group.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does have not permissions to add group to a commission except yourself`() {
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addGroup/{commissionId}/{groupId}",
                commission.getId(),
                group.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerTeacher())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to add group to a commission except yourself`() {
        val header = headerTeacher()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().withMembers(listOf(student.getEmail()!!)).build())
        commissionService.addStudent(commission.getId()!!, student.getId()!!)
        val teacher = teacherService.findByEmail("docente@gmail.com")
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addGroup/{commissionId}/{groupId}",
                commission.getId(),
                group.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to add group to a commission`() {
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().withMembers(listOf(student.getEmail()!!)).build())
        commissionService.addStudent(commission.getId()!!, student.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addGroup/{commissionId}/{groupId}",
                commission.getId(),
                group.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when trying to add a group to a commission and the members of the group are not students of the commission`() {
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().withMembers(listOf(student.getEmail()!!)).build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addGroup/{commissionId}/{groupId}",
                commission.getId(),
                group.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when trying to remove a group in a non-existent commission`() {
        studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addGroup/{commissionId}/{groupId}",
                -1,
                group.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when add a non-existent group`() {
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addGroup/{commissionId}/{groupId}",
                commission.getId(),
                -1
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 400 status when add group to a commission and it has already been added`() {
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().withMembers(listOf(student.getEmail()!!)).build())
        commissionService.addStudent(commission.getId()!!, student.getId()!!)
        commissionService.addGroup(commission.getId()!!, group.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/addGroup/{commissionId}/{groupId}",
                commission.getId(),
                group.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
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
                .header("Authorization", "")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does have not permissions to remove group of a commission`() {
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().withEmail("test@gmail.com").build())
        val group = groupService.save(aGroupDTO().withMembers(listOf(student.getEmail()!!)).build())
        commissionService.addStudent(commission.getId()!!, student.getId()!!)
        commissionService.addGroup(commission.getId()!!, group.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeGroup/{commissionId}/{groupId}",
                commission.getId(),
                group.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerStudent())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does have not permissions to remove group of a commission except yourself`() {
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().withMembers(listOf(student.getEmail()!!)).build())
        commissionService.addStudent(commission.getId()!!, student.getId()!!)
        commissionService.addGroup(commission.getId()!!, group.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeGroup/{commissionId}/{groupId}",
                commission.getId(),
                group.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerTeacher())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to remove group of a commission except yourself`() {
        val header = headerTeacher()
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().withMembers(listOf(student.getEmail()!!)).build())
        val teacher = teacherService.findByEmail("docente@gmail.com")
        commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
        commissionService.addStudent(commission.getId()!!, student.getId()!!)
        commissionService.addGroup(commission.getId()!!, group.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeGroup/{commissionId}/{groupId}",
                commission.getId(),
                group.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", header)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when admin remove an existing group of a commission`() {
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        val student = studentService.save(aStudentDTO().build())
        val student2 = studentService.save(aStudentDTO().withEmail("test@gmail.com").build())
        val group = groupService.save(aGroupDTO().withMembers(listOf(student.getEmail()!!,student2.getEmail()!!)).build())
        commissionService.addStudent(commission.getId()!!, student.getId()!!)
        commissionService.addStudent(commission.getId()!!, student2.getId()!!)
        commissionService.addGroup(commission.getId()!!, group.getId()!!)
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeGroup/{commissionId}/{groupId}",
                commission.getId(),
                group.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when remove a non-existent group`() {
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeGroup/{commissionId}/{groupId}",
                commission.getId(),
                -1
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when non-existent commission to remove group`() {
        studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeGroup/{commissionId}/{groupId}",
                -1,
                group.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when trying to remove a group who does not belong to a commission`() {
        matterService.save(aMatter().build())
        val commission = commissionService.save(aCommission().build())
        studentService.save(aStudentDTO().build())
        val group = groupService.save(aGroupDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.put(
                "/commissions/removeGroup/{commissionId}/{groupId}",
                commission.getId(),
                group.getId()
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }


    private fun headerStudent(): String {
        val student = studentService.save(aStudentDTO().build())
        val login = aLoginDTO().withEmail(student.getEmail()).withPassword("funciona").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(status().isOk)

        val stringToken = response.andReturn().response.contentAsString
        return "Bearer ${stringToken.substring(10, stringToken.length - 2)}"
    }

    private fun headerTeacher(): String {
        val teacher = teacherService.save(aTeacherDTO().withEmail("docente@gmail.com").build())
        val login = aLoginDTO().withEmail(teacher.getEmail()).withPassword("funciona").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(status().isOk)

        val stringToken = response.andReturn().response.contentAsString
        return "Bearer ${stringToken.substring(10, stringToken.length - 2)}"
    }

    private fun headerAdmin(): String {
        val admin = adminService.save(aAdminDTO().withEmail("prueba@gmail.com").build())
        val login = aLoginDTO().withEmail(admin.getEmail()).withPassword("funciona").build()
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login))
                .accept("application/json")
        ).andExpect(status().isOk)

        val stringToken = response.andReturn().response.contentAsString
        return "Bearer ${stringToken.substring(10, stringToken.length - 2)}"
    }

    @AfterEach
    fun tearDown() {
        commissionService.clearCommissions()
        groupService.clearGroups()
        studentService.clearStudents()
        teacherService.clearTeachers()
        matterService.clearMatters()
        adminService.clearAdmins()
    }
}