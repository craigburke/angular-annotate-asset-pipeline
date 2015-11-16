grails.project.work.dir = 'target'

grails.project.fork = false

grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {

    inherits 'global'
    log 'warn'

    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }
    dependencies {
		compile "com.craigburke.angular:angular-annotate-asset-pipeline:2.4.0"
    }
    plugins {

        build(":release:3.0.1",
              ":rest-client-builder:1.0.3") {
            export = false
        }
    }
}
