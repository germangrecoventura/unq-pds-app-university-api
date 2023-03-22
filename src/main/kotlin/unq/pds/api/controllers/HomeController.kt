package unq.pds.api.controllers

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/")
@CrossOrigin
class HomeController() {
    @GetMapping
    @ResponseBody
    fun index(): String {
        return "Hola"
    }
}