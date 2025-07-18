package com.bkt.advlibrary

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

val timeZoneIST: TimeZone = TimeZone.getTimeZone("Asia/Kolkata")
val today: Date by lazy { Calendar.getInstance().time }

fun sysdate(): Date {
    return Calendar.getInstance().time
}

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
    format: String = DateFormats.DATE.format,
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

fun Date.addTime(hours: Int, minutes: Int = 0): Date {
    val calendar = this.getCalendar()
    calendar.add(Calendar.HOUR_OF_DAY, hours)
    calendar.add(Calendar.MINUTE, minutes)
    this.time = calendar.timeInMillis
    return this
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

fun Date.getPreviousMonth() = this.getCalendar().also {
    it.add(Calendar.MONTH, -1)
}.time

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

fun Date.endOfDay(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar[Calendar.HOUR_OF_DAY] = 23
    calendar[Calendar.MINUTE] = 59
    calendar[Calendar.SECOND] = 59
    calendar[Calendar.MILLISECOND] = 999
    return calendar.time
}

fun Date.startOfDay(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0
    return calendar.time
}

fun Date.isWeekend(): Boolean {
    return getCalendar().get(Calendar.DAY_OF_WEEK) in listOf(
        Calendar.SATURDAY,
        Calendar.SUNDAY
    )
}

fun Date.getLastWeekdayOfWeek(): Date {
    val cal = getCalendar()
    when {
        cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY -> cal.add(Calendar.DATE, -1)
        cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY -> cal.add(Calendar.DATE, -2)
        else -> {}
    }
    return cal.time
}

fun String.toDateAsMillis(): Date? {
    if (this.length != 13)
        return null
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this.toLong()
    return calendar.time
}

fun Date.getYearStart(): Date {
    val calendar = getCalendar()
    calendar[Calendar.DAY_OF_YEAR] = calendar.getActualMinimum(Calendar.DAY_OF_YEAR)
    return calendar.time
}

fun Date.getYearEnd(): Date {
    val calendar = getCalendar()
    calendar[Calendar.DAY_OF_YEAR] = calendar.getActualMaximum(Calendar.DAY_OF_YEAR)
    return calendar.time
}

fun Date.getFinancialYearStart(): Date {
    val calendar = getCalendar()
    if (calendar[Calendar.MONTH] < Calendar.APRIL)
        calendar[Calendar.YEAR] -= 1
    calendar[Calendar.MONTH] = Calendar.APRIL
    calendar[Calendar.DAY_OF_MONTH] = calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
    return calendar.time
}

fun Date.getFinancialYearEnd(): Date {
    val calendar = getCalendar()
    if (calendar[Calendar.MONTH] > Calendar.MARCH)
        calendar[Calendar.YEAR] += 1
    calendar[Calendar.MONTH] = Calendar.MARCH
    calendar[Calendar.DAY_OF_MONTH] = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
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

fun String.toDate(format: String = DateFormats.DATE.format): Date? {
    val df = SimpleDateFormat(format, Locale.US)
    df.timeZone = timeZoneIST
    return df.parse(this)
}

fun String.toDateOrNull(format: String = DateFormats.DATE.format): Date? {
    val df = SimpleDateFormat(format, Locale.US)
    df.timeZone = timeZoneIST
    return try {
        df.parse(this)
    } catch (e: Exception) {
        null
    }
}

fun String.toDate(format: DateFormats = DateFormats.DATE): Date? {
    val df = SimpleDateFormat(format.format, Locale.US)
    df.timeZone = timeZoneIST
    return df.parse(this)
}

fun String.toDateOrNull(format: DateFormats = DateFormats.DATE): Date? {
    val df = SimpleDateFormat(format.format, Locale.US)
    df.timeZone = timeZoneIST
    return try {
        df.parse(this)
    } catch (_: Exception) {
        null
    }
}

fun Date.addCurrentTime(): Date {
    val dateCal = Calendar.getInstance()
    dateCal.time = this

    val returnCal = Calendar.getInstance()
    returnCal[Calendar.DAY_OF_YEAR] = dateCal[Calendar.DAY_OF_YEAR]
    return returnCal.time
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

fun Date?.isSameDateAs(otherDate: Date?): Boolean {
    if (this == null || otherDate == null)
        return false
    val rangeCal = otherDate.getCalendar()
    val effectiveCal = this.getCalendar()

    return rangeCal[Calendar.YEAR] == effectiveCal[Calendar.YEAR] && rangeCal[Calendar.MONTH] == effectiveCal[Calendar.MONTH] &&
            rangeCal[Calendar.DAY_OF_MONTH] == effectiveCal[Calendar.DAY_OF_MONTH]
}

fun Date.between(startDate: Date, endDate: Date): Boolean {
    return this.time >= startDate.time && this.time <= endDate.time
}

fun Long.toDateFromMillis(): Date {
    val cal = Calendar.getInstance()
    cal.timeInMillis = this
    return cal.time
}

operator fun Date.minus(days: Int): Date {
    return this.add(-days)
}

operator fun Date.plus(i: Int): Date {
    return this.add(i)
}

fun LocalDate.toDate(): Date {
    return Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())
}

fun Date.toLocalDate(): LocalDate {
    return this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
}

fun Date.between(
    startDate: LocalDate,
    endDate: LocalDate,
    inclusiveOf: Pair<Boolean, Boolean> = Pair(true, true)
): Boolean {
    val thisDate = toLocalDate()
    val isAfterStartDate = if (inclusiveOf.first) thisDate >= startDate else thisDate > startDate
    val isBeforeEndDate = if (inclusiveOf.second) thisDate <= endDate else thisDate < endDate
    return isAfterStartDate && isBeforeEndDate
}

/**
 * Change a date in string from one format to other
 */
fun String.changeDateFormat(fromFormat: String, toFormat: String): String? {
    return this.toDate(fromFormat)?.format(toFormat)
}