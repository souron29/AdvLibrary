package com.bkt.advlibrary

import java.util.Date
import java.util.TimeZone

enum class DateFormats(val format: String) {
    DATE("dd/MM/yyyy"),
    DATE_TIME_API("yyyy-MM-dd HH:mm"),
    DB_DATE_CODE("yyyyMMdd"),
    DB_DATE_TIME("yyyy-MM-dd HH:mm:ss"),
    DB_DATE_TIME_CODE("yyyyMMddHHmmss"),
    DD_MMM("dd MMM"),
    DD_MMM_TIME("dd MMM, h:mm aa"),
    DD_MMM_YYYY("dd MMM, yyyy"),
    DD_MMM_YYYY_TIME("dd MMM, yyyy h:mm aa"),
    MMM_YYYY("MMM, yyyy"),
    D_MMM_NEXT_LINE("d\nMMM"),
    EEE("EEE"),
    EEEE("EEEE"),
    TIME("h:mm aa"),
    EEEE_TIME("EEEE TIME"),
    EEE_TIME("EEE TIME"),
}

fun Date.format(
    dateFormat: DateFormats, applyTrim: Boolean = false,
    timeZone: TimeZone = timeZoneIST
) = format(dateFormat.format, applyTrim, timeZone)