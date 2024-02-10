package inc.kaizen.automata.module.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil
import inc.kaizen.automata.module.extension.copyTo
import inc.kaizen.automata.module.extension.toPath
import java.nio.file.Path

@State(
    name = "ModuleSettings",
    storages = [Storage("moduleTemplates.xml")],
    additionalExportDirectory = "templates",
    category = SettingsCategory.OTHER
)
class ModuleSettings: PersistentStateComponent<ModuleSettings> {

    private val templatePath: Path = System.getProperty("user.home")
        .toPath()
        .resolve("templates")
        .resolve("ClientModule")

    private val defaultModuleTemplate: ModuleTemplate = ModuleTemplate("Client Module", templatePath.normalize().toString())

    var templates: MutableList<ModuleTemplate> = mutableListOf()

    init {
        "template.zip".copyTo(templatePath)
    }
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