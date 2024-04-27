package inc.kaizen.automata.core.extension

import inc.kaizen.automata.core.render.TemplateRenderer
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.copyToRecursively

fun String.toPath(): Path = Paths.get(this)

@OptIn(ExperimentalPathApi::class)
fun Path.copyDefaultTemplates() = run {
    val destinationDir = toFile()
    if (destinationDir.exists() || destinationDir.listFiles()?.isNotEmpty() == true) {
        return@run
    }

    if (!toFile().exists()) {
        toFile().mkdirs()
    }
    val uri = TemplateRenderer::class.java.getResource("/template")?.toURI()
    if (uri != null) {
        val myPath: Path = if (uri.scheme.equals("jar")) {
            val fileSystem: FileSystem = FileSystems.newFileSystem(uri, emptyMap<String, Any>())
            fileSystem.getPath("/template")
        } else {
            Paths.get(uri)
        }

        myPath.copyToRecursively(this, followLinks = true)
    } else {
        throw Exception("Could not find default templates in source code")
    }
}