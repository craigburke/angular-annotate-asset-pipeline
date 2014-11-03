import asset.pipeline.JsAssetFile
import com.craigburke.angular.AnnotateProcessor
import com.craigburke.angular.AnnotateProcessorUtil

class AngularAnnotateAssetPipelineGrailsPlugin {

    def version = "1.1.3"
    def grailsVersion = "2.0 > *"

    def pluginExcludes = [
            "grails-app/assets/**",
            "grails-app/views/**"
    ]

    def title = "AngularJS Annotate Asset-Pipeline Plugin"
    def author = "Craig Burke"
    def authorEmail = "craig@craigburke.com"
    def description = "Provides AngularJS dependency injection annotation support for the asset-pipeline static asset management plugin."
    def documentation = "http://github.com/craigburke/angular-annotate-asset-pipeline"

    def license = "APACHE"
    def issueManagement = [ system: "GITHUB", url: "http://github.com/craigburke/angular-annotate-asset-pipeline/issues" ]
    def scm = [ url: "http://github.com/craigburke/angular-annotate-asset-pipeline" ]

    def loadAfter = ['coffeeAssetPipeline']

    def doWithDynamicMethods = { ctx ->
        AnnotateProcessorUtil.load()
    }

}
