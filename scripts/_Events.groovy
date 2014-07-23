eventAssetPrecompileStart = { assetConfig ->
    def jsAssetFile = Class.forName('asset.pipeline.JsAssetFile')
    jsAssetFile.processors << Class.forName('com.craigburke.angular.AnnotateProcessor')
}