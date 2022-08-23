package com.bkt.advlibrary

import android.content.Context
import androidx.preference.PreferenceManager

fun getStringSettings(context: Context, key: String, default: String = ""): String {
    return PreferenceManager.getDefaultSharedPreferences(context).getString(key, default) ?: default
}

fun getIntSettings(context: Context, key: String): Int {
    return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, 0)
}

fun getBoolSettings(context: Context, key: String): Boolean {
    return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false)
}

fun writeStringToSettings(context: Context, key: String, value: String, ifEmpty: Boolean = false) {
    if (!ifEmpty) {
        val edit = PreferenceManager.getDefaultSharedPreferences(context).edit()
        edit.putString(key, value)
        edit.apply()
    } else if (getStringSettings(context, key).isEmpty()) {
        val edit2 = PreferenceManager.getDefaultSharedPreferences(context).edit()
        edit2.putString(key, value)
        edit2.apply()
    }
}

fun writeBoolToSettings(context: Context, key: String, value: Boolean, ifEmpty: Boolean) {
    if (!ifEmpty) {
        val edit = PreferenceManager.getDefaultSharedPreferences(context).edit()
        edit.putBoolean(key, value)
        edit.apply()
    } else if (getStringSettings(context, key).isEmpty()) {
        val edit2 = PreferenceManager.getDefaultSharedPreferences(context).edit()
        edit2.putBoolean(key, value)
        edit2.apply()
    }
}

fun writeIntToSettings(context: Context, key: String, value: Int): Int {
    val edit = PreferenceManager.getDefaultSharedPreferences(context).edit()
    edit.putInt(key, value)
    edit.apply()
    return value
}

fun writeBoolToSettings(context: Context, key: String, value: Boolean): Boolean {
    val edit = PreferenceManager.getDefaultSharedPreferences(context).edit()
    edit.putBoolean(key, value)
    edit.apply()
    return value
}

fun wipeAllPref(context: Context?): Runnable {
    return Runnable {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply()
    }
}