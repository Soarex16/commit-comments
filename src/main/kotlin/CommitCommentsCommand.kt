import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.check
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.restrictTo
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.IllegalArgumentException

class CommitCommentsCommand : CliktCommand(printHelpOnEmptyArgs = true, name = "commit-comments") {
    private val repoUrl by argument(
        "<Repo URL>",
        help = "Github repo URL"
    )
        .check("Value must be valid github repository URL") { repoRegex.matches(it) }

    private val repoRegex = """^https?://github\.com/(?<author>\w+)/(?<repo>\w+)$""".toRegex()

    private val commitsNum by option(
        "-l",
        "--limit",
        help = "Number of commit messages to fetch"
    )
        .int()
        .restrictTo(1, 50)
        .default(10)

    override fun run() {
        val (author, repo) = getRepoInfo(repoUrl!!)

        val formattedEndpoint = "https://api.github.com/repos/${author}/${repo}/commits?per_page=${commitsNum}"

        val client = HttpClient.newBuilder().build();
        val request = HttpRequest.newBuilder()
            .uri(URI.create(formattedEndpoint))
            .build();

        val response = client.send(request, HttpResponse.BodyHandlers.ofString());

        val status = response.statusCode()

        try {
            checkStatusCode(status)
        } catch (e: IllegalArgumentException) {
            println(e.message)
            return
        }

        val commitsJson = Json.decodeFromString<JsonArray>(response.body())

        val commitMessages = commitsJson
            .map { it.jsonObject["commit"]!!.jsonObject["message"] }

        println("Last $commitsNum commits:")
        println(commitMessages.joinToString("\n"))
    }

    /**
     * Extracts author and repository name from GitHub url
     */
    private fun getRepoInfo(url: String): Pair<String, String> {
        val match = repoRegex.matchEntire(url)!!

        val author = match.groups["author"]!!.value
        val repo = match.groups["repo"]!!.value

        return Pair(author, repo)
    }

    /**
     * Returns true if status code successful (200-299)
     * In other cases throws an exception with error message
     */
    private fun checkStatusCode(code: Int) = when (code) {
        in 200..299 -> true
        404 -> throw IllegalArgumentException("Repo not found")
        500 -> throw IllegalArgumentException("Internal server error")
        else -> throw IllegalArgumentException("Unknown error while retrieving. Http status code $code")
    }
}