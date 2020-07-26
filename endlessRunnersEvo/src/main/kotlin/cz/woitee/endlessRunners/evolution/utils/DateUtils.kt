package cz.woitee.endlessRunners.evolution.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun timestampString(): String {
        return SimpleDateFormat("yyyy_MM_dd-HH_mm_ss_SSS").format(Date())
    }
}