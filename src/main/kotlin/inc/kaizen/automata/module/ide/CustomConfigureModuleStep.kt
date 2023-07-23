package inc.kaizen.automata.module.ide

import com.android.tools.adtui.device.FormFactor
import com.android.tools.idea.npw.model.NewAndroidModuleModel
import com.android.tools.idea.npw.module.ConfigureModuleStep
import com.android.tools.idea.npw.toWizardFormFactor
import com.android.tools.idea.wizard.model.ModelWizardStep
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.text
import com.intellij.util.ui.JBUI.Borders.empty
import javax.swing.JPanel

class CustomConfigureModuleStep(
    model: CustomAndroidModuleModel,
    minSdkLevel: Int,
    basePackage: String?,
    title: String
): ConfigureModuleStep<CustomAndroidModuleModel>(
    model,
    FormFactor.MOBILE,
    minSdkLevel,
    basePackage,
    title
) {

//    init {
//        this.minSdkLevel = minSdkLevel
//        this.basePackage = basePackage
//    }

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
//                cell(bytecodeCombo).align(AlignX.FILL)
//            }
//        }

        row("Minimum SDK") {
            cell(apiLevelCombo).align(AlignX.FILL)
        }
    }.withBorder(empty(6))


    override fun createDependentSteps(): Collection<ModelWizardStep<*>> {
//        val minSdkLevel = model.androidSdkInfo.value.minApiLevel
        return listOf(CustomParameterConfigureModuleStep(model, 21, model.packageName.get(), "Configure Parameter"))
    }
}