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
import javax.servlet.http.Cookie

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
        val cookie = cookiesStudent()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aMatterDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to create matter`() {
        val cookie = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aMatterDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to create matter`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aMatterDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when the matter has a null name`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aMatterDTO().withName(null).build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }


    @Test
    fun `should throw a 400 status when the matter has a empty name`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aMatterDTO().withName("").build()))
                .cookie(cookie)
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
        val cookie = cookiesStudent()
        val matter = matterService.save(aMatter().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", matter.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to get matter if exist`() {
        val cookie = cookiesTeacher()
        val matter = matterService.save(aMatter().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", matter.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to get matter if exist`() {
        val cookie = cookiesAdmin()
        val matter = matterService.save(aMatter().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", matter.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when a student does have permissions to get matter if not exist`() {
        val cookie = cookiesStudent()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1").cookie(cookie)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when a teacher does have permissions to get matter if not exist`() {
        val cookie = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1").cookie(cookie)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 404 status when a admin does have permissions to get matter if not exist`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", "-1").cookie(cookie)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 400 status when a student are looking for a matter with id and the id is not the proper type`() {
        val cookie = cookiesStudent()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", "DOS")
                .cookie(cookie)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when a teacher are looking for a matter with id and the id is not the proper type`() {
        val cookie = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", "DOS")
                .cookie(cookie)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when a admin are looking for a matter with id and the id is not the proper type`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", "DOS")
                .cookie(cookie)
        )
            .andExpect(status().isBadRequest)
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
        val cookie = cookiesStudent()
        val matter = matterService.save(aMatter().build())

        matter.name = "Lengua"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(matter))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to update matter`() {
        val cookie = cookiesTeacher()
        val matter = matterService.save(aMatter().build())

        matter.name = "Lengua"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(matter))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to update matters`() {
        val cookie = cookiesAdmin()
        val matter = matterService.save(aMatter().build())

        matter.name = "Lengua"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(matter))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isOk)
    }

    @Test
    fun `should throw a 404 status when you update a matter that does not exist`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": \"5\",\n" +
                            "  \"name\": \"Lengua\"\n" +
                            "}"
                )
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 400 status when you update a matter with id null`() {
        val cookie = cookiesAdmin()
        var matter = matterService.save(aMatter().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${null},\n" +
                            "  \"name\": \"${matter.name}\",\n" +
                            "}"
                )
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a matter with id empty`() {
        val cookie = cookiesAdmin()
        var matter = matterService.save(aMatter().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${""},\n" +
                            "  \"name\": \"${matter.name}\",\n" +
                            "}"
                )
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a matter with name null`() {
        val cookie = cookiesAdmin()
        var matter = matterService.save(aMatter().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${matter.getId()},\n" +
                            "  \"name\": \"${null}\",\n" +
                            "}"
                )
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when you update a matter with name empty`() {
        val cookie = cookiesAdmin()
        var matter = matterService.save(aMatter().build())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "  \"id\": ${matter.getId()},\n" +
                            "  \"name\": \"${""}\",\n" +
                            "}"
                )
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when a admin update a matter with already registered name`() {
        val cookie = cookiesAdmin()
        matterService.save(aMatter().withName("Math").build())
        val newMatter = matterService.save(aMatter().withName("Applications development").build())

        newMatter.name = "Math"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newMatter))
                .cookie(cookie)
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
        val cookie = cookiesStudent()
        var matter = matterService.save(aMatter().build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", matter.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a teacher does not have permissions to delete matter`() {
        val cookie = cookiesTeacher()
        var matter = matterService.save(aMatter().build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", matter.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to delete matter`() {
        val cookie = cookiesAdmin()
        var matter = matterService.save(aMatter().build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", matter.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 400 status when the matter id is empty`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", "").cookie(cookie)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 400 status when the id is not the proper type`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", "ala").cookie(cookie)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw a 404 status you want to delete a matter that does not exist`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/groups").accept(MediaType.APPLICATION_JSON)
                .param("id", "2").cookie(cookie)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should throw a 200 status when a student does have permissions to get all matters`() {
        val cookie = cookiesStudent()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters/getAll").accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a teacher does have permissions to get all matters`() {
        val cookie = cookiesTeacher()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters/getAll").accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 200 status when a admin does have permissions to get all matters`() {
        val cookie = cookiesAdmin()
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters/getAll").accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should throw a 401 status when trying to get all matters and is not authenticated`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters/getAll").accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when a create matter with cookie empty`() {
        val cookie = Cookie("jwt", "")
        mockMvc.perform(
            MockMvcRequestBuilders.post("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aMatterDTO().build()))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when get matter with cookie empty`() {
        val cookie = Cookie("jwt", "")
        val matter = matterService.save(aMatter().build())
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", matter.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when update matter with cookie empty`() {
        val cookie = Cookie("jwt", "")
        val matter = matterService.save(aMatter().build())

        matter.name = "Lengua"
        mockMvc.perform(
            MockMvcRequestBuilders.put("/matters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(matter))
                .cookie(cookie)
                .accept("application/json")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when delete matter with cookie empty`() {
        val cookie = Cookie("jwt", "")
        var matter = matterService.save(aMatter().build())
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/matters").accept(MediaType.APPLICATION_JSON)
                .param("id", matter.getId().toString()).cookie(cookie)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should throw a 401 status when get all matters with cookie empty`() {
        val cookie = Cookie("jwt", "")
        mockMvc.perform(
            MockMvcRequestBuilders.get("/matters/getAll").accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        )
            .andExpect(status().isUnauthorized)
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