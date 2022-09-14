pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.gradle.sample") {
                useModule("com.gradle:sample-plugins:1.0.0")
            }
            // springboot
            if (requested.id.id.startsWith("org.springframework.boot")) {
                useVersion("2.7.1")
            }
            if (requested.id.id.startsWith("io.spring.dependency-management")) {
                useVersion("1.0.12.RELEASE")
            }
            // kotlin
            if (requested.id.id.startsWith("org.jetbrains.kotlin.")) {
                useVersion("1.6.21")
            }
        }
    }
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

/*
 * This file was generated by the Gradle 'init' task.
 *
 * The settings file is used to specify which projects to include in your build.
 *
 * Detailed information about configuring a multi-project build in Gradle can be found
 * in the user manual at https://docs.gradle.org/7.2/userguide/multi_project_builds.html
 */

rootProject.name = "jacoco-aggregate-reports"
include("http", "list", "utilities")