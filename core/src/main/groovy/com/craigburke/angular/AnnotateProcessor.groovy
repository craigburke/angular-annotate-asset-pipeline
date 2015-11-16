package com.craigburke.angular

import asset.pipeline.AbstractProcessor
import asset.pipeline.AssetFile
import asset.pipeline.AssetCompiler
import com.craigburke.asset.JavaScriptEngine
import groovy.transform.Synchronized

class AnnotateProcessor extends AbstractProcessor {

    static JavaScriptEngine engine

    AnnotateProcessor(AssetCompiler precompiler) {
        super(precompiler)
        setupEngine()
    }

    @Synchronized
    static void setupEngine() {
        if (!engine) {
            URL ngAnnotate = AnnotateProcessor.classLoader.getResource('ngannotate.js')
            engine = new JavaScriptEngine(ngAnnotate.getText('UTF-8'))
        }
    }

    String process(String input, AssetFile assetFile) {
        engine.run {
            AnnotateOptions options = new AnnotateOptions(add: true, sourcemap: false, stats: false)
            Map result = ngAnnotate(input, options)

            if (result.containsKey('errors')) {
                String message = "Javascript error in ${assetFile.path}\n ${result.errors}"
                throw new Exception(message)
            }
            result.src
        }
    }

}
