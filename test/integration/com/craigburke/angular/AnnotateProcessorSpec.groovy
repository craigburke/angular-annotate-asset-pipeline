package com.craigburke.angular

import grails.plugin.spock.IntegrationSpec
import asset.pipeline.JsAssetFile
import asset.pipeline.processors.UglifyJsProcessor

class AnnotateProcessorSpec extends IntegrationSpec {

    def "Annotate and minifiy JS file with controller that has dependencies"() {
        given:
        def annotateProcessor = new AnnotateProcessor()
        def minifyJsProcessor = new UglifyJsProcessor()
        def expectedAnnotations = ['$scope', '$http', 'foo', 'bar']

        String input = """\
        angular.module('foo', []).controller('FooController', function(${expectedAnnotations.join(',')}) {});
        """

        when:
        String annotatedJs = annotateProcessor.process(input, new JsAssetFile())
        String minifiedJs = minifyJsProcessor.process(annotatedJs, [strictSemicolons: false, mangleOptions:[mangle: true]])

        then:
        annotatedJs.size() > input.size()

        expectedAnnotations.each { String annotation ->
            assert minifiedJs.contains(annotation)
        }

    }

    def "Annotate Invalid Js file"() {
        given:
        def annotateProcessor = new AnnotateProcessor()
        String input = "var nonTerminatingString = 'Uh oh"

        when:
        annoteProcessor.process(input, new JsAssetFile())

        then:
        thrown(Exception)

    }

}
