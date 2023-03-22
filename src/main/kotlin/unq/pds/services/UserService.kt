package unq.pds.services

import unq.pds.model.User

interface UserService {
    fun create(user: User): User
    fun count(): Int
    fun clearUsers()
}