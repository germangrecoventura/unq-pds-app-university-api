package unq.pds.model.exceptions

class EmailAlreadyRegisteredException : RuntimeException() {
    override val message: String
        get() = "The email is already registered"
}