package inc.kaizen.automata.module.settings

import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.tools.ToolsBundle
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.ListTableModel
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.ListSelectionModel

class ModuleSettingsComponent {

    private var parent: JPanel
    private var tableModel: ListTableModel<ModuleTemplate> = ListTableModel<ModuleTemplate>(
        object : ColumnInfo<ModuleTemplate?, String?>("Name") {
            override fun valueOf(o: ModuleTemplate?): String? {
                return o?.name
            }
        },
        object : ColumnInfo<ModuleTemplate?, String?>("Template Path") {
            override fun valueOf(o: ModuleTemplate?): String? {
                return o?.templatePath
            }
        })

    private val templatesTable: JBTable = JBTable(tableModel)

    init {
         val items = mutableListOf<ModuleTemplate>()
        ModuleSettings.getInstance().templates.forEach { moduleTemplate ->
            items.add(moduleTemplate)
        }

        tableModel.items = items
        templatesTable.selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
        templatesTable.setColumnSelectionAllowed(false)
        templatesTable.rowHeight = 22
        templatesTable.preferredScrollableViewportSize = Dimension(
            -1,
            templatesTable.rowHeight * tableModel.items.size / 2
        )
        templatesTable.isStriped = true
        templatesTable.setShowGrid(false)
        templatesTable.setBorder(JBUI.Borders.empty())
        templatesTable.setDragEnabled(false)
        templatesTable.columnModel.getColumn(0).setPreferredWidth(150)
        templatesTable.columnModel.getColumn(1).setPreferredWidth(450)


        val toolbarDecorator: ToolbarDecorator = ToolbarDecorator.createDecorator(templatesTable)

        toolbarDecorator.setAddAction {
            val dialog = CreateModuleTemplateDialog(tableModel.items)
            dialog.setData(ModuleTemplate())
            if (dialog.showAndGet()) {
                tableModel.addRow(dialog.getData())
            }

            IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown {
                IdeFocusManager.getGlobalInstance().requestFocus(templatesTable, true)
            }
        }

        toolbarDecorator.setRemoveAction { actionButton ->
            val selectedIndices: IntArray? = this.templatesTable.selectionModel.selectedIndices
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

        toolbarDecorator.setEditAction {
            val selectedIndices: IntArray? = this.templatesTable.selectionModel.selectedIndices
            if (selectedIndices?.size == 1) {
                val template = tableModel.getItem(this.templatesTable.selectionModel.selectedIndices[0])
                val dialog = CreateModuleTemplateDialog(tableModel.items)
                dialog.setData(template)
                if (dialog.showAndGet()) {
                    dialog.getData()
                    tableModel.fireTableRowsUpdated(selectedIndices[0], selectedIndices[0])
                }

                IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown {
                    IdeFocusManager.getGlobalInstance().requestFocus(templatesTable, true)
                }
            }
        }

        val scrollPane = ScrollPaneFactory.createScrollPane(toolbarDecorator.createPanel())

        parent = FormBuilder.createFormBuilder()
            .addComponent(scrollPane)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    fun getPanel(): JPanel {
        return parent
    }

    fun getPreferredFocusedComponent(): JComponent {
        return templatesTable
    }

    fun getTemplates(): MutableList<ModuleTemplate> {
        return tableModel.items
    }

    fun setTemplates(moduleTemplate: List<ModuleTemplate>): Boolean {
        tableModel.items = moduleTemplate
        return true
    }
}