import groovy.util.*
import groovy.xml.*
import groovy.xml.XmlUtil
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.FileInputStream
import java.util.*

configurations.all() {
    exclude("org.springframework.boot", "spring-boot-starter-tomcat")
}

//in root project configuration
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath ("info.solidsoft.gradle.pitest:gradle-pitest-plugin:1.7.4")
    }
}

/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    // Support convention plugins written in Kotlin. Convention plugins are build scripts in 'src/main' that automatically become available as plugins in the main build.
    kotlin("jvm")
    id("info.solidsoft.pitest") version "1.7.4"
    jacoco
}

apply(plugin = "info.solidsoft.pitest.aggregator")
apply(plugin = "java")

group = "aggregate.main"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

allprojects {

    apply(plugin = "java")
    apply(plugin = "jacoco")
    apply(plugin = "info.solidsoft.pitest")

    repositories {
        mavenCentral()
    }

    dependencies {

        implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")

        // Align versions of all Kotlin components
        implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

        // Use JUnit Jupiter for testing.
        testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")

        implementation("org.apache.commons:commons-text:1.9")

        // pitest
        implementation("info.solidsoft.gradle.pitest:gradle-pitest-plugin:1.7.4")

        // logback
        implementation("org.codehaus.janino:janino:3.1.12")
        testImplementation("org.slf4j:slf4j-nop:2.0.16")


    }

    tasks.withType<Test> {

        useJUnitPlatform()

        extensions.configure(JacocoTaskExtension::class) {
            setDestinationFile(layout.buildDirectory.file("jacoco/test.exec").get().asFile)
            classDumpDir = layout.buildDirectory.dir("jacoco/classpathdumps").get().asFile
        }

        finalizedBy( ":jacocoTestReport", ":moveReports", ":pitestReportAggregate")

    }

    tasks.jacocoTestReport {

        additionalSourceDirs.setFrom(files(sourceSets.main.get().allSource.srcDirs))
        sourceDirectories.setFrom(files(sourceSets.main.get().allSource.srcDirs))
        classDirectories.setFrom(files(sourceSets.main.get().output))

        reports {
            html.required.set(true)
            html.outputLocation.set(layout.buildDirectory.dir("/reports/jacoco/html"))
            xml.required.set(true)
            xml.outputLocation.set(file("${buildDir}/reports/jacoco/jacoco.xml"))
            csv.required.set(false)
        }

        dependsOn(tasks.test)
    }

    /*
     * it is an workaround to create aggregated pitest reports
     * Pitest is not able to create the linecoverage.xml and mutations.xml files
     */
    tasks.create<Copy>("moveReports") {

        dependsOn(":utilities:pitest")
        dependsOn(":list:pitest")
        dependsOn("http:pitest")

        val properties =  Properties().apply {
            runCatching { load(FileInputStream("gradle.properties")) }
                .onFailure { // Optional block if file does not exists
                    println("Failed to load properties.")
                    // set default values here
                }
        }

        val targetPitestNewFileDir = properties["targetPitestNewFileDir"]

        val linecoverage = createPitestFile("coverage")
        file("$targetPitestNewFileDir/linecoverage.xml").writeText("$linecoverage")

        val mutations = createPitestFile("mutations")
        file("$targetPitestNewFileDir/mutations.xml").writeText("$mutations")
    }

    pitest {
        targetClasses.add("com.jacoco.aggregate.reports.*")
        targetTests.add(/* element = */ "com.jacoco.aggregate.reports.*Test")
        testSourceSets.add(sourceSets.test)
        mainSourceSets.add(sourceSets.main)
        jvmArgs.addAll("-Xmx1024m", "-Dspring.test.constructor.autowire.mode=all")
        useClasspathFile.set(true)     //useful with bigger projects on Windows
        fileExtensionsToFilter.addAll("xml", "orbit")
        outputFormats.addAll("XML")
        timestampedReports.set(false)
        junit5PluginVersion.set("0.15")
        exportLineCoverage.set(true)

    }

    tasks.withType<KotlinCompile>() {
        kotlinOptions.jvmTarget = "11"
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }
}

jacoco {

    toolVersion = "0.8.7"
}

tasks.test {

    useJUnitPlatform()

    extensions.configure(JacocoTaskExtension::class) {
        setDestinationFile(layout.buildDirectory.file("jacoco/test.exec").get().asFile)
        classDumpDir = layout.buildDirectory.dir("jacoco/classpathdumps").get().asFile
    }

    finalizedBy( ":jacocoRootReport")
}

tasks.create<JacocoReport>("jacocoRootReport") {

    subprojects {
        val subproject = this
        subproject.plugins.withType<JacocoPlugin>().configureEach {
            subproject.tasks.matching { it.extensions.findByType<JacocoTaskExtension>() != null }.configureEach {
                val testTask = this
                sourceSets(this@subprojects.the<SourceSetContainer>().named("main").get())
                executionData(testTask)
            }
            subproject.tasks.matching { it.extensions.findByType<JacocoTaskExtension>() != null }.forEach {
                rootProject.tasks["jacocoTestReport"].dependsOn(it)
            }
        }
    }

    reports {
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("/reports/jacoco/html"))
        xml.required.set(true)
        xml.outputLocation.set(file("${buildDir}/reports/jacoco/jacoco.xml"))
        csv.required.set(false)
    }

    dependsOn(tasks.jacocoTestReport)
}

fun createPitestFile(node: String): String {

    project.file("build/reports/pitest/").mkdirs()
    val xml = Node(null, node, null)
    return XmlUtil.serialize(xml)
}
