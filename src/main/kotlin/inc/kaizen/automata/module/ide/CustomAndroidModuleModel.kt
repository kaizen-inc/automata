package inc.kaizen.automata.module.ide

import com.android.tools.idea.gradle.npw.project.GradleAndroidModuleTemplate.createSampleTemplate
import com.android.tools.idea.npw.model.ExistingProjectModelData
import com.android.tools.idea.npw.model.ProjectModelData
import com.android.tools.idea.npw.model.ProjectSyncInvoker
import com.android.tools.idea.npw.module.ModuleModel
import com.android.tools.idea.observable.core.BoolValueProperty
import com.android.tools.idea.observable.core.StringValueProperty
import com.android.tools.idea.projectsystem.NamedModuleTemplate
import com.android.tools.idea.wizard.template.FormFactor
import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.Recipe
import com.android.tools.idea.wizard.template.TemplateData
import com.google.wireless.android.sdk.stats.AndroidStudioEvent
import com.google.wireless.android.sdk.stats.AndroidStudioEvent.TemplatesUsage.TemplateComponent.WizardUiContext.NEW_MODULE
import com.intellij.openapi.project.Project
import com.google.wireless.android.sdk.stats.AndroidStudioEvent.TemplateRenderer as RenderLoggingEvent

class CustomAndroidModuleModel(
    name: String,
    commandName: String,
    isLibrary: Boolean,
    projectModelData: ProjectModelData,
    template: NamedModuleTemplate,
    moduleParent: String,
    wizardContext: AndroidStudioEvent.TemplatesUsage.TemplateComponent.WizardUiContext,
): ModuleModel(name, commandName, isLibrary, projectModelData, template, moduleParent, wizardContext) {

    init {
        packageName.set("basePackage")
    }

    var isBaseModule: BoolValueProperty = BoolValueProperty(false)
    var databaseFileName: StringValueProperty = StringValueProperty("database.db")
    var clientDependency: StringValueProperty = StringValueProperty()
    var isPaginationRequired: BoolValueProperty = BoolValueProperty(true)

    override val loggingEvent: AndroidStudioEvent.TemplateRenderer
        get() = formFactor.get().toModuleRenderingLoggingEvent()

    override val renderer: ModuleTemplateRenderer = CustomModuleTemplateRenderer()

    inner class CustomModuleTemplateRenderer : ModuleModel.ModuleTemplateRenderer() {
        override val recipe: Recipe
            get() = { data: TemplateData ->
                moduleRecipe(
                    moduleData = data as ModuleTemplateData,
                    databaseFileName = databaseFileName.get(),
                    packageName = packageName.get(),
                    featureName = moduleName.get(),
                    isPaginationRequired = isPaginationRequired.get(),
                    isBaseModule = isBaseModule.get()
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

        fun fromExistingProject(
            project: Project,
            moduleParent: String,
            projectSyncInvoker: ProjectSyncInvoker,
            isLibrary: Boolean = false
        ) : CustomAndroidModuleModel = CustomAndroidModuleModel(
            name = "",
            commandName = "New Module",
            projectModelData = ExistingProjectModelData(project, projectSyncInvoker),
            template = createSampleTemplate(),
            moduleParent = moduleParent,
            isLibrary = isLibrary,
            wizardContext = NEW_MODULE
        )
    }
}