package unq.pds.api.controllers

import org.springframework.web.bind.annotation.*
import unq.pds.services.UserService
import java.util.*

@RestController
@CrossOrigin
@RequestMapping("/user")
class UserController(private val userService: UserService) {

    @GetMapping
    fun getUsers() : Int { return userService.count() }

}
