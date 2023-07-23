package inc.kaizen.automata.module.ide

import java.nio.file.Path
import java.nio.file.Paths

class TemplateRenderer {

    fun renderContentFromTemplate(
//        save: (packageName: String, fileName: String, content: String) -> Unit
    ): List<Path>? {
        val resource: Path? = templatePath()
        return resource?.let { readMustacheFolder(it) }
    }

    fun templatePath(): Path? {
//        val resource: URL? = this::class.java.javaClass.classLoader.getResource("template")
//        return if (resource != null) {
//            Paths.get(resource.toURI())
//        } else {
//            null
//        }
        return Paths.get("C:\\Git\\intellij\\automata\\src\\main\\resources\\template")
    }

    private fun readMustacheFolder(folder: Path): List<Path> {
        return folder
            .toFile()
            .walkTopDown()
            .filter { it.isFile && it.extension.endsWith("mustache") }
            .map { it.toPath() }
            .toList()
    }
}