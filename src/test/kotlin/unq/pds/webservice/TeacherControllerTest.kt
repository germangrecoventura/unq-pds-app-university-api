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
import unq.pds.model.builder.BuilderTeacher.Companion.aTeacher
import unq.pds.model.builder.CommissionBuilder.Companion.aCommission
import unq.pds.model.builder.MatterBuilder.Companion.aMatter
import unq.pds.model.builder.ProjectBuilder.Companion.aProject
import unq.pds.services.*
import unq.pds.services.builder.BuilderAdminDTO.Companion.aAdminDTO
import unq.pds.services.builder.BuilderCommentCreateDTO.Companion.aCommentDTO
import unq.pds.services.builder.BuilderGroupDTO.Companion.aGroupDTO
import unq.pds.services.builder.BuilderLoginDTO.Companion.aLoginDTO
import unq.pds.services.builder.BuilderRepositoryDTO.Companion.aRepositoryDTO
import unq.pds.services.builder.BuilderStudentDTO.Companion.aStudentDTO
import unq.pds.services.builder.BuilderTeacherDTO.Companion.aTeacherDTO

@ExtendWith(SpringExtension::class)
@SpringBootTest
class TeacherControllerTest {
      lateinit var mockMvc: MockMvc

      @Autowired
      lateinit var context: WebApplicationContext

      @Autowired
      lateinit var studentService: StudentService

      @Autowired
      lateinit var teacherService: TeacherService

      @Autowired
      lateinit var adminService: AdminService

      @Autowired
      lateinit var matterService: MatterService

      @Autowired
      lateinit var commissionService: CommissionService

      @Autowired
      lateinit var groupService: GroupService

      @Autowired
      lateinit var projectService: ProjectService

      @Autowired
      lateinit var repositoryService: RepositoryService

      private val mapper = ObjectMapper()

      @Autowired
      lateinit var initializer: Initializer

      @BeforeEach
      fun setUp() {
          initializer.cleanDataBase()
          mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
      }


