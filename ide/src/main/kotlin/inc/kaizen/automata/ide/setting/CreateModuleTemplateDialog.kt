package inc.kaizen.automata.ide.setting

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.components.fields.ExtendableTextField
import inc.kaizen.automate.core.template.ModuleTemplate
import org.apache.commons.lang.StringUtils
import java.awt.GridLayout
import java.io.File
import javax.swing.*

class CreateModuleTemplateDialog(private val availableTemplates: List<ModuleTemplate>) : DialogWrapper(true) {

    private var template: ModuleTemplate? = null

    private lateinit var nameField: JTextField
    private lateinit var pathField: ExtendableTextField

    init {
        title = "Add new module template"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel(GridLayout(2, 2))
        var label = JLabel("Template Name:")
        dialogPanel.add(label)

        nameField = JTextField(template?.name)
        dialogPanel.add(nameField)

        label = JLabel("Template Folder:")
        dialogPanel.add(label)

        pathField = ExtendableTextField(template?.templatePath)
        val textFieldWithBrowseButton = TextFieldWithBrowseButton(pathField)
        dialogPanel.add(textFieldWithBrowseButton)
        addTemplatePathBrowseAction(textFieldWithBrowseButton)

        return dialogPanel
    }

    private fun addTemplatePathBrowseAction(programField: TextFieldWithBrowseButton) {
        programField.addBrowseFolderListener(object : TextBrowseFolderListener(
            FileChooserDescriptorFactory.createSingleFolderDescriptor()) {
        })
    }

    fun setData(template: ModuleTemplate) {
        this.template = template
        nameField.text = template.name
        pathField.text = template.templatePath
    }

    fun getData(): ModuleTemplate? {
        template?.name = nameField.text
        template?.templatePath = pathField.text
        return template
    }

    override fun doValidate(): ValidationInfo? {
        val name: String = nameField.text
        val filePath: String = pathField.text
        if(StringUtils.isBlank(name) && StringUtils.isBlank(filePath))
            return super.doValidate()

        if (StringUtils.isBlank(name)) {
            return ValidationInfo("Provide a valid and unique name", nameField)
        }
        if (availableTemplates.any { it.name == name }) {
            return ValidationInfo("$name is already configured", nameField)
        }

        val file = File(filePath)
        return if (!file.exists()) {
            ValidationInfo("Selected folder does not exist", pathField)
        } else if(availableTemplates.any { it.templatePath == filePath }) {
            ValidationInfo("Configured folder is already present in another template", pathField)
        } else if(!file.walk().any { it.extension == "mustache" }) {
            ValidationInfo("Please select folder containing at-least one mustache file", pathField)
        } else {
            super.doValidate()
        }
    }

    override fun postponeValidation(): Boolean {
        return false
    }
}
