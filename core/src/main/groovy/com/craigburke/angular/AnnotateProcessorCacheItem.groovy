package com.craigburke.angular

import org.mozilla.javascript.ScriptableObject

class AnnotateProcessorCacheItem {

    boolean inUse = false

    ScriptableObject script = null

    int pos

}