package inc.kaizen.automata.ide.step

import com.android.tools.adtui.device.FormFactor
import com.android.tools.idea.npw.module.ConfigureModuleStep
import com.android.tools.idea.observable.ui.TextProperty
import com.android.tools.idea.wizard.model.ModelWizardStep
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI.Borders.empty
import inc.kaizen.automata.ide.model.AutomataModuleModel
import inc.kaizen.automata.core.model.Variable
import javax.swing.JPanel

class AutomataConfigureModuleStep(
    model: AutomataModuleModel,
    minSdkLevel: Int,
    basePackage: String?,
    title: String
): ConfigureModuleStep<AutomataModuleModel>(
    model,
    FormFactor.MOBILE,
    minSdkLevel,
    basePackage,
    title
) {

    init {
        bindings.bind(model.moduleName, TextProperty(moduleName))
        bindings.bind(model.packageName, TextProperty(packageName))
    }

    override fun createMainPanel(): JPanel = panel{
//        row(contextLabel("Module name", message("android.wizard.module.help.name"))) {
        row("Module name") {
            cell(moduleName).comment("Add module name").align(AlignX.FILL)
        }

        row("Package name") {
            cell(packageName).align(AlignX.FILL)
        }

        row("Language") {
            cell(languageCombo).align(AlignX.FILL)
        }

//        if (model.isLibrary) {
//            row("Bytecode Level") {
//                cell(library).align(AlignX.FILL)
//            }
//        }

        row("Minimum SDK") {
            cell(apiLevelCombo).align(AlignX.FILL)
        }
    }.withBorder(empty(6))


    override fun createDependentSteps(): Collection<ModelWizardStep<*>> {
//        val minSdkLevel = model.androidSdkInfo.value.minApiLevel
        return listOf(AutomataTemplateConfigureModuleStep(model, 21, model.packageName.get(), "Configure Parameter"))
    }

    override fun onProceeding() {
        model.variables["featureName"] = Variable("featureName", model.moduleName.get())
        model.variables["packageName"] = Variable("packageName", model.packageName.get())
        super.onProceeding()
    }
}