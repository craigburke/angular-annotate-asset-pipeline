package com.craigburke.angular

import asset.pipeline.JsAssetFile
import grails.util.Holders

class AnnotateProcessorUtil {

    static def load() {
        JsAssetFile.processors << AnnotateProcessor

        boolean coffeeScriptPlugin = Holders.pluginManager?.allPlugins?.find { it.name == 'coffeeAssetPipeline' }
        if (coffeeScriptPlugin) {
            def coffeeScriptAssetFile = AnnotateProcessorUtil.classLoader.loadClass('asset.pipeline.coffee.CoffeeAssetFile')
            coffeeScriptAssetFile?.processors << AnnotateProcessor
        }

    }

}
