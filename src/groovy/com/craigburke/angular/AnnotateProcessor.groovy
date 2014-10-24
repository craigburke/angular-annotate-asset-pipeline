package com.craigburke.angular

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

import org.springframework.core.io.ClassPathResource
import asset.pipeline.AssetCompiler

class AnnotateProcessor {

	ScriptEngine engine
	boolean useNashorn = true
    def rhinoScope

    AnnotateProcessor(AssetCompiler precompiler) {
        def annotateResource = new ClassPathResource('com/craigburke/angular/ngannotate.js', this.class.classLoader)
		def engineManager = new ScriptEngineManager()
		
		engine = engineManager.getEngineByName("nashorn")
		
		if (engine) {
			println "Using Nashorn"
			engine.eval(annotateResource.inputStream.text)
		}
		else { 
			println "Using Rhino"
			useNashorn = false
			engine = engineManager.getEngineByName("rhino")
			
			def rhinoContext = Class.forName("org.mozilla.javascript.ContextFactory").getGlobal().enterContext()			
	        rhinoScope = rhinoContext.initStandardObjects()
	        rhinoContext.evaluateString(rhinoScope, annotateResource.inputStream.text, annotateResource.filename, 0, null)
			try { 
				rhinoContext.exit() 
			}
			catch (Exception e) { }				
		}
		
    }

    def process(input, assetFile) {
        try {
			def result
			
			if (useNashorn) {
				result = engine.invokeFunction("ngAnnotate", input, [add: true])
			}
			else {
				def context = Class.forName("org.mozilla.javascript.ContextFactory").getGlobal().enterContext()			
				def annotateScope = context.newObject(rhinoScope)
				
				annotateScope.setParentScope(rhinoScope)
				annotateScope.put("inputSrc", annotateScope, input)

				result = context.evaluateString(annotateScope, "ngAnnotate(inputSrc, {add: true})", "AngularJS annotate command", 0, null)
			}
			result.src
        } 
		catch (Exception e) {
            throw new Exception("AngularJS annotate failed", e)
        }

    }
}