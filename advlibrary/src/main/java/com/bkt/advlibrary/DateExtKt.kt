package com.bkt.advlibrary

import com.bkt.advlibrary.DateFormats
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

val timeZoneIST: TimeZone = TimeZone.getTimeZone("Asia/Kolkata")
val today: Date by lazy { Calendar.getInstance().time }
fun Date.getCalendar(): Calendar {
    val cal = Calendar.getInstance(Locale.US)
    cal.time = this
    return cal
}

fun Calendar.getYear(): Int {
    return this[Calendar.YEAR]
}

fun Calendar.getMonth(): Int {
    return this[Calendar.DAY_OF_MONTH]
}

fun Date.trim(): Date {
    val calendar = getCalendar()
    calendar[11] = 0
    calendar[12] = 0
    calendar[13] = 0
    calendar[14] = 0
    return calendar.time
}

/*fun Date.format(
    format: DateFormat = DateFormat.DATE,
    applyTrim: Boolean = false,
    timeZone: TimeZone = timeZoneIST
): String {
    val date =
        if (applyTrim) {
            trim()
        } else this
    val df = SimpleDateFormat(format.format, Locale.US)
    df.timeZone = timeZone
    return df.format(date)
}*/
fun Date?.format(
    format: String = DateFormats.DATE,
    applyTrim: Boolean = false,
    timeZone: TimeZone = timeZoneIST
): String {
    if (this == null) return ""
    val date =
        if (applyTrim) {
            trim()
        } else this
    val df = SimpleDateFormat(format, Locale.US)
    df.timeZone = timeZone
    return df.format(date)
}

fun Date.add(days: Int, months: Int = 0, years: Int = 0): Date {
    val c = getCalendar()
    c.add(Calendar.DAY_OF_MONTH, days)
    c.add(Calendar.MONTH, months)
    c.add(Calendar.YEAR, years)
    return c.time
}

fun Date.getMonthStart(): Date {
    val calendar = getCalendar()
    calendar[Calendar.DAY_OF_MONTH] = calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
    return calendar.time
}

fun Date.getMonthEnd(): Date {
    val calendar = getCalendar()
    calendar[Calendar.DAY_OF_MONTH] = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    return calendar.time
}

fun Date.getWeekStart(): Date {
    val calendar = getCalendar()
    calendar.add(
        Calendar.DAY_OF_WEEK,
        calendar.firstDayOfWeek - calendar.get(Calendar.DAY_OF_WEEK)
    )
    return calendar.time
}

fun Date.getWeekEnd(): Date {
    val calendar = getCalendar()
    calendar.add(
        Calendar.DAY_OF_WEEK,
        calendar.firstDayOfWeek - calendar.get(Calendar.DAY_OF_WEEK)
    )
    calendar.add(Calendar.DAY_OF_YEAR, 6)
    return calendar.time
}

fun Date.getDaysTo(toDate: Date, useTime: Boolean = false): Double {
    val endTime: Long
    val startTime: Long
    if (useTime) {
        startTime = time
        endTime = toDate.time
    } else {
        startTime = trim().time
        endTime = toDate.trim().time
    }
    return ((endTime - startTime).toFloat() / 8.64E7f).toDouble()
}

fun Date.monthsTo(date: Date): Long {
    return ChronoUnit.MONTHS.between(
        this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            .withDayOfMonth(1),
        date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().withDayOfMonth(1)
    )
}

fun String.toDate(format: String = DateFormats.DATE): Date? {
    val df = SimpleDateFormat(format, Locale.US)
    df.timeZone = timeZoneIST
    return df.parse(this)
}

fun Date.isSameMonthAs(date: Date = today): Boolean {
    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance()
    cal1.time = this
    cal2.time = date
    return cal1[Calendar.YEAR] == cal2[Calendar.YEAR] && cal1[Calendar.MONTH] == cal2[Calendar.MONTH]
}


fun Calendar.setValues(
    day: Int = this[Calendar.DAY_OF_MONTH],
    month: Int = this[Calendar.MONTH],
    year: Int = this[Calendar.YEAR]
): Calendar {
    this[Calendar.DAY_OF_MONTH] = day
    this[Calendar.MONTH] = month
    this[Calendar.YEAR] = year
    return this
}

fun Date.inSameMonthOf(rangeDate: Date): Boolean {
    val rangeCal = rangeDate.getCalendar()
    val effectiveCal = this.getCalendar()
    return rangeCal[Calendar.YEAR] == effectiveCal[Calendar.YEAR] && rangeCal[Calendar.MONTH] == effectiveCal[Calendar.MONTH]
}

fun Date.between(startDate: Date, endDate: Date): Boolean {
    return this.time >= startDate.time && this.time <= endDate.time
}

fun Long.toDateFromMillis(): Date {
    val cal = Calendar.getInstance()
    cal.timeInMillis = this
    return cal.time
}