import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

class ComponentTestsPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.sourceSets {
            componentTest {
                java {
                    compileClasspath += main.output + test.output
                    runtimeClasspath += main.output + test.output
                    srcDir project.file('src/componentTest/java')
                }
                resources.srcDir project.file('src/componentTest/resources')
            }
        }

        project.configurations {
            componentTestImplementation.extendsFrom testImplementation
            componentTestRuntime.extendsFrom testRuntime
        }

        project.task("componentTest", type: Test) {
            testClassesDirs = project.sourceSets.componentTest.output.classesDirs
            classpath = project.sourceSets.componentTest.runtimeClasspath
            if (project.tasks.findByName("integrationTest"))
                shouldRunAfter("integrationTest")
            // Ensures that JAR is built prior to building images
            dependsOn("assemble")
        }
        project.tasks.findByName("check").dependsOn(project.tasks.componentTest)


        project.tasks.withType(Test) {
            reports.html.destination = project.file("${project.reporting.baseDir}/${name}")
        }
    }
}
