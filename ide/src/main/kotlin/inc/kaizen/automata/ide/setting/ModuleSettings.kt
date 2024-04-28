package inc.kaizen.automata.ide.setting

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil
import inc.kaizen.automata.core.extension.copyDefaultTemplates
import inc.kaizen.automata.core.extension.toPath
import inc.kaizen.automata.core.template.ModuleTemplate
import java.nio.file.Path

@State(
    name = "ModuleSettings",
    storages = [Storage("moduleTemplates.xml")],
    additionalExportDirectory = "template",
    category = SettingsCategory.OTHER
)
class ModuleSettings: PersistentStateComponent<ModuleSettings> {

    private val templatePath: Path = System.getProperty("user.home")
        .toPath()
        .resolve("automata")
        .resolve("template")

    var templates: MutableList<ModuleTemplate> = mutableListOf()

    init {
        templatePath.copyDefaultTemplates()

        templatePath
            .toFile()
            .listFiles()?.forEach { folder ->
            val moduleTemplate = ModuleTemplate(folder.name, folder.toPath().normalize().toString())
            if(folder.isDirectory && !templates.contains(moduleTemplate)) {
                templates.add(moduleTemplate)
            }
        }
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
    }
}