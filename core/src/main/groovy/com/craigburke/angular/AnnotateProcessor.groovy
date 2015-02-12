package com.craigburke.angular

import asset.pipeline.AssetFile

import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable
import asset.pipeline.AssetCompiler

import groovy.transform.CompileStatic
import groovy.transform.Synchronized

@CompileStatic
class AnnotateProcessor {
	
	static Scriptable globalScope
	
	AnnotateProcessor(AssetCompiler precompiler) { }

	@Synchronized
	static Scriptable getGlobalScope() {
        if (!globalScope) {
            try {
                URL annotateResource = AnnotateProcessor.classLoader.getResource('ngannotate.js')
                Context context = Context.enter()
                globalScope = context.initStandardObjects()
                context.evaluateString(globalScope, annotateResource.text, annotateResource.file, 0, null)
            }
            catch (Exception ex) {
                throw new Exception("ngAnnotate initialization failed")
            }
            finally {
                try { Context.exit() }
                catch (Exception ex) {}
            }
        }

        globalScope
    }

    def process(String input, AssetFile assetFile) {
		try {
			def context = Context.enter()
			def annotateScope = context.newObject(AnnotateProcessor.globalScope)
			annotateScope.setPrototype(AnnotateProcessor.globalScope)
			annotateScope.setParentScope(null)
			annotateScope.put("inputSrc", annotateScope, input)

			Map result = (Map)context.evaluateString(annotateScope, "ngAnnotate(inputSrc, {add: true, sourcemap: false, stats: false})", "ngAnnotate command", 0, null)
			
			if (result.containsKey('errors')) {
				throw new Exception(result.errors as String)
			}
			else {
				return result.src
			}

		} catch (Exception ex) {
			throw new Exception("ngAnnotate failed: ${ex.message}", ex)
		} finally {
			try { Context.exit() }
			catch (Exception ex) {}
		}
	}

}
