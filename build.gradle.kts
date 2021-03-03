// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id(BuildPlugins.ktlintPlugin)
    id(BuildPlugins.dektPlugin)
    id(BuildPlugins.gradleVersionsPlugin)
    id(BuildPlugins.dynamicFeature) apply false
    id(BuildPlugins.androidLibrary) apply false
    id(BuildPlugins.androidApplication) apply false
    id(BuildPlugins.kotlinAndroid) apply false
    id(BuildPlugins.kotlinAndroidExtensions) apply false
    id(BuildPlugins.safeArgs) apply false
    id(BuildPlugins.firebasePlugin) apply false
    id(BuildPlugins.googleServices) apply false
}

allprojects {
    repositories {
        google()
        jcenter()
        maven(url = "https://jitpack.io")
    }
    apply(plugin = BuildPlugins.ktlintPlugin)
    ktlint {
        android.set(true)
        verbose.set(true)
        filter {
            exclude { element -> element.file.path.contains("generated/") }
        }
    }
}
subprojects {
    apply(plugin = BuildPlugins.dektPlugin)
    detekt {
        config = files("${project.rootDir}/detekt.yml")
        parallel = true
    }
}

tasks.register("clean").configure {
    delete("build")
}

tasks.withType<Test> {
    testLogging {
        events = setOf(
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT
        )
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true

        debug {
            events = setOf(
                org.gradle.api.tasks.testing.logging.TestLogEvent.STARTED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR,
                org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT
            )
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
        info.events = debug.events
        info.exceptionFormat = debug.exceptionFormat

        addTestListener(
            object : TestListener {
                override fun beforeTest(p0: TestDescriptor?) = Unit
                override fun beforeSuite(p0: TestDescriptor?) = Unit
                override fun afterTest(desc: TestDescriptor, result: TestResult) = Unit
                override fun afterSuite(desc: TestDescriptor, result: TestResult) {
                    printResults(desc, result)
                }
            }
        )
    }
}

fun printResults(desc: TestDescriptor, result: TestResult) {
    if (desc.parent != null) {
        val output = result.run {
            "Results: $resultType (" +
                "$testCount tests, " +
                "$successfulTestCount successes, " +
                "$failedTestCount failures, " +
                "$skippedTestCount skipped" +
                ")"
        }
        val testResultLine = "|  $output  |"
        val repeatLength = testResultLine.length
        val separationLine = "-".repeat(repeatLength)
        println(separationLine)
        println(testResultLine)
        println(separationLine)
    }
}