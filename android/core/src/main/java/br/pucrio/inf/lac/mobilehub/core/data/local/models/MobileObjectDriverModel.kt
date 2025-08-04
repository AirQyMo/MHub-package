package br.pucrio.inf.lac.mobilehub.core.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "mobile_object_driver",
    indices = [Index(value = ["wpan", "name"], unique = true)])
internal data class MobileObjectDriverModel(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true)  var id: Long? = null,
    @ColumnInfo(name = "wpan") var wpan: Int,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "config") var config: String?,
    @ColumnInfo(name = "content") var content: String,
    @ColumnInfo(name = "required") var required: Date = Date(),
    @ColumnInfo(name = "updated") var updated: Date = Date(),
    @ColumnInfo(name = "created") var created: Date = Date()
) {
    fun updateLastRequired() {
        required = Date()
    }
}