package api.controllers

import dao.HibernateUserDAO
import entityManager
import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import services.UserServiceImpl
import java.util.*

class UserController {
    fun getUsers(ctx: Context) {
        try {
            val userDAO = HibernateUserDAO(ctx.entityManager)
            val userService = UserServiceImpl(userDAO)
            val user = userService.count()
            ctx.json(user)
        } catch (e: Exception) {
            throw BadRequestResponse(e.message!!)
        }
    }

}
