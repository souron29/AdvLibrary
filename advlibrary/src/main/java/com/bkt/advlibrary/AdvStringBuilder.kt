package com.bkt.advlibrary

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.*
import android.view.View
import androidx.annotation.ColorInt

class AdvStringBuilder(private val initialText: CharSequence = "", vararg spans: CharacterStyle) {
    private var sb: SpannableStringBuilder

    init {
        sb = SpannableStringBuilder()
        appendSpan(initialText, *spans)
    }

    fun append(text: CharSequence?): AdvStringBuilder {
        if (text != null)
            sb.append(text)
        return this
    }

    fun appendSpan(text: CharSequence, vararg spans: CharacterStyle): AdvStringBuilder {
        sb.append(text)
        val start = sb.length - text.length
        val end = sb.length

        for (span in spans)
            sb.setSpan(
                span,
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        return this
    }

    fun appendBold(text: CharSequence?): AdvStringBuilder {
        if (text != null)
            appendSpan(text, StyleSpan(Typeface.BOLD))
        return this
    }

    fun appendColor(text: CharSequence?, @ColorInt color: Int): AdvStringBuilder {
        if (text != null) {
            appendSpan(text, ForegroundColorSpan(color))
        }
        return this
    }

    fun appendBackgroundColor(text: CharSequence?, @ColorInt color: Int): AdvStringBuilder {
        if (text != null) {
            appendSpan(text, BackgroundColorSpan(color))
        }
        return this
    }

    fun appendUnderline(text: CharSequence?): AdvStringBuilder {
        if (text != null) {
            appendSpan(text, UnderlineSpan())
        }
        return this
    }

    fun appendClickable(text: CharSequence?, onCLick: () -> Unit): AdvStringBuilder {
        if (text != null) {
            appendSpan(text, object : ClickableSpan() {
                override fun onClick(widget: View) {
                    onCLick.invoke()
                }
            })
        }
        return this
    }

    fun append(text: CharSequence?, span: AdvSpan): AdvStringBuilder {
        if (text != null) {
            sb.append(text)
            val start = sb.length - text.length
            val end = sb.length
            if (span.isBold)
                sb.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            if (span.color != -1)
                sb.setSpan(
                    ForegroundColorSpan(span.color),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            if (span.size != 1.0f)
                sb.setSpan(
                    RelativeSizeSpan(span.size),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
        }
        return this
    }


    fun clear(): AdvStringBuilder {
        sb = SpannableStringBuilder()
        return this
    }

    fun get(): CharSequence {
        return sb
    }

    override fun toString(): String {
        return sb.toString()
    }


}

data class AdvSpan(val isBold: Boolean = false, val color: Int = -1, val size: Float = 1.0f)