package inc.kaizen.automata.module.extension

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheFactory
import inc.kaizen.automata.module.ide.AutomataModuleDescriptionProvider
import java.io.File
import java.io.StringWriter
import java.nio.file.Path


fun String.readResource(): String? =
    AutomataModuleDescriptionProvider::class.java.getResource(this)?.readText()

fun Path.renderTemplate(scopes: Map<String, Any>): String {
    return StringWriter().use {
        val mf: MustacheFactory = DefaultMustacheFactory()
        val mustache = mf.compile(toFile().bufferedReader(), toFile().name)
        mustache.execute(it, scopes)
    }.toString()
}