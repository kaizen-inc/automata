package inc.kaizen.automata.module.ide

import android.databinding.tool.ext.toCamelCase
import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.android.tools.idea.wizard.template.impl.activities.common.addAllKotlinDependencies
import com.android.tools.idea.wizard.template.impl.activities.common.addMaterial3Dependency
import inc.kaizen.automata.module.extension.renderTemplate
import java.util.*
import kotlin.io.path.relativeTo

private const val COMPOSE_BOM_VERSION = "2022.10.00"
private const val COMPOSE_KOTLIN_COMPILER_VERSION = "1.3.2"

fun RecipeExecutor.moduleRecipe(
    moduleData: ModuleTemplateData,
    variables: Map<String, Variable> = mapOf()
) {
    addIncludeToSettings(moduleData.name)

    save("", moduleData.rootDir.resolve("build.gradle"))
    addAllKotlinDependencies(moduleData)
    addMaterial3Dependency()

    val canAddComposeDependencies = true
    if (canAddComposeDependencies) {
        val projectData = moduleData.projectTemplateData
        applyPlugin("com.android.library", projectData.gradlePluginVersion)
        applyPlugin("org.jetbrains.kotlin.android", projectData.gradlePluginVersion)
        addDependency(mavenCoordinate = "androidx.activity:activity-compose:1.5.1")

        // Add Compose dependencies, using the BOM to set versions
        addPlatformDependency(mavenCoordinate = "androidx.compose:compose-bom:${COMPOSE_BOM_VERSION}")
        addPlatformDependency(mavenCoordinate = "androidx.compose:compose-bom:${COMPOSE_BOM_VERSION}", "androidTestImplementation")

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


    // viewmodel package
//    var content = viewModel(packageName, featureName, isPaginationRequired)
//    createSource(moduleData, packageName, "${feature}ViewModel.kt", content)
//
//    content = screen(packageName, featureName)
//    createSource(moduleData, packageName, "${feature}Screen.kt", content)
//
//    content = navigationExtension(packageName, featureName)
//    createSource(moduleData, packageName,"${feature}NavigationExtension.kt", content)
//
//    content = destination(packageName, featureName)
//    createSource(moduleData, packageName,"${feature}Destination.kt", content)
//
//    content = component(packageName, featureName)
//    createSource(moduleData, packageName,"${feature}Component.kt", content)
//
//    content = dependencyInjectionModule(packageName, featureName, isPaginationRequired)
//    createSource(moduleData, packageName,"${feature}Module.kt", content)
//
//    content = dataStoreInterface(packageName, featureName)
//    createSource(moduleData, packageName,"I${feature}DataStore.kt", content)
//
//    content = repositoryInterface(packageName, featureName)
//    createSource(moduleData, packageName,"I${feature}Repository.kt", content)
//
//    content = repositoryImplementation(packageName, featureName)
//    createSource(moduleData, packageName,"${feature}Repository.kt", content)
//
//    content = remoteClient(packageName, featureName)
//    createSource(moduleData, packageName,"${feature}RemoteClient.kt", content)
//
//    content = localStore(packageName, featureName)
//    createSource(moduleData, packageName,"I${feature}Store.kt", content)
//
//    content = entity(packageName, featureName)
//    createSource(moduleData, packageName,"${feature}Entity.kt", content)
//
//    content = database(databaseFileName, packageName, featureName)
//    createSource(moduleData, packageName,"${feature}Database.mustache", content)
//
//    content = dao(packageName, featureName)
//    createSource(moduleData, packageName,"${feature}Dao.kt", content)
//
//    converter(packageName, featureName) { completePackageName, fileName, fileContent ->
//        save(fileContent, moduleData.srcDir.resolve(completePackageName).resolve(fileName))
//    }
//
//    localClient(packageName, featureName) { completePackageName, fileName, fileContent ->
//        save(fileContent, moduleData.srcDir.resolve(completePackageName).resolve(fileName))
//    }

    val scopes = mutableMapOf<String, Any>()

    variables.forEach { (_, variable) ->
        scopes[variable.name] = variable.value
    }

    val feature = variables["featureName"]?.value?.toCamelCase()
    val packageName = variables["packageName"]?.value?.toCamelCase()

    val templateRenderer = TemplateRenderer()
    val templatePath = templateRenderer.templatePath()
    val paths = templateRenderer.renderContentFromTemplate()
    if (templatePath != null) {
        paths?.forEach { path ->
            val relativeFilePath = path
                .parent
                .relativeTo(templatePath)
                .toString()
                .replace(".mustache", "")
            val packageExtension = relativeFilePath
                .replace("\\", ".")
                .lowercase(Locale.getDefault())
            val completePackageName = "$packageName.$packageExtension"
            val className = path.toFile().nameWithoutExtension
            scopes["completePackageName"] = completePackageName
            scopes["className"] = className
            val classContent = path.renderTemplate(scopes)
            save(classContent, moduleData.srcDir.resolve(relativeFilePath).resolve("${feature + className}.kt"))
        }
    }
}

private fun RecipeExecutor.createSource(
    moduleData: ModuleTemplateData,
    packageName: String,
    fileName: String,
    content: String,
) {
    save(content, moduleData.srcDir.resolve(packageName).resolve(fileName))
}