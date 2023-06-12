package unq.pds.security

class TokenInfo(jwtToken: String){

    private val jwtToken = jwtToken

    fun getToken(): String{
        return jwtToken
    }
}