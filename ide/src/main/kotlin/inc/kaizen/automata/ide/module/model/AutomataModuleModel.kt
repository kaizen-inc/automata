package inc.kaizen.automata.ide.module.model

import com.android.tools.idea.npw.model.ExistingProjectModelData
import com.android.tools.idea.npw.model.ProjectModelData
import com.android.tools.idea.npw.model.ProjectSyncInvoker
import com.android.tools.idea.npw.module.ModuleModel
import com.android.tools.idea.projectsystem.AndroidModulePathsImpl
import com.android.tools.idea.projectsystem.NamedModuleTemplate
import com.android.tools.idea.wizard.template.FormFactor
import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.Recipe
import com.android.tools.idea.wizard.template.TemplateData
import com.google.common.collect.ImmutableList
import com.google.wireless.android.sdk.stats.AndroidStudioEvent
import com.google.wireless.android.sdk.stats.AndroidStudioEvent.TemplatesUsage.TemplateComponent.WizardUiContext.NEW_MODULE
import com.intellij.openapi.project.Project
import inc.kaizen.automata.core.model.Variable
import inc.kaizen.automata.core.template.ModuleTemplate
import inc.kaizen.automata.ide.extension.moduleRecipe2
import java.io.File
import com.google.wireless.android.sdk.stats.AndroidStudioEvent.TemplateRenderer as RenderLoggingEvent

class AutomataModuleModel(
    name: String,
    commandName: String,
    isLibrary: Boolean,
    projectModelData: ProjectModelData,
    template: NamedModuleTemplate,
    moduleParent: String,
    wizardContext: AndroidStudioEvent.TemplatesUsage.TemplateComponent.WizardUiContext,
): ModuleModel(name, commandName, isLibrary, projectModelData, template, moduleParent, wizardContext) {

    init {
        moduleName.set("module")
        packageName.set("basePackage")
    }

    lateinit var selectedTemplate: ModuleTemplate
    val variables: MutableMap<String, Variable> = mutableMapOf()

    fun variableByName(name: String): Variable {
        var variable = variables[name]
        if(variable == null) {
            variable = Variable(name, "")
            variables[name] = variable
        }
        return variable
    }

    override val loggingEvent: AndroidStudioEvent.TemplateRenderer
        get() = formFactor.get().toModuleRenderingLoggingEvent()

    override val renderer: ModuleTemplateRenderer = CustomModuleTemplateRenderer()

    inner class CustomModuleTemplateRenderer : ModuleModel.ModuleTemplateRenderer() {
        override val recipe: Recipe
            get() = { data: TemplateData ->
                moduleRecipe2(
                    moduleData = data as ModuleTemplateData,
                    selectedTemplate,
                    variables
                )
            }
    }

    private fun FormFactor.toModuleRenderingLoggingEvent() = when(this) {
        FormFactor.Mobile -> RenderLoggingEvent.ANDROID_MODULE
        FormFactor.Tv -> RenderLoggingEvent.ANDROID_TV_MODULE
        FormFactor.Automotive -> RenderLoggingEvent.AUTOMOTIVE_MODULE
        FormFactor.Wear -> RenderLoggingEvent.ANDROID_WEAR_MODULE
        FormFactor.Generic -> RenderLoggingEvent.ANDROID_MODULE // TODO(b/145975555)
    }

    companion object {

        private fun createTemplate(moduleRoot: File = File("")): NamedModuleTemplate {
            val baseSrcDir = File(moduleRoot, "src")
            val baseFlavorDir = File(baseSrcDir, "main")
            return NamedModuleTemplate(
                "main", AndroidModulePathsImpl(
                    moduleRoot,
                    baseFlavorDir,
                    File(baseFlavorDir, "java"),
                    File(baseSrcDir.path, "test" + File.separatorChar + "java"),
                    File(baseSrcDir.path, "androidTest" + File.separatorChar + "java"),
                    File(baseFlavorDir, "aidl"),
                    ImmutableList.of(
                        File(baseFlavorDir, "res")
                    ),
                    ImmutableList.of(File(baseFlavorDir, "ml"))
                )
            )
        }

        fun fromExistingProject(
            project: Project,
            moduleParent: String,
            projectSyncInvoker: ProjectSyncInvoker,
            isLibrary: Boolean = false
        ) : AutomataModuleModel = AutomataModuleModel(
            name = "",
            commandName = "New Module",
            projectModelData = ExistingProjectModelData(project, projectSyncInvoker),
            template = createTemplate(),
            moduleParent = moduleParent,
            isLibrary = isLibrary,
            wizardContext = NEW_MODULE
        )
    }
}