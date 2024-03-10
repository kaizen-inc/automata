plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.21"
}

group = "inc.kaizen"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

subprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
    }

    group = "inc.kaizen"
    version = "1.0-SNAPSHOT"

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

