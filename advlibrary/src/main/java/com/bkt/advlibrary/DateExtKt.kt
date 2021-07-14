package library.extensions

import com.bkt.advlibrary.DateFormats
import java.text.SimpleDateFormat
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

fun String.toDate(format: String = DateFormat.DATE.format): Date? {
    val df = SimpleDateFormat(format, Locale.US)
    df.timeZone = timeZoneIST
    return df.parse(this)
}

enum class DateFormat(val format: String) {
    DATE(DateFormats.DATE),
    DD_MMM_YYYY(DateFormats.DD_MMM_YYYY),
    DD_MMM(DateFormats.DD_MMM),
    DD_MMM_TIME(DateFormats.DD_MMM_TIME),
    TIME(DateFormats.TIME),
    MMM("MMM"),
    MMM_YYYY("MMM/YYYY")
}