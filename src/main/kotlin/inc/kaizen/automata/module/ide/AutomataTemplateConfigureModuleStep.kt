package inc.kaizen.automata.module.ide

import com.android.tools.idea.npw.module.ConfigureModuleStep
import com.android.tools.idea.npw.toWizardFormFactor
import com.android.tools.idea.observable.ui.TextProperty
import com.intellij.openapi.observable.util.whenItemSelected
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.tools.ToolsBundle
import com.intellij.ui.CollectionComboBoxModel
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBUI.Borders.empty
import com.intellij.util.ui.ListTableModel
import inc.kaizen.automata.module.extension.toPath
import inc.kaizen.automata.module.settings.ModuleSettings
import inc.kaizen.automata.module.settings.ModuleTemplate
import java.awt.Dimension
import javax.swing.ComboBoxModel
import javax.swing.JComboBox
import javax.swing.JPanel
import javax.swing.ListSelectionModel
import javax.swing.table.TableCellEditor

class AutomataTemplateConfigureModuleStep(
    model: AutomataModuleModel,
    minSdkLevel: Int,
    basePackage: String?,
    title: String
): ConfigureModuleStep<AutomataModuleModel>(
    model,
    model.formFactor.get().toWizardFormFactor(),
    minSdkLevel,
    basePackage,
    title
) {
    private val comboBoxModel: ComboBoxModel<ModuleTemplate> = CollectionComboBoxModel(ModuleSettings.getInstance().templates)
    private val moduleTemplateSelector: JComboBox<ModuleTemplate> = ComboBox(comboBoxModel)

    private var tableModel: ListTableModel<Variable> = ListTableModel<Variable>(
        object : ColumnInfo<Variable?, String?>("Name") {
            override fun valueOf(o: Variable?): String? {
                return o?.name
            }
        },
        object : ColumnInfo<Variable?, String?>("Value") {
            override fun valueOf(o: Variable?): String? {
                return o?.value
            }

            override fun isCellEditable(item: Variable?): Boolean {
                return true
            }

            override fun setValue(variable: Variable?, value: String?) {
                if(variable != null) {
                    variable.value = value ?: ""
                }
            }
        })

    private val variableTable: JBTable = JBTable(tableModel)

    override fun createMainPanel(): JPanel {
        variableTable.selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
        variableTable.setColumnSelectionAllowed(false)
        variableTable.rowHeight = 22
        variableTable.preferredScrollableViewportSize = Dimension(
            -1,
            variableTable.rowHeight * tableModel.items.size / 2
        )
        variableTable.isStriped = true
        variableTable.setShowGrid(false)
        variableTable.setBorder(empty())
        variableTable.setDragEnabled(false)
        variableTable.columnModel.getColumn(0).setPreferredWidth(150)
        variableTable.columnModel.getColumn(1).setPreferredWidth(450)

        val toolbarDecorator: ToolbarDecorator = ToolbarDecorator.createDecorator(variableTable)

        toolbarDecorator.setAddAction {
            val dialog = CreateVariableDialog(tableModel.items)
            dialog.setData(Variable())
            if (dialog.showAndGet()) {
                tableModel.addRow(dialog.getData())
            }

            IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown {
                IdeFocusManager.getGlobalInstance().requestFocus(variableTable, true)
            }
        }

        toolbarDecorator.setRemoveAction { actionButton ->
            val selectedIndices: IntArray? = this.variableTable.selectionModel.selectedIndices
            if (selectedIndices != null) {
                val result: Int = Messages.showYesNoDialog(
                    ToolsBundle.message("tools.delete.confirmation", *arrayOfNulls(0)),
                    ToolsBundle.message("dialog.title.delete.tool", *arrayOfNulls(0)),
                    Messages.getWarningIcon()
                )
                if (result == 0) {
                    selectedIndices.forEach { index ->
                        tableModel.removeRow(index)
                    }
                }
            }
        }

        toolbarDecorator.setEditAction {  actionButton ->
            val selectedIndices: IntArray? = this.variableTable.selectionModel.selectedIndices
            if (selectedIndices?.size == 1) {
                val template = tableModel.getItem(this.variableTable.selectionModel.selectedIndices[0])
                val dialog = CreateVariableDialog(tableModel.items)
                dialog.setData(template)
                if (dialog.showAndGet()) {
                    dialog.getData()
                    tableModel.fireTableRowsUpdated(selectedIndices[0], selectedIndices[0])
                }

                IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown {
                    IdeFocusManager.getGlobalInstance().requestFocus(variableTable, true)
                }
            }
        }

        moduleTemplateSelector.whenItemSelected { selectedItem ->
            tableModel.items = findVariablesFromTemplate(selectedItem)
        }

        val scrollPane = ScrollPaneFactory.createScrollPane(toolbarDecorator.createPanel())

        return FormBuilder.createFormBuilder()
            .addLabeledComponent("Select template:", moduleTemplateSelector)
            .addComponentFillVertically(scrollPane, 8)
            .panel
    }

    private fun findVariablesFromTemplate(selectedItem: ModuleTemplate?): List<Variable> {
        val regex = "\\{\\{([a-zA-Z]+)\\}\\}".toRegex()

        val variables = mutableSetOf<Variable>()
        selectedItem.let { template ->
            template!!.templatePath
                .toPath()
                .toFile()
                .walk()
                .filter { it.extension == "mustache" }
                .forEach { file ->
                    regex
                        .findAll(file.readText())
                        .map {
                            val name = it.groupValues[1]
                            model.variableByName(name)
                        }
                        .toCollection(variables)
                }
        }
        return variables.toList()
    }

    override fun onEntering() {
        super.onEntering()
        tableModel.items = findVariablesFromTemplate(moduleTemplateSelector.selectedItem as ModuleTemplate?)
    }
}