package io.github.surpsg.deltacoverage.gradle.task

import io.github.surpsg.deltacoverage.gradle.DeltaCoverageConfiguration
import io.github.surpsg.deltacoverage.gradle.unittest.applyDeltaCoveragePlugin
import io.github.surpsg.deltacoverage.gradle.unittest.testJavaProject
import io.kotest.matchers.file.shouldExist
import org.gradle.api.internal.project.ProjectInternal
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class DeltaCoverageTaskTest {

    @Suppress("VarCouldBeVal")
    @TempDir
    private lateinit var tempDir: File

    @Test
    fun `should create summary report`() {
        // GIVEN
        val project: ProjectInternal = testJavaProject {
            applyDeltaCoveragePlugin()

            val diffFile = tempDir.resolve("111").apply {
                createNewFile()
            }

            extensions.configure(DeltaCoverageConfiguration::class.java) {
                with(it) {
                    diffSource { source ->
                        source.file.set(diffFile.absolutePath)
                    }
                    reportViews.getByName("test").coverageBinaryFiles = files("any")
                }
            }
        }

        val deltaTask: DeltaCoverageTask = project.tasks.withType(DeltaCoverageTask::class.java).first()

        // WHEN
        deltaTask.executeAction()

        // THEN
        val summaryFile = project.layout.buildDirectory.file("reports/coverage-reports/aggregated-summary.json")
            .get().asFile
        summaryFile.shouldExist()
    }
}
