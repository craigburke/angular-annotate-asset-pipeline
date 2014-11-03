package com.craigburke.angular

import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable
import org.springframework.core.io.ClassPathResource
import asset.pipeline.AssetCompiler

class AnnotateProcessor {

    Scriptable globalScope
    ClassLoader classLoader

    AnnotateProcessor(AssetCompiler precompiler) {
    try {
        classLoader = getClass().getClassLoader()
        def annotateResource = new ClassPathResource('com/craigburke/angular/ngannotate.js', classLoader)

        Context rhinoContext = Context.enter()
        globalScope = rhinoContext.initStandardObjects()
        rhinoContext.evaluateReader(globalScope, new InputStreamReader(annotateResource.inputStream, 'UTF-8'), annotateResource.filename, 0, null)
    } catch (Exception e) {
        throw new Exception("Annotation initialization failed.", e)
    } finally {
        try {
            Context.exit()
        } catch (IllegalStateException e) {}
    }
    }

    def process(input, assetFile) {
        try {
            def context = Context.enter()
            def annotateScope = context.newObject(globalScope)

            annotateScope.setParentScope(globalScope)
            annotateScope.put("inputSrc", annotateScope, input)

            def result = context.evaluateString(annotateScope, "ngAnnotate(inputSrc, {add: true})", "AngularJS annotate command", 0, null)

            if (result.containsKey('errors')) {
                throw new Exception(result.errors.toString())
            }
            else {
                return result.src
            }

        } catch (Exception e) {
            throw new Exception("AngularJS annotate failed", e)
        } finally {
            Context.exit()
        }

    }
}