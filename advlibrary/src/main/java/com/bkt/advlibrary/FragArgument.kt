package com.bkt.advlibrary

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import java.io.Serializable
import kotlin.properties.ReadWriteProperty
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class FragArgument<T>(private val defaultValue: T) : ReadWriteProperty<Fragment, T> {
    val key: String = "FragArg${Random.nextInt(0, 100000000)}"

    override fun getValue(
        thisRef: Fragment,
        property: KProperty<*>
    ): T {
        val arguments = thisRef.arguments ?: return defaultValue
        if (!arguments.containsKey(key))
            return defaultValue

        return when (property.returnType.classifier) {
            String::class -> arguments.getString(key)
            Int::class -> arguments.getInt(key)
            Boolean::class -> arguments.getBoolean(key)
            Long::class -> arguments.getLong(key)
            Float::class -> arguments.getFloat(key)
            Double::class -> arguments.getDouble(key)
            Char::class -> arguments.getChar(key)
            Short::class -> arguments.getShort(key)
            Byte::class -> arguments.getByte(key)
            CharSequence::class -> arguments.getCharSequence(key)
            Bundle::class -> arguments.getBundle(key)
            Parcelable::class -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val kClass = property.returnType.classifier as? KClass<*>
                    val javaClass = kClass?.java as? Class<Parcelable>
                    @Suppress("DEPRECATION")
                    javaClass?.let {
                        arguments.getParcelable(key, javaClass)
                    } ?: arguments.getParcelable(key)
                } else {
                    @Suppress("DEPRECATION")
                    arguments.getParcelable(key)
                }
            }

            Serializable::class -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val kClass = property.returnType.classifier as? KClass<*>
                    val javaClass = kClass?.java as? Class<Serializable>
                    @Suppress("DEPRECATION")
                    javaClass?.let {
                        arguments.getSerializable(key, javaClass)
                    } ?: arguments.getSerializable(key)
                } else {
                    @Suppress("DEPRECATION")
                    arguments.getSerializable(key)
                }
            }

            else -> {
                @Suppress("DEPRECATION")
                arguments.get(key)
            }
        } as T
    }

    override fun setValue(
        thisRef: Fragment,
        property: KProperty<*>,
        value: T
    ) {
        val arguments = thisRef.arguments ?: Bundle().also { thisRef.arguments = it }

        if (value == null) {
            // If the value is null, remove the key from the bundle
            arguments.remove(key)
        } else {
            // Put the non-null value into the Bundle based on its type
            when (value) {
                is String -> arguments.putString(key, value)
                is Int -> arguments.putInt(key, value)
                is Boolean -> arguments.putBoolean(key, value)
                is Long -> arguments.putLong(key, value)
                is Float -> arguments.putFloat(key, value)
                is Double -> arguments.putDouble(key, value)
                is Char -> arguments.putChar(key, value)
                is Short -> arguments.putShort(key, value)
                is Byte -> arguments.putByte(key, value)
                is ByteArray -> arguments.putByteArray(key, value)
                is CharArray -> arguments.putCharArray(key, value)
                is ShortArray -> arguments.putShortArray(key, value)
                is IntArray -> arguments.putIntArray(key, value)
                is LongArray -> arguments.putLongArray(key, value)
                is FloatArray -> arguments.putFloatArray(key, value)
                is DoubleArray -> arguments.putDoubleArray(key, value)
                is BooleanArray -> arguments.putBooleanArray(key, value)
                is CharSequence -> arguments.putCharSequence(key, value)
                is Bundle -> arguments.putBundle(key, value)
                is Parcelable -> arguments.putParcelable(key, value)
                is Serializable -> arguments.putSerializable(key, value)
                // Add more array types (e.g., ParcelableArray, StringArray) if needed
                // For ArrayLists (e.g., ArrayList<String>), you might need to add specific put methods like putStringArrayList
                // For other complex types, you might need to convert them to Parcelable/Serializable first.
                else -> throw IllegalArgumentException("${property.name} argument '$key' is not supported by Bundle for non-null values.")
            }
        }
    }
}

