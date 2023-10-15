package io.noties.adapt.ui.util

import android.content.Context
import java.util.WeakHashMap

object CachingContextWrapper {
    val cache = WeakHashMap<Context, Context>()

    fun contextWrapper(contextWrapper: (Context) -> Context): (Context) -> Context {
        return {
            cache.getOrPut(it) {
                // as context is wrapped and then it can be used to generate children
                //  we should not wrap already wrapped contexts
                if (cache.containsValue(it)) {
                    // put it also in cache to speed-up, already processed
                    it
                } else {
                    contextWrapper(it)
                }
            }
        }
    }

    operator fun invoke(contextWrapper: (Context) -> Context): (Context) -> Context = contextWrapper(contextWrapper)
}