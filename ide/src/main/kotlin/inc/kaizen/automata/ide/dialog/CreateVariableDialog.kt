package inc.kaizen.automata.ide.dialog

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.components.fields.ExtendableTextField
import inc.kaizen.automata.core.model.Variable
import org.apache.commons.lang.StringUtils
import java.awt.GridLayout
import javax.swing.*

class CreateVariableDialog(private val availableVariables: List<Variable>) : DialogWrapper(true) {

    private var variable: Variable? = null

    private lateinit var nameField: JTextField
    private lateinit var valueField: ExtendableTextField

    init {
        title = "Add/Edit New Variable"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel(GridLayout(2, 2))
        var label = JLabel("Variable Name:")
        dialogPanel.add(label)

        nameField = JTextField(variable?.name)
        nameField.isEditable = false
        dialogPanel.add(nameField)

        label = JLabel("Variable Folder:")
        dialogPanel.add(label)

        valueField = ExtendableTextField(variable?.value)
        dialogPanel.add(valueField)

        return dialogPanel
    }

    fun setData(variable: Variable) {
        this.variable = variable
        nameField.text = variable.name
        valueField.text = variable.value
    }

    fun getData(): Variable? {
        variable?.name = nameField.text
        variable?.value = valueField.text
        return variable
    }

    override fun doValidate(): ValidationInfo? {
        val name: String = nameField.text
        val value: String = valueField.text
        if(name.isBlank() && value.isBlank())
            return super.doValidate()

        if (name.isBlank()) {
            return ValidationInfo("Provide a valid and unique name", nameField)
        }
        if (availableVariables.any { it.name == name }) {
            return ValidationInfo("$name is already configured", nameField)
        }

        if (value.isBlank()) {
            return ValidationInfo("Provide a valid value", valueField)
        }

        return super.doValidate()
    }

    override fun postponeValidation(): Boolean {
        return false
    }
}
