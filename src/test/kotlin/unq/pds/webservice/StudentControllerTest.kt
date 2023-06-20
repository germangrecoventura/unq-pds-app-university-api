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
import unq.pds.model.builder.BuilderStudent.Companion.aStudent
import unq.pds.services.AdminService
import unq.pds.services.StudentService
import unq.pds.services.TeacherService
import unq.pds.services.builder.BuilderAdminDTO.Companion.aAdminDTO
import unq.pds.services.builder.BuilderLoginDTO.Companion.aLoginDTO
import unq.pds.services.builder.BuilderStudentDTO.Companion.aStudentDTO
import unq.pds.services.builder.BuilderTeacherDTO.Companion.aTeacherDTO

@ExtendWith(SpringExtension::class)
@SpringBootTest
class StudentControllerTest {

    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var studentService: StudentService

    @Autowired
    lateinit var teacherService: TeacherService

    @Autowired
    lateinit var adminService: AdminService

    private val mapper = ObjectMapper()

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }


    @Test
    fun `should throw a 401 status when trying to create a student and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withEmail("prueba@gmail.com").build()))
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to create students`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withEmail("prueba@gmail.com").build()))
                .header("Authorization", headerStudent())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to create students`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().build()))
                .header("Authorization", headerTeacher())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to create students`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when the student has a null firstname`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withFirstName(null).build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a empty first name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withFirstName("").build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has numbers in his first name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withFirstName("G3rman").build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a special character in his first name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withFirstName("Germ@n").build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a null last name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withLastName(null).build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a empty last name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withLastName("").build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has numbers in his last name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withLastName("G3rman").build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a special character in his last name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withLastName("Germ@n").build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a null email`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withEmail(null).build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a empty email`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withEmail("").build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a null password`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withPassword(null).build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has a empty password`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withPassword("").build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the student has an invalid email`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().withEmail("german.com").build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the email is already registered`() {
        studentService.save(aStudentDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudentDTO().build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 401 status when trying to get a student and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
                .header("Authorization", "")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to get student if exist`() {
        val student = studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", student.getId().toString())
                .header("Authorization", headerStudent())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to get student if exist`() {
        val student = studentService.save(aStudentDTO().withEmail("pruebaa@gmail.com").build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", student.getId().toString())
                .header("Authorization", headerTeacher())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to get student if exist`() {
        val student = studentService.save(aStudentDTO().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", student.getId().toString())
                .header("Authorization", headerAdmin())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when a student are looking for a student with id null`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", null)
                .header("Authorization", headerStudent())
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when a teacher are looking for a student with id null`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", null)
                .header("Authorization", headerTeacher())
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when a admin are looking for a student with id null`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", null)
                .header("Authorization", headerAdmin())
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status when a student does have permissions to get student if not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1")
                .header("Authorization", headerStudent())
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when a teacher does have permissions to get student if not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1")
                .header("Authorization", headerTeacher())
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when a admin does have permissions to get student if not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1")
                .header("Authorization", headerAdmin())
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 401 status when trying to update a student and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aStudent().build()))
                .header("Authorization", "")
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to update students except yourself`() {
        val student = studentService.save(aStudentDTO().withEmail("jose@gmail.com").build())

        student.setFirstName("Jose")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(student))
                .header("Authorization", headerStudent())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to update students`() {
        val student = studentService.save(aStudentDTO().withEmail("jose@gmail.com").build())

        student.setFirstName("Jose")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(student))
                .header("Authorization", headerTeacher())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to update students`() {
        val student = studentService.save(aStudentDTO().withEmail("jose@gmail.com").build())

        student.setFirstName("Jose")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(student))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when you update a student that does not exist`() {
        val student = aStudentDTO().withId(-1).build()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(student))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 400 status when you update a student with first name null`() {
        val student = studentService.save(aStudentDTO().build())
        val studentToUpdate = aStudentDTO().withId(student.getId()).build()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${studentToUpdate.id},\n" +
                            "  \"email\": \"${studentToUpdate.email}\",\n" +
                            "  \"firstName\": ${null},\n" +
                            "  \"lastName\": \"${studentToUpdate.lastName}\"\n" +
                            "}"
                )
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with first name empty`() {
        val student = studentService.save(aStudentDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${student.getId()},\n" +
                            "  \"email\": \"${student.getEmail()}\",\n" +
                            "  \"firstName\": \"${""}\",\n" +
                            "  \"lastName\": \"${student.getLastName()}\"\n" +
                            "}"
                )
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with first name with numbers`() {
        val student = studentService.save(aStudentDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${student.getId()},\n" +
                            "  \"email\": \"${student.getEmail()}\",\n" +
                            "  \"firstName\": \"${"G2erman"}\",\n" +
                            "  \"lastName\": \"${student.getLastName()}\"\n" +
                            "}"
                )
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with first name with special character`() {
        val student = studentService.save(aStudentDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${student.getId()},\n" +
                            "  \"email\": \"${student.getEmail()}\",\n" +
                            "  \"firstName\": \"${"G@rman"}\",\n" +
                            "  \"lastName\": \"${student.getLastName()}\"\n" +
                            "}"
                )
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with last name null`() {
        val student = studentService.save(aStudentDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${student.getId()},\n" +
                            "  \"email\": \"${student.getEmail()}\",\n" +
                            "  \"firstName\": ${student.getFirstName()},\n" +
                            "  \"lastName\": \"${null}\"\n" +
                            "}"
                )
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with last name empty`() {
        val student = studentService.save(aStudentDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${student.getId()},\n" +
                            "  \"email\": \"${student.getEmail()}\",\n" +
                            "  \"firstName\": \"${student.getFirstName()}\",\n" +
                            "  \"lastName\": \"${""}\"\n" +
                            "}"
                )
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with last name with numbers`() {
        val student = studentService.save(aStudentDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${student.getId()},\n" +
                            "  \"email\": \"${student.getEmail()}\",\n" +
                            "  \"firstName\": \"${"Ger"}\",\n" +
                            "  \"lastName\": \"${"G2erman"}\"\n" +
                            "}"
                )
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with last name with special character`() {
        val student = studentService.save(aStudentDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${student.getId()},\n" +
                            "  \"email\": \"${student.getEmail()}\",\n" +
                            "  \"firstName\": \"${"Ger"}\",\n" +
                            "  \"lastName\": \"${"G@rman"}\"\n" +
                            "}"
                )
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with mail null`() {
        val student = studentService.save(aStudentDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${student.getId()},\n" +
                            "  \"email\": \"${null}\",\n" +
                            "  \"firstName\": ${student.getFirstName()},\n" +
                            "  \"lastName\": \"${student.getLastName()}\"\n" +
                            "}"
                )
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with mail empty`() {
        val student = studentService.save(aStudentDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${student.getId()},\n" +
                            "  \"email\": \"${""}\",\n" +
                            "  \"firstName\": \"${student.getFirstName()}\",\n" +
                            "  \"lastName\": \"${student.getLastName()}\"\n" +
                            "}"
                )
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with an invalid email`() {
        val student = studentService.save(aStudentDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${student.getId()},\n" +
                            "  \"email\": \"${"germacom"}\",\n" +
                            "  \"firstName\": \"${student.getFirstName()}\",\n" +
                            "  \"lastName\": \"${student.getLastName()}\"\n" +
                            "}"
                )
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a student with already registered email`() {
        val student = studentService.save(aStudentDTO().build())
        val student2 = studentService.save(aStudentDTO().withEmail("hola@gmail.com").build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${student2.getId()},\n" +
                            "  \"email\": \"${student.getEmail()}\",\n" +
                            "  \"firstName\": \"${student2.getFirstName()}\",\n" +
                            "  \"lastName\": \"${student2.getLastName()}\"\n" +
                            "}"
                )
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 401 status when trying to delete a student and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
                .header("Authorization", "")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to delete students`() {
        val student = studentService.save(aStudentDTO().withEmail("jose@gmail.com").build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", student.getId().toString())
                .header("Authorization", headerStudent())
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to delete students`() {
        val student = studentService.save(aStudentDTO().withEmail("jose@gmail.com").build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", student.getId().toString())
                .header("Authorization", headerTeacher())
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to delete students`() {
        val student = studentService.save(aStudentDTO().build())

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", student.getId().toString())
                .header("Authorization", headerAdmin())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when admin deleting a student with id null`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", null)
                .header("Authorization", headerAdmin())
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status you want to delete a student that does not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/students").accept(MediaType.APPLICATION_JSON)
                .param("id", "2")
                .header("Authorization", headerAdmin())
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to get all students`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students/getAll").accept(MediaType.APPLICATION_JSON)
                .header("Authorization", headerStudent())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to get all students`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students/getAll").accept(MediaType.APPLICATION_JSON)
                .header("Authorization", headerTeacher())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to get all students`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students/getAll").accept(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 401 status when trying to get all students and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/students/getAll").accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "")
        ).andExpect(status().isUnauthorized)
    }


    private fun headerTeacher(): String {
        val teacher = teacherService.save(aTeacherDTO().build())
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

    private fun headerAdmin(): String {
        val admin = adminService.save(aAdminDTO().build())
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
        studentService.clearStudents()
        teacherService.clearTeachers()
        adminService.clearAdmins()
    }
}