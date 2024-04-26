package inc.kaizen.automata.core.render

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheFactory
import inc.kaizen.automata.core.template.ModuleTemplate
import java.io.StringWriter
import java.nio.file.Path
import java.nio.file.Paths

class TemplateRenderer(private val template: ModuleTemplate) {

    private val mustacheFactory: MustacheFactory = DefaultMustacheFactory()

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

    fun renderTemplate(file: Path, scopes: Map<String, Any>): String {
        return StringWriter().use {
            val mustache = mustacheFactory.compile(file.toFile().bufferedReader(), file.toFile().name)
            mustache.execute(it, scopes)
        }.toString()
    }
}