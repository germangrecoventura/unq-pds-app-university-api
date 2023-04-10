package unq.pds.api

import java.util.regex.Pattern

class Validator {

    companion object {
        private val specialCharacterPattern =
            Pattern.compile("[¡!@#$%&*()_+=|<>¿?{}.\\[\\]~-]", Pattern.CASE_INSENSITIVE)
        private val emailAddressPattern = Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$", Pattern.CASE_INSENSITIVE
        )

        fun containsNumber(toCheck: String?): Boolean {
            if (toCheck == null) {
                return false
            }
            return toCheck.any { Character.isDigit(it) }
        }

        fun isAllNumbers(toCheck: String?): Boolean {
            if (toCheck == null) {
                return false
            }
            return toCheck.all { Character.isDigit(it) }
        }

        fun containsSpecialCharacter(toCheck: String?): Boolean {
            if (toCheck == null) {
                return false
            }
            val matcher = specialCharacterPattern.matcher(toCheck)
            return matcher.find()
        }

        fun isValidEMail(toCheck: String?): Boolean {
            if (toCheck == null) {
                return false
            }
            val matcher = emailAddressPattern.matcher(toCheck)
            return matcher.matches()
        }
    }
}
