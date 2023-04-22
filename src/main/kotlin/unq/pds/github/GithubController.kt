package unq.pds.github

import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate


@RestController
@CrossOrigin
@RequestMapping("git")
class GithubController {

    private var restTemplate: RestTemplate = RestTemplate()


    @GetMapping
    fun getReposiroies(@RequestParam token: String): String? {
        val url = "https://api.github.com/user/repos"

        // create headers

        // create headers
        val headers = HttpHeaders()
        // set `accept` header
        // set `accept` header
        headers.set("Accept", "application/vnd.github+json")
        // set custom header
        // set custom header
        headers.set("Authorization", "Bearer $token")
        val request: HttpEntity<*> = HttpEntity<Any?>(headers)

        // use `exchange` method for HTTP call

        // use `exchange` method for HTTP call
        val response: ResponseEntity<String> = restTemplate.exchange(url, HttpMethod.GET, request, String::class.java)
        return if (response.statusCode == HttpStatus.OK) {
            response.body
        } else {
            null
        }
    }
}
