package unq.pds.model.builder

import org.jasypt.util.text.AES256TextEncryptor
import unq.pds.model.Teacher


class BuilderTeacher {
    private var firstName: String? = "German"
    private var lastName: String? = "Fernandez"
    private var emailAddress: String? = "german@gmail.com"
    private var password: String? = "QVNm6Z3nmXAqTzQUDWrGgTGLoyVKPw+z+RZ4784R4MZi5E2OpjqR01ChmR2qTmgo"
    fun build(): Teacher {
        return Teacher(firstName!!, lastName!!, emailAddress!!, password!!)
    }

    fun withFirstName(name: String?): BuilderTeacher {
        this.firstName = name
        return this
    }

    fun withLastName(lastname: String?): BuilderTeacher {
        this.lastName = lastname
        return this
    }

    fun withEmail(email: String?): BuilderTeacher {
        this.emailAddress = email
        return this
    }

    fun withPassword(password: String?): BuilderTeacher {
        val encryptor = AES256TextEncryptor()
        encryptor.setPassword(System.getenv("ENCRYPT_PASSWORD"))
        val myEncryptedPassword = encryptor.encrypt(password)
        this.password = myEncryptedPassword
        return this
    }

    companion object {
        fun aTeacher(): BuilderTeacher {
            return BuilderTeacher()
        }
    }
}