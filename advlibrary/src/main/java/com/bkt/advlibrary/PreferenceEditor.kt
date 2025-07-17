package com.bkt.advlibrary

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class AdvPreference<T>(
    context: Context,
    private val key: String,
    private val defaultValue: T
) : ReadWriteProperty<Any?, T> {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(key, defaultValue)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(key, value)
    }

    fun remove() = prefs.edit {
        remove(key)
    }

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

    private fun putPreference(name: String, value: T) = with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("This type cannot be saved into Preferences")
        }.apply()
    }
}

fun Context.getStringPreferences(key: String, default: String = ""): String {
    return PreferenceManager.getDefaultSharedPreferences(this).getString(key, default) ?: default
}

fun Context.getIntPreferences(context: Context, key: String, default: Int = 0): Int {
    return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, default)
}

fun getBoolPreferences(context: Context, key: String, default: Boolean = false): Boolean {
    return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, default)
}

fun writeStringToPreferences(
    context: Context,
    key: String,
    value: String,
    ifEmpty: Boolean = false
) {
    if (!ifEmpty) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putString(key, value)
        }
    } else if (context.getStringPreferences(key).isEmpty()) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putString(key, value)
        }
    }
}

fun writeBoolToPreferences(context: Context, key: String, value: Boolean, ifEmpty: Boolean) {
    if (!ifEmpty) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putBoolean(key, value)
        }
    } else if (context.getStringPreferences(key).isEmpty()) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putBoolean(key, value)
        }
    }
}

fun writeIntToPreferences(context: Context, key: String, value: Int): Int {
    PreferenceManager.getDefaultSharedPreferences(context).edit {
        putInt(key, value)
    }
    return value
}

fun writeBoolToPreferences(context: Context, key: String, value: Boolean): Boolean {
    PreferenceManager.getDefaultSharedPreferences(context).edit {
        putBoolean(key, value)
    }
    return value
}

fun wipeAllPref(context: Context) {
    return PreferenceManager.getDefaultSharedPreferences(context).edit { clear() }
}