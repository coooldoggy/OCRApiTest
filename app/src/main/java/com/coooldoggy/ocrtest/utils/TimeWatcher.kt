package com.coooldoggy.ocrtest.utils

import android.content.Context
import java.text.DecimalFormat
import java.util.*


     var startTime: Long = 0
     var endTime: Long = 0
     var bworking = false

    fun Context.start() {
        startTime = System.currentTimeMillis()
        bworking = true
    }

    fun Context.stop() {
        bworking = false
        endTime = System.currentTimeMillis()
    }

    fun Context.isworking(): Boolean {
        return bworking
    }

    fun Context.watch(): Long {
        return if (bworking)
            (System.currentTimeMillis() - startTime) / 1000
        else
            0L
    }

    fun Context.getSeconds(): Long {
        return (endTime - startTime) / 1000
    }

    fun Context.getMilliSeconds(): Long {
        return endTime - startTime
    }

    fun Context.getstartString(): String {
        val yf = DecimalFormat("0000")
        val df = DecimalFormat("00")

        val c = Calendar.getInstance()

        c.timeInMillis = startTime

        return yf.format(c.get(Calendar.YEAR)) + "/" +
                df.format(c.get(Calendar.MONTH) + 1) + "/" +
                df.format(c.get(Calendar.DATE)) + " " +
                df.format(c.get(Calendar.HOUR)) + ":" +
                df.format(c.get(Calendar.MINUTE)) + ":" +
                df.format(c.get(Calendar.SECOND))
    }

    fun Context.getstopString(): String {
        val yf = DecimalFormat("0000")
        val df = DecimalFormat("00")

        val c = Calendar.getInstance()

        c.timeInMillis = endTime

        return yf.format(c.get(Calendar.YEAR)) + "/" +
                df.format(c.get(Calendar.MONTH) + 1) + "/" +
                df.format(c.get(Calendar.DATE)) + " " +
                df.format(c.get(Calendar.HOUR)) + ":" +
                df.format(c.get(Calendar.MINUTE)) + ":" +
                df.format(c.get(Calendar.SECOND))
    }
