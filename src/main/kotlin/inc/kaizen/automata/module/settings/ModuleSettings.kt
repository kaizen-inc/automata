package inc.kaizen.automata.module.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil
import inc.kaizen.automata.module.extension.toPath

@State(
    name = "ModuleSettings",
    storages = [Storage("moduleTemplates.xml")],
    additionalExportDirectory = "templates",
    category = SettingsCategory.OTHER
)
class ModuleSettings: PersistentStateComponent<ModuleSettings> {

    private val templatePath: String = System.getProperty("user.home")
        .toPath()
        .resolve("templates")
        .resolve("ClientModule")
        .normalize()
        .toString()

    private val defaultModuleTemplate: ModuleTemplate = ModuleTemplate("Client Module", templatePath)

    var templates: MutableList<ModuleTemplate> = mutableListOf()

    companion object {
        fun getInstance(): ModuleSettings {
            return ApplicationManager.getApplication().getService(ModuleSettings::class.java)
        }
    }

    override fun getState(): ModuleSettings {
        return this
    }

    override fun loadState(state: ModuleSettings) {
        XmlSerializerUtil.copyBean(state, this)
        if(!templates.contains(defaultModuleTemplate)) {
            templates.add(defaultModuleTemplate)
        }
    }
}