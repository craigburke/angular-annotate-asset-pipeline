package com.craigburke.angular

import asset.pipeline.AssetFile
import asset.pipeline.GenericAssetFile
import com.craigburke.angular.AnnotateProcessor

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class AnnotateProcessorUnitSpec extends Specification {

    @Shared AssetFile assetFile

    def setup() {
        assetFile = new GenericAssetFile()
    }

    def "Annotate and minifiy JS file with controller that has dependencies"() {
           given:
           def processor = new AnnotateProcessor()
           def dependencies = ['$scope', '$http', 'foo', 'bar']

           String input = """\
       			angular.module('foo', []).controller('FooController', function(${dependencies.join(',')}) {});
           """

           when:
           String annotatedJs = processor.process(input, assetFile)

           then:
           annotatedJs.size() > input.size()
		   
		   and:
           dependencies.each { String dependency ->
               assert annotatedJs.contains(dependency)
           }
       }

       def "Annotate Invalid Js file"() {
           given:
           def processor = new AnnotateProcessor()
           
		   String input = """\
		   		var nonTerminatingString = 'Uh oh
		   """

           when:
		   processor.process(input, assetFile)

           then:
           thrown(Exception)
       }

       def "Simulate parallel annotation with several threads"() {
        given:

            String input = "var test = 'ok';"

        when:
            def results = new String[20]

            def threads = (0..(results.size() - 1)).collect { index ->
                Thread.start {
                    println "Starting thread : " + index
                    try {
                        results[index] = new AnnotateProcessor().process(input, assetFile)
                    } catch (Exception ex) {
                        results[index] = "ERROR:" + ex.message
                    }
                }
            }
            threads*.join()

        then:
            results.findAll { it != input }.isEmpty()

       }

}