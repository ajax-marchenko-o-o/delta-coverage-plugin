import io.gradle.surpsg.deltacoverage.libDeps
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    kotlin("jvm")

    `jvm-test-suite`
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
            dependencies {
                implementation(libDeps.assertj)
                implementation(libDeps.mockk)
                implementation(libDeps.kotestRunnerJunit5)
                implementation(libDeps.kotestAssertions)
                implementation(libDeps.kotestProperty)
            }
            targets.all {
                testTask.configure {
                    outputs.apply {
                        upToDateWhen { false }
                        cacheIf { false }
                    }

                    val disableParallelTests: String? by project
                    val parallelTestsEnabled: Boolean = disableParallelTests == null
                    systemProperty("junit.jupiter.execution.parallel.enabled", parallelTestsEnabled)
                    systemProperty("junit.jupiter.execution.parallel.mode.default", "concurrent")
                    systemProperty("junit.jupiter.execution.parallel.mode.classes.default", "concurrent")
                    systemProperty("junit.jupiter.execution.parallel.config.strategy", "fixed")
                    systemProperty("junit.jupiter.execution.parallel.config.fixed.parallelism", 2)
                    systemProperty("kotest.framework.classpath.scanning.config.disable", "true")

                    testLogging {
                        events(TestLogEvent.SKIPPED, TestLogEvent.FAILED, TestLogEvent.PASSED)
                        showStandardStreams = true
                    }
                }
            }
        }
    }
}
