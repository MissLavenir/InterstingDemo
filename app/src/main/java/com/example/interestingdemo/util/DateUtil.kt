package com.example.interestingdemo.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    const val YYYYMMDD = "yyyyMMdd"
    const val YYYYMMDDHHMM = "yyyyMMddHHmm"
    const val YYYYMMDDHHMMSS = "yyyyMMddHHmmss"
    const val YYYY_MM_BARS = "yyyy-MM"
    const val YYYY_MM_DD_BARS = "yyyy-MM-dd"
    const val YYYY_MM_DD_POINT = "yyyy.MM.dd"
    const val YYYY_MM_DD_SLASH = "yyyy/MM/dd"
    const val YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm"
    const val YYYY_MM_DD_HH_MM_DOT = "yyyy.MM.dd HH:mm"
    const val YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"
    const val YY_MM_DD_UNIT = "yyyy年MM月dd日"
    const val MM_DD_SLASH = "MM/dd"
    const val MM_DD_BARS = "MM-dd"
    const val MM_DD_HH_MM = "MM-dd HH:mm"
    const val HHMMSS = "HHmmss"
    const val HH_MM_SS = "HH:mm:ss"
    const val HH_MM = "HH:mm"
    const val MM_DD_POINT = "MM.dd"
    const val YY_MM_DD_FORMAT = "yyyy年MM月dd日 HH点mm分"
    const val YY_MM_DD_POINT = "yyyy.MM.dd"
    const val hh_mm_FORMAT = "hh点mm分"

    fun currentDate(): String {
        val format = SimpleDateFormat(YYYY_MM_DD_BARS, Locale.getDefault())
        val data = Date(System.currentTimeMillis())
        return format.format(data)
    }

    fun currentDateTime(): String {
        val format = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS, Locale.getDefault())
        val data = Date(System.currentTimeMillis())
        return format.format(data)
    }

    fun getOldDate(distanceDay: Int): String {
        val dft = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val beginDate = Date()
        val date = Calendar.getInstance()
        date.time = beginDate
        date.set(Calendar.DATE, date.get(Calendar.DATE) - distanceDay)
        var endDate: Date? = null
        try {
            endDate = dft.parse(dft.format(date.time))
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return dft.format(endDate)
    }

    /**
     * 返回当月的第一天
     */
    fun getFirstDayOfMonth(): String {
        val format = SimpleDateFormat(YYYY_MM_DD_BARS, Locale.getDefault())
        val c = Calendar.getInstance()
        c.add(Calendar.MONTH, 0)
        c.set(Calendar.DAY_OF_MONTH, 1)
        return format.format(c.time)
    }

    /**
     * 返回当月的第一天+Time Minute
     */
    fun getFirstDayHMOfMonth(): String {
        val format = SimpleDateFormat(YYYY_MM_DD_HH_MM, Locale.getDefault())
        val c = Calendar.getInstance()
        c.add(Calendar.MONTH, 0)
        c.set(Calendar.DAY_OF_MONTH, 1)
        return format.format(c.time)
    }

    /**
     * 返回当月的最后一天
     */
    fun getLastDayOfMonth(): String {
        val format = SimpleDateFormat(YYYY_MM_DD_BARS, Locale.getDefault())
        val c = Calendar.getInstance()
        c.add(Calendar.MONTH, 1)
        c.set(Calendar.DAY_OF_MONTH, 0)
        return format.format(c.time)
    }

    /**
     * 返回当月的最后一天
     */
    fun getNextDay(): String {
        val format = SimpleDateFormat(YYYY_MM_DD_BARS, Locale.getDefault())
        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_MONTH, 1)
        return format.format(c.time)
    }

    fun getCurrentYMDate(): String {
        val format = SimpleDateFormat(YYYY_MM_BARS, Locale.getDefault())
        return format.format(Date())
    }

    fun getCurrentYMDTime(): String {
        val format = SimpleDateFormat(YYYY_MM_DD_BARS, Locale.getDefault())
        return format.format(Date())
    }

    fun getCurrentYMDHMTime(): String {
        val format = SimpleDateFormat(YYYY_MM_DD_HH_MM, Locale.getDefault())
        return format.format(Date())
    }

    fun getYMDHMTime(date: Date): String {
        val format = SimpleDateFormat(YYYY_MM_DD_HH_MM, Locale.getDefault())
        return format.format(date)
    }

    fun getYMDHMTimeDate(text: String?): Date? {
        text ?: return null
        val format = SimpleDateFormat(YYYY_MM_DD_HH_MM, Locale.getDefault())
        return try {
            format.parse(text)
        } catch (e: Exception) {
            null
        }
    }
}