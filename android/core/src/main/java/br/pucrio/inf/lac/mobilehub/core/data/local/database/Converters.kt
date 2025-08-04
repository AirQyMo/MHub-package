package br.pucrio.inf.lac.mobilehub.core.data.local.database

import androidx.room.TypeConverter
import java.util.*

@Suppress("unused")
internal class Converters {
    @TypeConverter
    fun dateFromTimestamp(value: Long?): Date? = if (value == null) null else Date(value)

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}