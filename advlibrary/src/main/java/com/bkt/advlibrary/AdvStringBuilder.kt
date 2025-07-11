package com.bkt.advlibrary

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.TextAppearanceSpan
import android.text.style.UnderlineSpan
import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import java.util.regex.Pattern

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

    fun appendIfEmpty(text: CharSequence?): AdvStringBuilder {
        if (text != null && sb.isNotEmpty())
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
            appendSpan(text, *span.getSpans().toTypedArray())
        }
        return this
    }

    fun newLine(count: Int = 1): AdvStringBuilder {
        repeat(count) {
            append("\n")
        }
        return this
    }

    fun setSpan(span: Any, indexStart: Int, indexEnd: Int, flags: Int): AdvStringBuilder {
        sb.setSpan(span, indexStart, indexEnd, flags)
        return this
    }

    /**
     * We need to also invoke Textview.setMovementMethod(LinkMovementMethod.getInstance())
     */
    fun appendOnClick(text: CharSequence?, onClick: () -> Unit): (TextView) -> Unit {
        text ?: return {}
        val cs = object : ClickableSpan() {
            override fun onClick(v: View) {
                onClick.invoke()
            }
        }
        appendSpan(text, cs)
        return { textView: TextView ->
            textView.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    fun setClickable(
        indexStart: Int,
        indexEnd: Int,
        flags: Int,
        onCLick: () -> Unit
    ): AdvStringBuilder {
        sb.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                onCLick.invoke()
            }
        }, indexStart, indexEnd, flags)
        return this
    }

    fun setClickableUrlSpan(activity: CommonActivity) {
        val text = sb.toString()
        Patterns.WEB_URL.onMatch(text) { indexStart, indexEnd ->
            var url = text.substring(indexStart, indexEnd)
            if (!url.contains("www"))
                return@onMatch
            if (!url.startsWith("http"))
                url = "https://$url"
            sb.setSpan(object : ClickableSpan() {
                override fun onClick(p0: View) {
                    val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    if (i.resolveActivity(activity.packageManager) != null) {
                        activity.startActivity(i)
                    }
                }
            }, indexStart, indexEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    fun setClickableEmailSpan(activity: CommonActivity) {
        val text = sb.toString()
        Patterns.EMAIL_ADDRESS.onMatch(text) { indexStart, indexEnd ->
            val email = text.substring(indexStart, indexEnd)
            sb.setSpan(object : ClickableSpan() {
                override fun onClick(p0: View) {
                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
                    // only email apps should handle this
                    val chooser = Intent.createChooser(intent, "Email")
                    activity.startActivity(chooser)
                }
            }, indexStart, indexEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    fun appendIfNotEmpty(text: String, span: AdvSpan = AdvSpan()) {
        if (this.get().isNotEmpty())
            append(text, span)
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

inline fun buildSpan(builderAction: AdvStringBuilder.() -> Unit): CharSequence {
    return AdvStringBuilder().apply(builderAction).get()
}

data class AdvSpan(
    val isBold: Boolean = false,
    val color: Int = -1,
    val size: Float = 1.0f,
    val backgroundColor: Int = -1,
    val underlined: Boolean = false,
    val italics: Boolean = false,
    val strikeThrough: Boolean = false,
    val superScript: Boolean = false,
    val subScript: Boolean = false
) {
    lateinit var textAppearanceSpan: TextAppearanceSpan

    fun addTextAppearance(
        context: Context,
        @StyleRes styleId: Int
    ) {
        textAppearanceSpan = TextAppearanceSpan(context, styleId)
    }

    fun getSpans(): List<CharacterStyle> {
        val list = ArrayList<CharacterStyle>()

        if (isBold && italics)
            list.add(StyleSpan(Typeface.BOLD_ITALIC))
        else if (isBold)
            list.add(StyleSpan(Typeface.BOLD))
        else if (italics)
            list.add(StyleSpan(Typeface.ITALIC))

        if (color != -1)
            list.add(ForegroundColorSpan(color))
        if (backgroundColor != -1)
            list.add(BackgroundColorSpan(backgroundColor))
        if (size != 1.0f)
            list.add(RelativeSizeSpan(size))
        if (underlined)
            list.add(UnderlineSpan())
        if (strikeThrough)
            list.add(StrikethroughSpan())
        if (superScript)
            list.add(SuperscriptSpan())
        if (subScript)
            list.add(SubscriptSpan())
        if (::textAppearanceSpan.isInitialized)
            list.add(textAppearanceSpan)

        return list
    }
}

fun Pattern.onMatch(input: CharSequence, matchFound: (Int, Int) -> Unit) {
    val matcher = this.matcher(input)
    while (matcher.find()) {
        matchFound.invoke(matcher.start(), matcher.end())
    }
}