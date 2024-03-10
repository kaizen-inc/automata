package inc.kaizen.automate.core.render

import inc.kaizen.automate.core.template.ModuleTemplate
import java.nio.file.Path
import java.nio.file.Paths

class TemplateRenderer(private val template: ModuleTemplate) {

    fun renderContentFromTemplate(): List<Path>? {
        val resource: Path? = templatePath()
        return resource?.let { readMustacheFolder(it) }
    }

    fun templatePath(): Path? {
        return Paths.get(template.templatePath)
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