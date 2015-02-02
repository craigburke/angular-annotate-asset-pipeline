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
	
	AnnotateProcessor(AssetCompiler precompiler) {
		this.initializeScope()
	}

	@Synchronized
	static void initializeScope() {
		if (!this.globalScope) {
			try {
				URL annotateResource = AnnotateProcessor.classLoader.getResource('ngannotate.js')
				Context context = Context.enter()
				AnnotateProcessor.globalScope = context.initStandardObjects()
				context.evaluateString(AnnotateProcessor.globalScope, annotateResource.text, annotateResource.file, 0, null)
			}
			catch (Exception ex) {
				throw new Exception("ngAnnotate initialization failed")
			}
			finally {
				try { Context.exit() }
				catch (Exception ex) {}
			}
		}
	}

    def process(String input, AssetFile assetFile) {
		try {
			def context = Context.enter()
			def annotateScope = context.newObject(AnnotateProcessor.globalScope)
			
			annotateScope.setParentScope(AnnotateProcessor.globalScope)
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
