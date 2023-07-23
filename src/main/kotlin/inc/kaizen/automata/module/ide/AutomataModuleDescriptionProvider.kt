package inc.kaizen.automata.module.ide

import com.android.tools.idea.npw.model.ProjectSyncInvoker
import com.android.tools.idea.npw.module.ModuleDescriptionProvider
import com.android.tools.idea.npw.module.ModuleGalleryEntry
import com.android.tools.idea.wizard.model.SkippableWizardStep
import com.intellij.openapi.project.Project
import icons.StudioIcons
import javax.swing.Icon

private class MobileModuleTemplateGalleryEntry(
    override val description: String? = "",
    override val icon: Icon? = StudioIcons.Wizards.Modules.PHONE_TABLET,
    override val name: String = "Feature Module with code"
) : ModuleGalleryEntry {
    override fun createStep(
        project: Project,
        moduleParent: String,
        projectSyncInvoker: ProjectSyncInvoker
    ): SkippableWizardStep<*> {
        val model = CustomAndroidModuleModel.fromExistingProject(project, moduleParent, projectSyncInvoker, true)
//        return ConfigureAndroidModuleStep(model, 21, "basePackage", name)
//        return ConfigureModuleStep<ModuleModel>()
        return CustomConfigureModuleStep(model, 21, model.packageName.get(), name)
//        return CustomConfigureModuleStep(model, model.androidSdkInfo.value.minApiLevel, model.packageName.get(), name)
    }
}

class AutomataModuleDescriptionProvider : ModuleDescriptionProvider {
    override fun getDescriptions(project: Project): Collection<ModuleGalleryEntry> {
        return listOf(MobileModuleTemplateGalleryEntry())
    }
}