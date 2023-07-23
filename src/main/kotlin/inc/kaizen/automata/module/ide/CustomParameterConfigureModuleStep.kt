package inc.kaizen.automata.module.ide

import com.android.tools.idea.npw.module.ConfigureModuleStep
import com.android.tools.idea.npw.toWizardFormFactor
import com.android.tools.idea.observable.core.BoolProperty
import com.android.tools.idea.observable.ui.SelectedProperty
import com.android.tools.idea.observable.ui.TextProperty
import com.android.tools.idea.wizard.model.ModelWizardStep
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI.Borders.empty
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JTextField

class CustomParameterConfigureModuleStep(
    model: CustomAndroidModuleModel,
    minSdkLevel: Int,
    basePackage: String?,
    title: String
): ConfigureModuleStep<CustomAndroidModuleModel>(
    model,
    model.formFactor.get().toWizardFormFactor(),
    minSdkLevel,
    basePackage,
    title
) {

    private val isBaseModule: JCheckBox = JCheckBox()
    private val databaseFileName: JTextField = JTextField()
    private val clientDependency: JTextField = JTextField()
    private val isPaginationRequired: JCheckBox = JCheckBox()

    init {
        val isBaseModule: BoolProperty = SelectedProperty(isBaseModule)
        val databaseFileName = TextProperty(databaseFileName)
        val clientDependency = TextProperty(clientDependency)
        val isPaginationRequired: BoolProperty = SelectedProperty(isPaginationRequired)

        bindings.bind(isBaseModule, model.isBaseModule)
        bindings.bind(model.databaseFileName, databaseFileName, !isBaseModule)
        bindings.bind(model.clientDependency, clientDependency, !isBaseModule)
        bindings.bind(model.isPaginationRequired, isPaginationRequired, !isBaseModule)
    }

    override fun createMainPanel(): JPanel = panel{
//        row(contextLabel("Module name", message("android.wizard.module.help.name"))) {
//        row("Feature name") {
//            cell(JTextField()).align(AlignX.FILL)
//        }

        row("Is base module") {
            cell(isBaseModule).align(AlignX.FILL)
        }

        row("Database File Name") {
            cell(databaseFileName).align(AlignX.FILL)
        }

        row("Client Gradle Dependency") {
            cell(clientDependency).align(AlignX.FILL)
        }

//        if (model.isLibrary) {
//            row("Bytecode Level") {
//                cell(bytecodeCombo).align(AlignX.FILL)
//            }
//        }

        row("Is Pagination required") {
            cell(isPaginationRequired).align(AlignX.FILL)
        }
    }.withBorder(empty(6))


    override fun createDependentSteps(): Collection<ModelWizardStep<*>> {
        return super.createDependentSteps()
    }
}