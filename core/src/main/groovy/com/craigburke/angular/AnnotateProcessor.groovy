package com.craigburke.angular

import asset.pipeline.AssetFile
import asset.pipeline.AssetCompiler
import com.craigburke.asset.JavaScriptEngine
import com.craigburke.asset.JavaScriptProcessor
import groovy.transform.CompileStatic
import groovy.transform.Synchronized

class AnnotateProcessor extends JavaScriptProcessor {

    AnnotateProcessor(AssetCompiler precompiler) {
        super(precompiler)
    }

    static JavaScriptEngine annotateEngine

    @Synchronized
    JavaScriptEngine getEngine() {
        annotateEngine = annotateEngine ?: new JavaScriptEngine('ngannotate.js')
        annotateEngine
    }

    String process(String input, AssetFile assetFile) {
        javaScript {
            inputSrc = input
            Map result = eval('ngAnnotate(inputSrc, {add: true, sourcemap: false, stats: false});')
            if (result.containsKey('errors')) {
                throw new Exception(result.errors as String)
            }
            result.src
        }
    }

}
