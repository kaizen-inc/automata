plugins {
    id("org.jetbrains.intellij") version "1.13.3"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation("org.jetbrains:marketplace-zip-signer:0.1.8")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    type.set("AI") // Target IDE Platform
    version.set("2023.2.1.21") // https://plugins.jetbrains.com/docs/intellij/android-studio-releases-list.html
    plugins.set(listOf(
        "org.jetbrains.android"
    ))
}

tasks.buildSearchableOptions {
    isEnabled = false
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set("242.*")
    }

    publishPlugin {
        val publishToken = if(project.findProperty("PUBLISH_TOKEN") != null)
            project.findProperty("PUBLISH_TOKEN") .toString()
        else
            System.getenv("PUBLISH_TOKEN")
        token.set(publishToken)
    }
}