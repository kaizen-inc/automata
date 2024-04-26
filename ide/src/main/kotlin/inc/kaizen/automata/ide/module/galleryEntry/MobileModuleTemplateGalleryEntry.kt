package inc.kaizen.automata.ide.module.galleryEntry

import com.android.tools.idea.npw.model.ProjectSyncInvoker
import com.android.tools.idea.npw.module.ModuleGalleryEntry
import com.android.tools.idea.wizard.model.SkippableWizardStep
import com.intellij.openapi.project.Project
import icons.StudioIcons
import inc.kaizen.automata.ide.module.model.AutomataModuleModel
import inc.kaizen.automata.ide.module.step.AutomataConfigureModuleStep
import javax.swing.Icon

class MobileModuleTemplateGalleryEntry(
    override val description: String? = "",
    override val icon: Icon? = StudioIcons.Wizards.Modules.PHONE_TABLET,
    override val name: String = "Module with code"
) : ModuleGalleryEntry {
    override fun createStep(
        project: Project,
        moduleParent: String,
        projectSyncInvoker: ProjectSyncInvoker
    ): SkippableWizardStep<*> {
        val model = AutomataModuleModel.fromExistingProject(project, moduleParent, projectSyncInvoker, false)
        return AutomataConfigureModuleStep(model, 21, model.packageName.get(), name)
    }
}