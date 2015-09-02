package com.craigburke.angular

import asset.pipeline.AssetFile
import asset.pipeline.AssetCompiler

import com.craigburke.asset.JavaScriptProcessor
import groovy.transform.CompileStatic

class AnnotateProcessor extends JavaScriptProcessor {

    String process(String input, AssetFile assetFile) {
        javaScript('ngannotate.js') {
            Map result = ngAnnotate(input, [add: true, sourcemap: false, stats: false])      
            if (result.containsKey('errors')) {
                throw new Exception(result.errors as String)
            }
            result.src
        }
    }

}
