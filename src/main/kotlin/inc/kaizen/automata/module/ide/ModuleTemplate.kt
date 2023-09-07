package inc.kaizen.automata.module.ide

import com.android.tools.idea.wizard.template.*
import com.android.tools.idea.wizard.template.impl.defaultPackageNameParameter
import inc.kaizen.automata.module.settings.ModuleTemplate
import java.io.File

val moduleTemplate
    get() = template {
        name = "Module Template"
        description = "Custom module Template with feature enabled"
        minApi = 21
        constraints = listOf(
            TemplateConstraint.AndroidX,
            TemplateConstraint.Kotlin
        )
        category = Category.Other
        formFactor = FormFactor.Mobile
        screens = listOf(WizardUiContext.NewModule)

        val featureName = stringParameter {
            name = "Feature Name"
            default = "Feature"
        }

        val databaseFileName = stringParameter {
            name = "Database File Name"
            default = "database"
        }

        val clientGradleDependency = stringParameter {
            name = "Client Gradle Dependency"
            default = ""
        }

        val isBaseModule = booleanParameter {
            name = "Is base module"
            default = false
        }

        val isPaginationRequired = booleanParameter {
            name = "Is Pagination required"
            default = true
        }

        val template: ModuleTemplate? = null

        val packageName = defaultPackageNameParameter

        widgets(
            TextFieldWidget(featureName),
            TextFieldWidget(databaseFileName),
            TextFieldWidget(clientGradleDependency),
            CheckBoxWidget(isBaseModule),
            CheckBoxWidget(isPaginationRequired),
            PackageNameWidget(packageName),
        )

        // I am reusing the thumbnail provided by Android Studio, but
        // replace it with your own
        thumb { File("compose-activity-material3").resolve("template_compose_empty_activity_material3.png") }

        recipe = { data: TemplateData ->
            moduleRecipe(
                moduleData = data as ModuleTemplateData,
            )
        }
    }