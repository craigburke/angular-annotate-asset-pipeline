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

    AnnotateProcessor(AssetCompiler precompiler) { }

    private static final List<AnnotateProcessorCacheItem> cache

    static {
        int cores = Runtime.getRuntime().availableProcessors();

        cache = (1..cores).collect { new AnnotateProcessorCacheItem(inUse: false, pos: it)}
    }

    @Synchronized
    static Script getAnnotateScript() {
        if (! annotateScript) {
            try {
                URL annotateResource = AnnotateProcessor.classLoader.getResource('ngannotate.js')

                Context context = Context.enter()

                annotateScript = context.compileString(annotateResource.text, annotateResource.file, 0, null);
            }
            catch (Exception ex) {
                throw new Exception("ngAnnotate initialization failed : " + ex.getMessage())
            }
            finally {
                try { Context.exit() }
                catch (Exception ex) {}
            }
        }

        annotateScript
    }

    static AnnotateProcessorCacheItem getCacheItem() {
        synchronized (cache) {

            while (true) {
                def  free = cache.findAll { !it.inUse }
                if (! free.isEmpty()) {
                    def first = free.find { it.script } // Priority to item with a script already initialized
                    if (! first) {
                        first = free.first()
                    }
                    first.inUse = true
                    return first
                }

                cache.wait()
            }
        }
    }

    static AnnotateProcessorCacheItem getCacheItemAndCreateScript(Context context) {
        AnnotateProcessorCacheItem cacheItem = getCacheItem()

        if (! cacheItem.script) {
            ScriptableObject annotateScope = context.initStandardObjects(null, true);
            getAnnotateScript().exec(context, annotateScope);

            cacheItem.script = annotateScope
        }

        return cacheItem
    }

    static void releaseCacheItem(AnnotateProcessorCacheItem cacheItem) {
        synchronized (cache) {
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
