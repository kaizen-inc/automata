package inc.kaizen.automata.ide.descriptionProvider

import com.android.tools.idea.npw.module.ModuleDescriptionProvider
import com.android.tools.idea.npw.module.ModuleGalleryEntry
import com.intellij.openapi.project.Project
import inc.kaizen.automata.ide.galleryEntry.MobileModuleTemplateGalleryEntry

class AutomataModuleDescriptionProvider : ModuleDescriptionProvider {
    override fun getDescriptions(project: Project): Collection<ModuleGalleryEntry> {
        return listOf(MobileModuleTemplateGalleryEntry())
    }
}