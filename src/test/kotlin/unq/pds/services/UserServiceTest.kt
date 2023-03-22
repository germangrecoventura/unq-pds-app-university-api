package unq.pds.services

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unq.pds.model.User

@SpringBootTest
class UserServiceTest {

    @Autowired lateinit var userService: UserService

    @BeforeEach
    fun setUp() {

    }

    @Test
    fun `basic entity checks`() {
        Assertions.assertTrue(userService.count() == 0)
    }

    @Test
    fun `hay 1 usuario en la base luego de agregarlo`() {
        userService.create(User("German"))
        Assertions.assertTrue(userService.count() == 1)
    }

    @AfterEach
    fun tearDown() {
        userService.clearUsers()
    }
}
