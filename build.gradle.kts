plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.21"
}

group = "inc.kaizen"
version = "1.0"

repositories {
    mavenCentral()
}

subprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
    }

    group = "inc.kaizen"
    version = "1.0"

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        testImplementation("org.jetbrains.kotlin:kotlin-test")
    }

    tasks.test {
        useJUnitPlatform()
    }
    kotlin {
        jvmToolchain(17)
    }
}

tasks.register<Jar>("uberJar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    subprojects.forEach { subproject ->
        dependsOn(subproject.configurations.runtimeClasspath)
    }

    subprojects.forEach { subproject ->
        from(subproject.sourceSets.main.get().output)
        from({
            subproject.configurations.runtimeClasspath.get().filter {
                it.name.endsWith("jar")
                        && !it.name.contains("jetbrains", true)
                        && !it.name.contains("kotlin", true)
            }.map {
                zipTree(it)
            }
        })
    }
}