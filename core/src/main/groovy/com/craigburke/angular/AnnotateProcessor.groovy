package com.craigburke.angular

import asset.pipeline.AssetFile
import asset.pipeline.AssetCompiler

import org.mozilla.javascript.Context
import org.mozilla.javascript.Script
import org.mozilla.javascript.ScriptableObject

import groovy.transform.Synchronized
import groovy.transform.CompileStatic

@CompileStatic
class AnnotateProcessor {

    static Script annotateScript
    private static final List<AnnotateProcessorCacheItem> cache

    AnnotateProcessor(AssetCompiler precompiler) { }

    static {
        int cores = Runtime.runtime.availableProcessors()
        cache = (1..cores).collect { new AnnotateProcessorCacheItem() }
    }

    @Synchronized
    static Script getAnnotateScript() {
        if (!annotateScript) {
            try {
                URL annotateResource = AnnotateProcessor.classLoader.getResource('ngannotate.js')
                Context context = Context.enter()
                annotateScript = context.compileString(annotateResource.text, annotateResource.file, 0, null)
            }
            catch (Exception ex) {
                throw new Exception("ngAnnotate initialization failed: ${ex.message}")
            }
            finally {
                try { Context.exit() }
                catch (Exception ex) {}
            }
        }

        annotateScript
    }

    static AnnotateProcessorCacheItem getCacheItem() {
        AnnotateProcessorCacheItem cacheItem

        synchronized (cache) {
            while (!cacheItem) {
                def availableCacheItems = cache.findAll { !it.inUse }

                if (availableCacheItems) {
                    cacheItem = availableCacheItems.sort { it.script }.first()
                    cacheItem.inUse = true
                }
                else {
                    cache.wait()
                }
            }
        }

        cacheItem

    }

    static AnnotateProcessorCacheItem getCacheItemAndCreateScript(Context context) {
        AnnotateProcessorCacheItem cacheItem = getCacheItem()

        if (!cacheItem.script) {
            ScriptableObject annotateScope = context.initStandardObjects(null, true)
            getAnnotateScript().exec(context, annotateScope)
            cacheItem.script = annotateScope
        }

        cacheItem
    }

    static void releaseCacheItem(AnnotateProcessorCacheItem cacheItem) {
        synchronized(cache) {
            cacheItem.inUse = false
            cache.notify()
        }
    }

    def process(String input, AssetFile assetFile) {
        AnnotateProcessorCacheItem cacheItem

        try {
            def context = Context.enter()

            cacheItem = getCacheItemAndCreateScript(context)

            cacheItem.script.put("inputSrc", cacheItem.script, input)
            Map result = (Map)context.evaluateString(cacheItem.script, "ngAnnotate(inputSrc, {add: true, sourcemap: false, stats: false})", "ngAnnotate command", 0, null)

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
            if (cacheItem) {
                releaseCacheItem(cacheItem)
            }
        }
    }
}
