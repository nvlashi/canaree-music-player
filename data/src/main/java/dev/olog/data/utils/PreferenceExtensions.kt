package dev.olog.data.utils

import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.flowViaChannel
import kotlin.coroutines.CoroutineContext

inline fun <reified T> SharedPreferences.observeKey(key: String, default: T, dispatcher: CoroutineContext = Dispatchers.Default): Flow<T> {
    val flow: Flow<T> = flowViaChannel { channel ->
        channel.offer(getItem(key, default))

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
            if (key == k) {
                channel.offer(getItem(key, default)!!)
            }
        }

        registerOnSharedPreferenceChangeListener(listener)
        channel.invokeOnClose { unregisterOnSharedPreferenceChangeListener(listener) }
    }
    return flow
        .assertBackground()
        .flowOn(dispatcher)
}

inline fun <reified T> SharedPreferences.getItem(key: String, default: T): T {
    @Suppress("UNCHECKED_CAST")
    return when (default){
        is String -> getString(key, default) as T
        is Int -> getInt(key, default) as T
        is Long -> getLong(key, default) as T
        is Boolean -> getBoolean(key, default) as T
        is Float -> getFloat(key, default) as T
        is Set<*> -> getStringSet(key, default as Set<String>) as T
        is MutableSet<*> -> getStringSet(key, default as MutableSet<String>) as T
        else -> throw IllegalArgumentException("generic type not handle ${T::class.java.name}")
    }
}