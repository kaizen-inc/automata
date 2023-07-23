package inc.kaizen.automata.project

import com.android.tools.idea.wizard.template.Template
import com.android.tools.idea.wizard.template.WizardTemplateProvider
import inc.kaizen.automata.module.ide.moduleTemplate

class MyProjectTemplatesProvider : WizardTemplateProvider() {

    override fun getTemplates(): List<Template> {
        return listOf(projectTemplate, moduleTemplate)
    }
}