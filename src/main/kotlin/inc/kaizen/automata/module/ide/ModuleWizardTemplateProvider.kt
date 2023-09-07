package inc.kaizen.automata.module.ide

import com.android.tools.idea.wizard.template.Template
import com.android.tools.idea.wizard.template.WizardTemplateProvider
import inc.kaizen.automata.module.extension.copyTo
import inc.kaizen.automata.module.extension.toPath
import java.nio.file.Path

class ModuleWizardTemplateProvider : WizardTemplateProvider() {

    init {
        val templatePath: Path = System.getProperty("user.home")
            .toPath()
            .resolve("templates")
            .resolve("ClientModule")
        "templates".copyTo(templatePath)
    }

    override fun getTemplates(): List<Template> {
        return listOf(moduleTemplate)
    }
}