package com.bkt.advlibrary

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.databinding.ObservableField
import androidx.preference.PreferenceManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class AdvPreference<T>(context: Context, private val key: String, private val defaultValue: T) :
    ReadWriteProperty<Any?, T>, ObservableField<T>() {
    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    private var value: T
        get() = findPreference(key, defaultValue)
        set(value) {
            putPreference(key, value)
        }

    val flow: Flow<T> by lazy {
        callbackFlow {
            val listener =
                SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, changedKey ->
                    if (key == changedKey) {
                        trySend(value)
                    }
                }

            prefs.registerOnSharedPreferenceChangeListener(listener)

            // Ensure the flow starts with the current value immediately
            trySend(value)

            // Unregister the listener when the flow is closed/cancelled
            awaitClose {
                prefs.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    override fun get(): T? {
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value ?: defaultValue
        set(value)
    }

    override fun set(value: T?) {
        this.value = value ?: defaultValue
        super.set(this.value)
    }

    fun remove() = prefs.edit { remove(key) }

    @Suppress("UNCHECKED_CAST")
    private fun findPreference(name: String, default: T): T = with(prefs) {
        val res: Any? = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException("This type cannot be saved into Preferences")
        }
        res as T
    }

    private fun putPreference(name: String, value: T?) = with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("This type cannot be saved into Preferences")
        }.apply()
    }

    companion object {
        fun clearAllPreferences(context: Context): SharedPreferences.Editor? {
            return context.getPreferences().edit().clear()
        }

        fun getStringPreferences(context: Context, key: String, default: String = ""): String {
            return context.getPreferences().getString(key, default) ?: default
        }

        fun getIntPreferences(context: Context, key: String, default: Int = 0): Int {
            return context.getPreferences().getInt(key, default)
        }

        fun getBoolPreferences(context: Context, key: String, default: Boolean = false): Boolean {
            return context.getPreferences().getBoolean(key, default)
        }

        fun writeStringToPreferences(
            context: Context,
            key: String,
            value: String,
            ifEmpty: Boolean = false
        ) {
            if (!ifEmpty) {
                context.getPreferences().edit { putString(key, value) }
            } else if (getStringPreferences(context, key).isEmpty()) {
                context.getPreferences().edit { putString(key, value) }
            }
        }

        fun writeBoolToPreferences(
            context: Context,
            key: String,
            value: Boolean,
            ifEmpty: Boolean = false
        ) {
            if (!ifEmpty) {
                context.getPreferences().edit { putBoolean(key, value) }
            } else if (getStringPreferences(context, key).isEmpty()) {
                context.getPreferences().edit { putBoolean(key, value) }
            }
        }

        fun writeIntToPreferences(
            context: Context,
            key: String,
            value: Int,
            ifEmpty: Boolean = false
        ) {
            if (!ifEmpty) {
                context.getPreferences().edit { putInt(key, value) }
            } else if (getStringPreferences(context, key).isEmpty()) {
                context.getPreferences().edit { putInt(key, value) }
            }
        }
    }
}

fun Context.getPreferences(): SharedPreferences =
    PreferenceManager.getDefaultSharedPreferences(this)