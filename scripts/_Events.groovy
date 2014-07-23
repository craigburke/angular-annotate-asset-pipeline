import asset.pipeline.JsAssetFile
import com.craigburke.angular.AnnotateProcessor

eventAssetPrecompileStart = { assetConfig ->
    JsAssetFile.processors << AnnotateProcessor
}