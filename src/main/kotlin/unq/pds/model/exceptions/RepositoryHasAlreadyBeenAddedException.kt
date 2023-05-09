package unq.pds.model.exceptions

class RepositoryHasAlreadyBeenAddedException : RuntimeException() {
    override val message: String
        get() = "The repository has already been added to a project"
}