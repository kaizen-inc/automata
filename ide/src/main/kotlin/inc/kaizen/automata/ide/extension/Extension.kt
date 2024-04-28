package inc.kaizen.automata.ide.extension

import android.databinding.tool.ext.toCamelCase
import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.android.tools.idea.wizard.template.impl.activities.common.addAllKotlinDependencies
import com.android.tools.idea.wizard.template.impl.activities.common.addMaterial3Dependency
import inc.kaizen.automata.core.model.Variable
import inc.kaizen.automata.core.render.TemplateRenderer
import inc.kaizen.automata.core.template.ModuleTemplate
import java.util.*
import kotlin.io.path.relativeTo

private const val COMPOSE_BOM_VERSION = "2022.10.00"
private const val COMPOSE_KOTLIN_COMPILER_VERSION = "1.3.2"
private const val DAGGER_HILT_VERSION = "1.3.2"

fun RecipeExecutor.moduleRecipe(
    moduleData: ModuleTemplateData,
    template: ModuleTemplate,
    variables: Map<String, Variable> = mapOf()
) {
    addIncludeToSettings(moduleData.name)

    save("", moduleData.rootDir.resolve("build.gradle"))
    addAllKotlinDependencies(moduleData)
    addMaterial3Dependency()

    val canAddComposeDependencies = true
    if (canAddComposeDependencies) {
        val projectData = moduleData.projectTemplateData
        applyPlugin("com.android.library", projectData.agpVersion)
        applyPlugin("org.jetbrains.kotlin.android", projectData.agpVersion)
        applyPlugin("kotlin-kapt", projectData.agpVersion)
        applyPlugin("dagger.hilt.android.plugin", projectData.agpVersion)
        applyPlugin("com.google.devtools.ksp", projectData.agpVersion)
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

        addDependency(mavenCoordinate = "com.google.dagger:hilt-android")
        addDependency(mavenCoordinate = "com.google.dagger:hilt-android-compiler", configuration="kapt")

        addDependency(mavenCoordinate = "androidx.paging:paging-runtime-ktx")
        addDependency(mavenCoordinate = "androidx.paging:paging-compose")

        addDependency(mavenCoordinate = "androidx.room:room-ktx")
        addDependency(mavenCoordinate = "androidx.room:room-compiler", configuration="ksp")
        addDependency(mavenCoordinate = "androidx.room:room-paging")
        addDependency(mavenCoordinate = "androidx.hilt:hilt-navigation-compose:1.0.0")

        addDependency(mavenCoordinate = "com.squareup.retrofit2:retrofit")
        addDependency(mavenCoordinate = "com.squareup.retrofit2:converter-moshi")
        addDependency(mavenCoordinate = "com.squareup.okhttp3:logging-interceptor")

        addDependency(mavenCoordinate = "androidx.lifecycle:lifecycle-runtime-compose")
        addDependency(mavenCoordinate = "androidx.lifecycle:lifecycle-viewmodel-compose")

        requireJavaVersion("1.8", true)
        setBuildFeature("compose", true)
        // Note: kotlinCompilerVersion default is declared in TaskManager.COMPOSE_KOTLIN_COMPILER_VERSION
        setComposeOptions(kotlinCompilerExtensionVersion = COMPOSE_KOTLIN_COMPILER_VERSION)
    }

    val scopes = mutableMapOf<String, Any>()

    variables.forEach { (_, variable) ->
        scopes[variable.name] = variable.value
    }

    val featureName = variables["featureName"]?.value
    val feature = featureName?.toCamelCase()
    val packageName = variables["packageName"]?.value

    val templateRenderer = TemplateRenderer(template)
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
            val completePackageName = "$packageName.$featureName.$packageExtension"
            val className = path.toFile().nameWithoutExtension
            scopes["completePackageName"] = completePackageName
            scopes["className"] = className
            scopes["feature"] = "$feature"
            val classContent = templateRenderer.renderTemplate(path, scopes)
            save(classContent, moduleData.srcDir
                .resolve(featureName!!)
                .resolve(relativeFilePath)
                .resolve(feature + className))
        }
    }
}



fun RecipeExecutor.moduleRecipe2(
    moduleData: ModuleTemplateData,
    template: ModuleTemplate,
    variables: Map<String, Variable> = mapOf()
) {
    val scopes = mutableMapOf<String, Any>()

    variables.forEach { (_, variable) ->
        scopes[variable.name] = variable.value
    }

    val featureName = variables["featureName"]?.value!!
    val feature = featureName.toCamelCase()
    val packageName = variables["packageName"]?.value!!

    val templateRenderer = TemplateRenderer(template)
    val templatePath = templateRenderer.templatePath()
    val paths = templateRenderer.renderContentFromTemplate()
    if (templatePath != null) {
        paths?.forEach { path ->
            val relativeFilePath = path
                .parent
                .relativeTo(templatePath)
                .toString()
                .replace("\\package\\", "\\$packageName\\")
                .replace(".mustache", "")
            if (path.toFile().isFile) {
                val packageExtension = relativeFilePath
                    .replace("\\", ".")
                    .lowercase(Locale.getDefault())
                val completePackageName = "$packageName.$featureName.$packageExtension"
                val fileName = path.toFile().nameWithoutExtension
                scopes["completePackageName"] = completePackageName
                scopes["className"] = fileName
                scopes["feature"] = feature

                val classContent = templateRenderer.renderTemplate(path, scopes)
                val isGradleFile = path.toFile().name.startsWith("build.gradle", false)
                val fileFullName = if(isGradleFile) fileName else feature + fileName
                save(classContent, moduleData.rootDir
                    .resolve(relativeFilePath)
                    .resolve(fileFullName))
            } else {
                createDirectory(moduleData.rootDir.resolve(relativeFilePath))
            }
        }
    }
    addIncludeToSettings(moduleData.name)
}