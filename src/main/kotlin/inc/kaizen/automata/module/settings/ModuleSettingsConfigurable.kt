package inc.kaizen.automata.module.settings

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class ModuleSettingsConfigurable: Configurable {

    private var mySettingsComponent: ModuleSettingsComponent = ModuleSettingsComponent()

    override fun getDisplayName(): String {
        return "Automata"
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return mySettingsComponent.getPreferredFocusedComponent()
    }

    override fun createComponent(): JComponent {
        return mySettingsComponent.getPanel()
    }

    override fun isModified(): Boolean =
        mySettingsComponent.getTemplates() !=
                ModuleSettings.getInstance().templates

    override fun apply() {
        val items = mutableListOf<ModuleTemplate>()
        mySettingsComponent.getTemplates().forEach { moduleTemplate ->
            items.add(moduleTemplate)
        }
        val settings: ModuleSettings = ModuleSettings.getInstance()
        settings.templates = items
    }

    override fun reset() {
        val items = mutableListOf<ModuleTemplate>()
        ModuleSettings.getInstance().templates.forEach { moduleTemplate ->
            items.add(moduleTemplate)
        }
        mySettingsComponent.setTemplates(items)
    }
}