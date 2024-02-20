package inc.kaizen.automata.module

import inc.kaizen.automata.module.extension.renderTemplate
import inc.kaizen.automata.module.ide.TemplateRenderer
import inc.kaizen.automata.module.settings.ModuleTemplate
import java.util.*
import kotlin.io.path.relativeTo

class Main

fun main() {
    val scopes = mutableMapOf<String, Any>()
    val packageName = "inc.kaizen"
    scopes["packageName"] = packageName
    scopes["featureName"] = "test"
    scopes["feature"] = "Test"
    scopes["isPaginationRequired"] = false
    scopes["databaseFileName"] = "testing"

    val templateRenderer = TemplateRenderer(ModuleTemplate())
    val templatePath = templateRenderer.templatePath()
    val paths = templateRenderer.renderContentFromTemplate()
    if (templatePath != null) {
        paths?.forEach { path ->
            val packageExtension = path
                .relativeTo(templatePath)
                .toString()
                .replace(".mustache", "")
                .replace("\\", ".")
                .lowercase(Locale.getDefault())
            val completePackageName = "$packageName.$packageExtension"
            val className = path.toFile().nameWithoutExtension
            scopes["completePackageName"] = completePackageName
            scopes["className"] = className
            val classContent = path.renderTemplate(scopes)
            println(classContent)
//            save(classContent, moduleData.srcDir.resolve(completePackageName).resolve("${file.nameWithoutExtension}.kt"))
        }
    }
}