      @Test
      fun `should throw a 401 status when trying to create a teacher and is not authenticated`() {
          mockMvc.perform(
              MockMvcRequestBuilders.post("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(aTeacherDTO().withEmail("prueba@gmail.com").build()))
                  .header("Authorization", "")
                  .accept("application/json")
          ).andExpect(status().isUnauthorized)
      }

      @Test
      fun `should throw a 401 status when a student does not have permissions to create teacher`() {
          mockMvc.perform(
              MockMvcRequestBuilders.post("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(aTeacherDTO().withEmail("prueba@gmail.com").build()))
                  .header("Authorization", headerStudent())
                  .accept("application/json")
          ).andExpect(status().isUnauthorized)
      }

      @Test
      fun `should throw a 401 status when a teacher does not have permissions to create teacher`() {
          mockMvc.perform(
              MockMvcRequestBuilders.post("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(aTeacherDTO().withEmail("prueba@gmail.com").build()))
                  .header("Authorization", headerTeacher())
                  .accept("application/json")
          ).andExpect(status().isUnauthorized)
      }

      @Test
      fun `should throw a 200 status when a admin does have permissions to create teacher`() {
          mockMvc.perform(
              MockMvcRequestBuilders.post("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(aTeacherDTO().withEmail("prueba@gmail.com").build()))
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isOk)
      }

      @Test
      fun `should throw a 400 status when if firstname is null to create`() {
          mockMvc.perform(
              MockMvcRequestBuilders.post("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(aTeacherDTO().withFirstName(null).build()))
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when if firstname is empty to create`() {
          mockMvc.perform(
              MockMvcRequestBuilders.post("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(aTeacherDTO().withFirstName("").build()))
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when if the firstname has any special characters to create`() {
          mockMvc.perform(
              MockMvcRequestBuilders.post("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(aTeacherDTO().withFirstName("J@").build()))
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when if the firstname has any number to create`() {
          mockMvc.perform(
              MockMvcRequestBuilders.post("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(aTeacherDTO().withFirstName("Jav1er").build()))
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when if lastname is null to create`() {
          mockMvc.perform(
              MockMvcRequestBuilders.post("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(aTeacherDTO().withLastName(null).build()))
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when if lastname is empty to create`() {
          mockMvc.perform(
              MockMvcRequestBuilders.post("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(aTeacherDTO().withLastName("").build()))
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when if the lastname has any special characters to create`() {
          mockMvc.perform(
              MockMvcRequestBuilders.post("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(aTeacherDTO().withLastName("J@").build()))
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when if the lastname has any number to create`() {
          mockMvc.perform(
              MockMvcRequestBuilders.post("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(aTeacherDTO().withLastName("Jav1er").build()))
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when if email is null to create`() {
          mockMvc.perform(
              MockMvcRequestBuilders.post("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(aTeacherDTO().withEmail(null).build()))
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when if email is empty to create`() {
          mockMvc.perform(
              MockMvcRequestBuilders.post("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(aTeacherDTO().withEmail("").build()))
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when the email is already registered to create`() {
          teacherService.save(aTeacherDTO().build())
          mockMvc.perform(
              MockMvcRequestBuilders.post("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(aTeacherDTO().build()))
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when the email is not valid to create`() {
          mockMvc.perform(
              MockMvcRequestBuilders.post("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(aTeacherDTO().withEmail("juanPerezgmail.com").build()))
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 200 status when a student does have permissions to get student if exist`() {
          val teacher = teacherService.save(aTeacherDTO().withEmail("pruebaa@gmail.com").build())
          mockMvc.perform(
              MockMvcRequestBuilders.get("/teachers").accept(MediaType.APPLICATION_JSON)
                  .param("id", teacher.getId().toString())
                  .header("Authorization", headerStudent())
          ).andExpect(status().isOk)
      }

      @Test
      fun `should throw a 200 status when a teacher does have permissions to get student if exist`() {
          val teacher = teacherService.save(aTeacherDTO().withEmail("prueba@gmail.com").build())
          mockMvc.perform(
              MockMvcRequestBuilders.get("/teachers").accept(MediaType.APPLICATION_JSON)
                  .param("id", teacher.getId().toString())
                  .header("Authorization", headerTeacher())
          ).andExpect(status().isOk)
      }

      @Test
      fun `should throw a 200 status when a admin does have permissions to get student if exist`() {
          val teacher = teacherService.save(aTeacherDTO().build())
          mockMvc.perform(
              MockMvcRequestBuilders.get("/teachers").accept(MediaType.APPLICATION_JSON)
                  .param("id", teacher.getId().toString())
                  .header("Authorization", headerAdmin())
          ).andExpect(status().isOk)
      }

      @Test
      fun `should throw a 401 status when trying to get a teacher and is not authenticated`() {
          mockMvc.perform(
              MockMvcRequestBuilders.get("/teachers").accept(MediaType.APPLICATION_JSON)
                  .param("id", "1")
                  .header("Authorization", "")
          ).andExpect(status().isUnauthorized)
      }

      @Test
      fun `should throw a 400 status when a student are looking for a teacher with id null`() {
          mockMvc.perform(
              MockMvcRequestBuilders.get("/teachers").accept(MediaType.APPLICATION_JSON)
                  .param("id", null)
                  .header("Authorization", headerStudent())
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when a teacher are looking for a teacher with id null`() {
          mockMvc.perform(
              MockMvcRequestBuilders.get("/teachers").accept(MediaType.APPLICATION_JSON)
                  .param("id", null)
                  .header("Authorization", headerTeacher())
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when a admin are looking for a teacher with id null`() {
          mockMvc.perform(
              MockMvcRequestBuilders.get("/teachers").accept(MediaType.APPLICATION_JSON)
                  .param("id", null)
                  .header("Authorization", headerAdmin())
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 404 status when a student are looking for a teacher does not exist`() {
          mockMvc.perform(
              MockMvcRequestBuilders.get("/teachers").accept(MediaType.APPLICATION_JSON)
                  .param("id", "-1")
                  .header("Authorization", headerStudent())
          ).andExpect(status().isNotFound)
      }

      @Test
      fun `should throw a 404 status when a teacher are looking for a teacher does not exist`() {
          mockMvc.perform(
              MockMvcRequestBuilders.get("/teachers").accept(MediaType.APPLICATION_JSON)
                  .param("id", "-1")
                  .header("Authorization", headerTeacher())
          ).andExpect(status().isNotFound)
      }

      @Test
      fun `should throw a 404 status when a admin are looking for a teacher does not exist`() {
          mockMvc.perform(
              MockMvcRequestBuilders.get("/teachers").accept(MediaType.APPLICATION_JSON)
                  .param("id", "-1")
                  .header("Authorization", headerAdmin())
          ).andExpect(status().isNotFound)
      }

      @Test
      fun `should throw a 401 status when trying to update a teacher and is not authenticated`() {
          mockMvc.perform(
              MockMvcRequestBuilders.put("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(aTeacher().build()))
                  .header("Authorization", "")
                  .accept("application/json")
          ).andExpect(status().isUnauthorized)
      }

      @Test
      fun `should throw a 401 status when a student does not have permissions to update teacher`() {
          val teacher = teacherService.save(aTeacherDTO().withEmail("pruebaa@gmail.com").build())

          teacher.setFirstName("Jose")
          mockMvc.perform(
              MockMvcRequestBuilders.put("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(teacher))
                  .header("Authorization", headerStudent())
                  .accept("application/json")
          ).andExpect(status().isUnauthorized)
      }

      @Test
      fun `should throw a 401 status when a teacher does not have permissions to update teacher except yourself`() {
          val teacher = teacherService.save(aTeacherDTO().withEmail("newTeacher@gmail.com").build())

          teacher.setFirstName("Jose")
          mockMvc.perform(
              MockMvcRequestBuilders.put("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(teacher))
                  .header("Authorization", headerTeacher())
                  .accept("application/json")
          ).andExpect(status().isUnauthorized)
      }

      @Test
      fun `should throw a 200 status when a admin does have permissions to update teachers`() {
          val teacher = teacherService.save(aTeacherDTO().build())

          teacher.setFirstName("Pepe")
          mockMvc.perform(
              MockMvcRequestBuilders.put("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(teacher))
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isOk)
      }

      @Test
      fun `should throw a 400 status when admin update a teacher with id null`() {
          val teacher = teacherService.save(aTeacherDTO().build())
          mockMvc.perform(
              MockMvcRequestBuilders.put("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      "{\n" +
                              "  \"id\": ${null},\n" +
                              "  \"email\": \"${teacher.getEmail()}\",\n" +
                              "  \"firstName\": ${teacher.getFirstName()},\n" +
                              "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                              "  \"password\": \"${teacher.getPassword()}\"\n" +
                              "}"
                  )
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when admin update a teacher with id empty`() {
          val teacher = teacherService.save(aTeacherDTO().build())
          mockMvc.perform(
              MockMvcRequestBuilders.put("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      "{\n" +
                              "  \"id\": ${""},\n" +
                              "  \"email\": \"${teacher.getEmail()}\",\n" +
                              "  \"firstName\": ${teacher.getFirstName()},\n" +
                              "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                              "  \"password\": \"${teacher.getPassword()}\"\n" +
                              "}"
                  )
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when admin update a teacher with first name null`() {
          val teacher = teacherService.save(aTeacherDTO().build())
          mockMvc.perform(
              MockMvcRequestBuilders.put("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      "{\n" +
                              "  \"id\": ${teacher.getId()},\n" +
                              "  \"email\": \"${teacher.getEmail()}\",\n" +
                              "  \"firstName\": ${null},\n" +
                              "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                              "  \"password\": \"${teacher.getPassword()}\"\n" +
                              "}"
                  )
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when admin update a teacher with first name empty`() {
          val teacher = teacherService.save(aTeacherDTO().build())
          mockMvc.perform(
              MockMvcRequestBuilders.put("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      "{\n" +
                              "  \"id\": ${teacher.getId()},\n" +
                              "  \"email\": \"${teacher.getEmail()}\",\n" +
                              "  \"firstName\": ${""},\n" +
                              "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                              "  \"password\": \"${teacher.getPassword()}\"\n" +
                              "}"
                  )
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when admin update a teacher with first name with number`() {
          val teacher = teacherService.save(aTeacherDTO().build())
          mockMvc.perform(
              MockMvcRequestBuilders.put("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      "{\n" +
                              "  \"id\": ${teacher.getId()},\n" +
                              "  \"email\": \"${teacher.getEmail()}\",\n" +
                              "  \"firstName\": ${"55"},\n" +
                              "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                              "  \"password\": \"${teacher.getPassword()}\"\n" +
                              "}"
                  )
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when admin update a teacher with first name with special character`() {
          val teacher = teacherService.save(aTeacherDTO().build())
          mockMvc.perform(
              MockMvcRequestBuilders.put("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      "{\n" +
                              "  \"id\": ${teacher.getId()},\n" +
                              "  \"email\": \"${teacher.getEmail()}\",\n" +
                              "  \"firstName\": ${"Ger@n"},\n" +
                              "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                              "  \"password\": \"${teacher.getPassword()}\"\n" +
                              "}"
                  )
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when admin update a teacher with last name null`() {
          val teacher = teacherService.save(aTeacherDTO().build())
          mockMvc.perform(
              MockMvcRequestBuilders.put("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      "{\n" +
                              "  \"id\": ${teacher.getId()},\n" +
                              "  \"email\": \"${teacher.getEmail()}\",\n" +
                              "  \"firstName\": ${teacher.getFirstName()},\n" +
                              "  \"lastName\": \"${null}\"\n" +
                              "  \"password\": \"${teacher.getPassword()}\"\n" +
                              "}"
                  )
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when admin update a teacher with last name empty`() {
          val teacher = teacherService.save(aTeacherDTO().build())
          mockMvc.perform(
              MockMvcRequestBuilders.put("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      "{\n" +
                              "  \"id\": ${teacher.getId()},\n" +
                              "  \"email\": \"${teacher.getEmail()}\",\n" +
                              "  \"firstName\": ${teacher.getFirstName()},\n" +
                              "  \"lastName\": \"${""}\"\n" +
                              "  \"password\": \"${teacher.getPassword()}\"\n" +
                              "}"
                  )
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when admin update a teacher with last name with number`() {
          val teacher = teacherService.save(aTeacherDTO().build())
          mockMvc.perform(
              MockMvcRequestBuilders.put("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      "{\n" +
                              "  \"id\": ${teacher.getId()},\n" +
                              "  \"email\": \"${teacher.getEmail()}\",\n" +
                              "  \"firstName\": ${teacher.getFirstName()},\n" +
                              "  \"lastName\": \"${"P3rez"}\"\n" +
                              "  \"password\": \"${teacher.getPassword()}\"\n" +
                              "}"
                  )
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when admin update a teacher with last name with special character `() {
          val teacher = teacherService.save(aTeacherDTO().build())
          mockMvc.perform(
              MockMvcRequestBuilders.put("/students")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      "{\n" +
                              "  \"id\": ${teacher.getId()},\n" +
                              "  \"email\": \"${teacher.getEmail()}\",\n" +
                              "  \"firstName\": ${teacher.getFirstName()},\n" +
                              "  \"lastName\": \"${"#"}\"\n" +
                              "  \"password\": \"${teacher.getPassword()}\"\n" +
                              "}"
                  )
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when admin update a teacher with password null`() {
          val teacher = teacherService.save(aTeacherDTO().build())
          mockMvc.perform(
              MockMvcRequestBuilders.put("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      "{\n" +
                              "  \"id\": ${teacher.getId()},\n" +
                              "  \"email\": \"${teacher.getEmail()}\",\n" +
                              "  \"firstName\": ${teacher.getFirstName()},\n" +
                              "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                              "  \"password\": \"${null}\"\n" +
                              "}"
                  )
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when admin update a teacher with password empty`() {
          val teacher = teacherService.save(aTeacherDTO().build())
          mockMvc.perform(
              MockMvcRequestBuilders.put("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      "{\n" +
                              "  \"id\": ${teacher.getId()},\n" +
                              "  \"email\": \"${teacher.getEmail()}\",\n" +
                              "  \"firstName\": ${teacher.getFirstName()},\n" +
                              "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                              "  \"password\": \"${""}\"\n" +
                              "}"
                  )
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when you update a student with mail null`() {
          val teacher = teacherService.save(aTeacherDTO().build())

          mockMvc.perform(
              MockMvcRequestBuilders.put("/students")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      "{\n" +
                              "  \"id\": ${teacher.getId()},\n" +
                              "  \"email\": \"${null}\",\n" +
                              "  \"password\": \"${teacher.getPassword()}\",\n" +
                              "  \"firstName\": ${teacher.getFirstName()},\n" +
                              "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                              "}"
                  )
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when you update a student with mail empty`() {
          val teacher = teacherService.save(aTeacherDTO().build())

          mockMvc.perform(
              MockMvcRequestBuilders.put("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      "{\n" +
                              "  \"id\": ${teacher.getId()},\n" +
                              "  \"email\": \"${""}\",\n" +
                              "  \"password\": \"${teacher.getPassword()}\",\n" +
                              "  \"firstName\": \"${teacher.getFirstName()}\",\n" +
                              "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                              "}"
                  )
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when you update a student with an invalid email`() {
          val teacher = teacherService.save(aTeacherDTO().build())
          mockMvc.perform(
              MockMvcRequestBuilders.put("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      "{\n" +
                              "  \"id\": ${teacher.getId()},\n" +
                              "  \"email\": \"${"germacom"}\",\n" +
                              "  \"password\": \"${teacher.getPassword()}\",\n" +
                              "  \"firstName\": \"${teacher.getFirstName()}\",\n" +
                              "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                              "}"
                  )
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 400 status when admin update a teacher with already registered email`() {
          val teacher = teacherService.save(aTeacherDTO().build())
          val teacher2 = teacherService.save(aTeacherDTO().withEmail("hola@gmail.com").build())

          mockMvc.perform(
              MockMvcRequestBuilders.put("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      "{\n" +
                              "  \"id\": ${teacher2.getId()},\n" +
                              "  \"email\": \"${teacher.getEmail()}\",\n" +
                              "  \"password\": \"${teacher2.getPassword()}\",\n" +
                              "  \"firstName\": \"${teacher2.getFirstName()}\",\n" +
                              "  \"lastName\": \"${teacher2.getLastName()}\"\n" +
                              "}"
                  )
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 404 status when admin update a teacher if not exists`() {
          val teacher = teacherService.save(aTeacherDTO().build())
          mockMvc.perform(
              MockMvcRequestBuilders.put("/teachers")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      "{\n" +
                              "  \"id\": ${"-20"},\n" +
                              "  \"email\": \"${teacher.getEmail()}\",\n" +
                              "  \"password\": \"${teacher.getPassword()}\",\n" +
                              "  \"firstName\": \"${teacher.getFirstName()}\",\n" +
                              "  \"lastName\": \"${teacher.getLastName()}\"\n" +
                              "}"
                  )
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isNotFound)
      }

      @Test
      fun `should throw a 401 status when trying to delete a teacher and is not authenticated`() {
          mockMvc.perform(
              MockMvcRequestBuilders.delete("/teachers").accept(MediaType.APPLICATION_JSON)
                  .param("id", "1")
                  .header("Authorization", "")
          ).andExpect(status().isUnauthorized)
      }

      @Test
      fun `should throw a 401 status when a student does not have permissions to delete teachers`() {
          val teacher = teacherService.save(aTeacherDTO().withEmail("germanF@gmail.com").build())
          mockMvc.perform(
              MockMvcRequestBuilders.delete("/teachers").accept(MediaType.APPLICATION_JSON)
                  .param("id", teacher.getId().toString())
                  .header("Authorization", headerStudent())
          ).andExpect(status().isUnauthorized)
      }

      @Test
      fun `should throw a 401 status when a teacher does not have permissions to delete teachers`() {
          val teacher = teacherService.save(aTeacherDTO().withEmail("germanF@gmail.com").build())
          mockMvc.perform(
              MockMvcRequestBuilders.delete("/teachers").accept(MediaType.APPLICATION_JSON)
                  .param("id", teacher.getId().toString())
                  .header("Authorization", headerTeacher())
          ).andExpect(status().isUnauthorized)
      }

      @Test
      fun `should throw a 200 status when a admin does have permissions to delete teachers`() {
          val teacher = teacherService.save(aTeacherDTO().withEmail("germanF@gmail.com").build())
          mockMvc.perform(
              MockMvcRequestBuilders.delete("/teachers").accept(MediaType.APPLICATION_JSON)
                  .param("id", teacher.getId().toString())
                  .header("Authorization", headerAdmin())
          ).andExpect(status().isOk)
      }

      @Test
      fun `should throw a 400 status when admin deleting a teacher with id null`() {
          mockMvc.perform(
              MockMvcRequestBuilders.delete("/teachers").accept(MediaType.APPLICATION_JSON)
                  .param("id", null)
                  .header("Authorization", headerAdmin())
          ).andExpect(status().isBadRequest)
      }

      @Test
      fun `should throw a 404 status when admin deleting a non-existent teacher`() {
          mockMvc.perform(
              MockMvcRequestBuilders.delete("/teachers").accept(MediaType.APPLICATION_JSON)
                  .param("id", "-1")
                  .header("Authorization", headerAdmin())
          ).andExpect(status().isNotFound)
      }

      @Test
      fun `should throw a 200 status when student recover an empty list of teachers when recover all and there is no persistence to student`() {
          mockMvc.perform(
              MockMvcRequestBuilders.get("/teachers/getAll").accept(MediaType.APPLICATION_JSON)
                  .header("Authorization", headerStudent())
          ).andExpect(status().isOk)
      }

      @Test
      fun `should throw a 200 status when teacher recover an empty list of teachers when recover all and there is no persistence to student`() {
          mockMvc.perform(
              MockMvcRequestBuilders.get("/teachers/getAll").accept(MediaType.APPLICATION_JSON)
                  .header("Authorization", headerTeacher())
          ).andExpect(status().isOk)
      }

      @Test
      fun `should throw a 200 status when admin recover an empty list of teachers when recover all and there is no persistence to student`() {
          mockMvc.perform(
              MockMvcRequestBuilders.get("/teachers/getAll").accept(MediaType.APPLICATION_JSON)
                  .header("Authorization", headerAdmin())
          ).andExpect(status().isOk)
      }

      @Test
      fun `should throw a 401 status when trying to recover all teachers and is not authenticated`() {
          mockMvc.perform(
              MockMvcRequestBuilders.get("/teachers/getAll").accept(MediaType.APPLICATION_JSON)
                  .header("Authorization", "")
          ).andExpect(status().isUnauthorized)
      }

      @Test
      fun `should throw a 401 status when trying to add a comment to a repository and is not authenticated`() {
          mockMvc.perform(
              MockMvcRequestBuilders.post("/teachers/addComment")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(aCommentDTO().build()))
                  .header("Authorization", "")
                  .accept("application/json")
          ).andExpect(status().isUnauthorized)
      }

      @Test
      fun `should throw a 401 status when student does not have permissions to add a comment to a repository`() {
          mockMvc.perform(
              MockMvcRequestBuilders.post("/teachers/addComment")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(aCommentDTO().build()))
                  .header("Authorization", headerStudent())
                  .accept("application/json")
          ).andExpect(status().isUnauthorized)
      }

      @Test
      fun `should throw a 401 status when teacher does not have permissions to add a comment to a repository`() {
          mockMvc.perform(
              MockMvcRequestBuilders.post("/teachers/addComment")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(aCommentDTO().build()))
                  .header("Authorization", headerTeacher())
                  .accept("application/json")
          ).andExpect(status().isUnauthorized)
      }

      @Test
      fun `should throw a 200 status when teacher does have permissions to add a comment to a repository`() {
          val header = headerTeacher()
          matterService.save(aMatter().build())
          val teacher = teacherService.findByEmail("german@gmail.com")
          val commission = commissionService.save(aCommission().build())
          val student = studentService.save(aStudentDTO().withEmail("prueba@gmail.com").build())
          val group = groupService.save(aGroupDTO().withMembers(listOf("prueba@gmail.com")).build())
          commissionService.addStudent(commission.getId()!!, student.getId()!!)
          commissionService.addTeacher(commission.getId()!!, teacher.getId()!!)
          commissionService.addGroup(commission.getId()!!, group.getId()!!)
          val project = projectService.save(aProject().build())
          val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
          groupService.addProject(group.getId()!!, project.getId()!!)
          projectService.addRepository(project.getId()!!, repository.id)

          mockMvc.perform(
              MockMvcRequestBuilders.post("/teachers/addComment")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(aCommentDTO().withId(repository.id).build()))
                  .header("Authorization", header)
                  .accept("application/json")
          ).andExpect(status().isOk)
      }

      @Test
      fun `should throw a 200 status when admin does have permissions to add a comment to a repository`() {
          val project = projectService.save(aProject().build())
          val repository = repositoryService.save(aRepositoryDTO().withProjectId(project.getId()!!).build())
          projectService.addRepository(project.getId()!!, repository.id)

          mockMvc.perform(
              MockMvcRequestBuilders.post("/teachers/addComment")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(aCommentDTO().withId(repository.id).build()))
                  .header("Authorization", headerAdmin())
                  .accept("application/json")
          ).andExpect(status().isOk)
      }


      private fun headerTeacher(): String {
          teacherService.save(aTeacherDTO().build())
          val login = aLoginDTO().build()
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
          studentService.save(aStudentDTO().build())
          val login = aLoginDTO().build()
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
          val login = aLoginDTO().withEmail(admin.getEmail()).build()
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