eventAssetPrecompileStart = { assetConfig ->
    def AnnotateProcessorUtil = classLoader.loadClass('com.craigburke.angular.AnnotateProcessorUtil')
    AnnotateProcessorUtil.load()
}