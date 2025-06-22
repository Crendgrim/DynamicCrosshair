import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.file.Directory
import org.gradle.kotlin.dsl.maven
import org.gradle.language.jvm.tasks.ProcessResources

val Project.mod: ModData get() = ModData(this)
fun Project.prop(key: String): String? = findProperty(key)?.toString()
fun String.upperCaseFirst() = replaceFirstChar { if (it.isLowerCase()) it.uppercaseChar() else it }

fun RepositoryHandler.strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
    forRepository { maven(url) { name = alias } }
    filter { groups.forEach(::includeGroup) }
}

fun ProcessResources.properties(files: Iterable<String>, vararg properties: Pair<String, Any>) {
    for ((name, value) in properties) inputs.property(name, value)
    filesMatching(files) {
        expand(properties.toMap())
    }
}

/**
 * Checks all mixin configs and removes those entries that do not have an associated class file in the given build
 * directory. This allows for mixins to be wrapped in stonecutter versioning comments and completely removed from
 * the finished mod jar. Note that this only refreshes when the mixin file gets edited!
 */
fun ProcessResources.filterVersionedMixins(buildDir: Directory) {
    filesMatching("*.mixins.json") {
        var first = true
        var path = ""
        filter {
            var result: String? = it
            if (it.contains("\"package\"")) {
                path = it.split(":")[1]
                    .trim()
                    .replace("\"", "")
                    .replace(",", "")
                    .replace(".", "/")
            }
            if (it.startsWith("    \"")) {
                val clazz = it
                    .trim()
                    .replace("\"", "")
                    .replace(",", "")
                    .replace(".", "/")
                if (java.io.File("${buildDir}/classes/java/main/${path}/${clazz}.class").exists()) {
                    // Since we can only do line-by-line filtering, removing lines from the end of the array will cause
                    // the resulting JSON to be malformed. JSON is a bad format. The workaround is to prepend a comma to
                    // every (not-first) line instead. It will get pretty-printed later, so we can ignore formatting.
                    result = it.replace(",", "")
                    if (first) {
                        first = false
                    } else {
                        result = ", $result"
                    }
                } else
                    result = null
            }
            // gradle is stupid and says this is an unexpected type, but filter expects "null" return to remove lines...
            // This is also why we store the result in a temporary variable, so that not the entire filter above gets
            // highlighted as wrong in IDEA.
            // One day this will be fixed.
            result
        }
    }
}

@JvmInline
value class ModData(private val project: Project) {
    val id: String get() = requireNotNull(project.prop("mod.id")) { "Missing 'mod.id'" }
    val name: String get() = requireNotNull(project.prop("mod.name")) { "Missing 'mod.name'" }
    val apiVersion: String get() = requireNotNull(project.prop("mod.api_version")) { "Missing 'mod.api_version'" }
    val version: String get() = requireNotNull(project.prop("mod.version")) { "Missing 'mod.version'" }
    val group: String get() = requireNotNull(project.prop("mod.group")) { "Missing 'mod.group'" }

    fun prop(key: String) = requireNotNull(project.prop("mod.$key")) { "Missing 'mod.$key'" }
    fun dep(key: String) = requireNotNull(project.prop("deps.$key")) { "Missing 'deps.$key'" }
    fun publish(key: String) = project.prop("publish.$key") == "true"
}