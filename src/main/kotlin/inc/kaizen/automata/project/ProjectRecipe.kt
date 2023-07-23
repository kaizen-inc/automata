package inc.kaizen.automata.project

import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.PackageName
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.android.tools.idea.wizard.template.impl.activities.common.addAllKotlinDependencies
import com.android.tools.idea.wizard.template.impl.activities.common.addMaterial3Dependency

const val COMPOSE_BOM_VERSION = "2022.10.00"
const val COMPOSE_KOTLIN_COMPILER_VERSION = "1.3.2"

fun RecipeExecutor.customModuleSetup(
    moduleData: ModuleTemplateData
) {
    // com.android.tools.idea.wizard.template.RecipeExecutor.addIncludeToSettings
    addIncludeToSettings(moduleData.name)

    // com.android.tools.idea.wizard.template.RecipeExecutor.save
//    save(createGradleFileContent(), moduleData.rootDir.resolve("build.gradle"))

    // com.android.tools.idea.wizard.template.RecipeExecutor.applyPlugin
//    applyPlugin("com.android.library")
//    applyPlugin("com.example.myplugin")
//    addAllKotlinDependencies(moduleData)
}


fun RecipeExecutor.projectRecipe(
    moduleData: ModuleTemplateData,
    packageName: PackageName,
    activityName: String,
    canAddComposeDependencies: Boolean
) {
    addAllKotlinDependencies(moduleData)
    addMaterial3Dependency()

    if (canAddComposeDependencies) {
        addDependency(mavenCoordinate = "androidx.activity:activity-compose:1.5.1")

        // Add Compose dependencies, using the BOM to set versions
        addPlatformDependency(mavenCoordinate = "androidx.compose:compose-bom:$COMPOSE_BOM_VERSION")
        addPlatformDependency(mavenCoordinate = "androidx.compose:compose-bom:$COMPOSE_BOM_VERSION", "androidTestImplementation")

        addDependency(mavenCoordinate = "androidx.compose.ui:ui")
        addDependency(mavenCoordinate = "androidx.compose.ui:ui-graphics")
        addDependency(mavenCoordinate = "androidx.compose.ui:ui-tooling", configuration = "debugImplementation")
        addDependency(mavenCoordinate = "androidx.compose.ui:ui-tooling-preview")
        addDependency(mavenCoordinate = "androidx.compose.ui:ui-test-manifest", configuration="debugImplementation")
        addDependency(mavenCoordinate = "androidx.compose.ui:ui-test-junit4", configuration="androidTestImplementation")
        addDependency(mavenCoordinate = "androidx.compose.material3:material3")

        requireJavaVersion("1.8", true)
        setBuildFeature("compose", true)
        // Note: kotlinCompilerVersion default is declared in TaskManager.COMPOSE_KOTLIN_COMPILER_VERSION
        setComposeOptions(kotlinCompilerExtensionVersion = COMPOSE_KOTLIN_COMPILER_VERSION)
    }

    val emptyActivity = emptyActivity(packageName, activityName)
    val emptyActivityPath = moduleData.srcDir.resolve(packageName).resolve("$activityName.kt")
    save(emptyActivity, emptyActivityPath)
    open(emptyActivityPath)
}