package unq.pds.model.exceptions

class NotAuthenticatedException : RuntimeException() {
    override val message: String
        get() = "Not authenticated"
}