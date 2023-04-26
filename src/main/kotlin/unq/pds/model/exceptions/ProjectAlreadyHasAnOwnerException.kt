package unq.pds.model.exceptions

class ProjectAlreadyHasAnOwnerException : RuntimeException() {
    override val message: String
        get() = "The project already has an owner"
}