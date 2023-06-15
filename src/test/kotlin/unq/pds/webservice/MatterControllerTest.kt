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
import unq.pds.model.builder.MatterBuilder.Companion.aMatter
import unq.pds.services.AdminService
import unq.pds.services.MatterService
import unq.pds.services.StudentService
import unq.pds.services.TeacherService
import unq.pds.services.builder.BuilderAdminDTO.Companion.aAdminDTO
import unq.pds.services.builder.BuilderLoginDTO.Companion.aLoginDTO
import unq.pds.services.builder.BuilderMatterDTO.Companion.aMatterDTO
import unq.pds.services.builder.BuilderStudentDTO.Companion.aStudentDTO
import unq.pds.services.builder.BuilderTeacherDTO.Companion.aTeacherDTO

@ExtendWith(SpringExtension::class)
@SpringBootTest
class MatterControllerTest {
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var matterService: MatterService

    @Autowired
    lateinit var teacherService: TeacherService

    @Autowired
    lateinit var studentService: StudentService

    @Autowired
    lateinit var adminService: AdminService

    private val mapper = ObjectMapper()

    @Autowired
    lateinit var initializer: Initializer

    @BeforeEach
    fun setUp() {
        initializer.cleanDataBase()
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }


    @Test
    fun `should throw a 401 status when trying to create a matter and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aMatterDTO().build()))
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to create matter`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aMatterDTO().build()))
                .header("Authorization", headerStudent())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to create matter`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aMatterDTO().build()))
                .header("Authorization", headerTeacher())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to create matter`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aMatterDTO().build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when the matter has a null name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aMatterDTO().withName(null).build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the matter has a empty name`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aMatterDTO().withName("").build()))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 401 status when trying to get a matter and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to get matter if exist`() {
        val matter = matterService.save(aMatter().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", matter.getId().toString())
                .header("Authorization", headerStudent())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to get matter if exist`() {
        val matter = matterService.save(aMatter().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", matter.getId().toString())
                .header("Authorization", headerTeacher())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to get matter if exist`() {
        val matter = matterService.save(aMatter().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", matter.getId().toString())
                .header("Authorization", headerAdmin())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when a student does have permissions to get matter if not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1")
                .header("Authorization", headerStudent())
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when a teacher does have permissions to get matter if not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1")
                .header("Authorization", headerTeacher())
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when a admin does have permissions to get matter if not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1")
                .header("Authorization", headerAdmin())
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 400 status when a student are looking for a matter with id and the id is not the proper type`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", "DOS")
                .header("Authorization", headerStudent())
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when a teacher are looking for a matter with id and the id is not the proper type`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", "DOS")
                .header("Authorization", headerTeacher())
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when a admin are looking for a matter with id and the id is not the proper type`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", "DOS")
                .header("Authorization", headerAdmin())
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 401 status when trying to update a matter and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aMatter().build()))
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to update matter`() {
        val matter = matterService.save(aMatter().build())

        matter.name = "Lengua"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(matter))
                .header("Authorization", headerStudent())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to update matter`() {
        val matter = matterService.save(aMatter().build())

        matter.name = "Lengua"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(matter))
                .header("Authorization", headerTeacher())
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to update matters`() {
        val matter = matterService.save(aMatter().build())

        matter.name = "Lengua"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(matter))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when you update a matter that does not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": \"5\",\n" +
                            "  \"name\": \"Lengua\"\n" +
                            "}"
                )
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 400 status when you update a matter with id null`() {
        val matter = matterService.save(aMatter().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${null},\n" +
                            "  \"name\": \"${matter.name}\",\n" +
                            "}"
                )
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a matter with id empty`() {
        val matter = matterService.save(aMatter().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${""},\n" +
                            "  \"name\": \"${matter.name}\",\n" +
                            "}"
                )
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a matter with name null`() {
        val matter = matterService.save(aMatter().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${matter.getId()},\n" +
                            "  \"name\": \"${null}\",\n" +
                            "}"
                )
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a matter with name empty`() {
        val matter = matterService.save(aMatter().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${matter.getId()},\n" +
                            "  \"name\": \"${""}\",\n" +
                            "}"
                )
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when a admin update a matter with already registered name`() {
        matterService.save(aMatter().withName("Math").build())
        val newMatter = matterService.save(aMatter().withName("Applications development").build())

        newMatter.name = "Math"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newMatter))
                .header("Authorization", headerAdmin())
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 401 status when trying to delete a matter and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", "1")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a student does not have permissions to delete matter`() {
        val matter = matterService.save(aMatter().build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", matter.getId().toString())
                .header("Authorization", headerStudent())
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to delete matter`() {
        val matter = matterService.save(aMatter().build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", matter.getId().toString())
                .header("Authorization", headerTeacher())
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to delete matter`() {
        val matter = matterService.save(aMatter().build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", matter.getId().toString())
                .header("Authorization", headerAdmin())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when the matter id is empty`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", "")
                .header("Authorization", headerAdmin())
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the id is not the proper type`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", "ala")
                .header("Authorization", headerAdmin())
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status you want to delete a matter that does not exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "2")
                .header("Authorization", headerAdmin())
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to get all matters`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters/getAll").accept(MediaType.APPLICATION_JSON)
                .header("Authorization", headerStudent())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to get all matters`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters/getAll").accept(MediaType.APPLICATION_JSON)
                .header("Authorization", headerTeacher())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to get all matters`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters/getAll").accept(MediaType.APPLICATION_JSON)
                .header("Authorization", headerAdmin())
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 401 status when trying to get all matters and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters/getAll").accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a create matter with header empty`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aMatterDTO().build()))
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when get matter with header empty`() {
        val matter = matterService.save(aMatter().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", matter.getId().toString())
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when update matter with header empty`() {
        val matter = matterService.save(aMatter().build())
        matter.name = "Lengua"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(matter))
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when delete matter with header empty`() {
        val matter = matterService.save(aMatter().build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", matter.getId().toString())
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when get all matters with header empty`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters/getAll").accept(MediaType.APPLICATION_JSON)
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
}