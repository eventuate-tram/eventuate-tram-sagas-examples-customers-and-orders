import org.gradle.api.Plugin
import org.gradle.api.Project

class ServicePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.apply(plugin: 'org.springframework.boot')
        project.apply(plugin: ComponentTestsPlugin)

        project.dependencies {

            implementation "org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j"
            implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui'

            runtimeOnly "io.eventuate.tram.springwolf:eventuate-tram-springwolf-support-starter"
            runtimeOnly "io.github.springwolf:springwolf-ui:${project.ext.springwolfVersion}"

            componentTestRuntimeOnly "io.eventuate.tram.springwolf:eventuate-tram-springwolf-support-starter"
            componentTestImplementation "io.eventuate.tram.springwolf:eventuate-tram-springwolf-support-testing"
            componentTestRuntimeOnly "io.github.springwolf:springwolf-ui:${project.ext.springwolfVersion}"

        }

    }
}
