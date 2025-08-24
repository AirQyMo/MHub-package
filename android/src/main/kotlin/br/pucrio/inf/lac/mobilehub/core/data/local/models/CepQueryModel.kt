package br.pucrio.inf.lac.mobilehub.core.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import br.pucrio.inf.lac.mobilehub.core.domain.entities.CepQuery
import java.util.*

@Entity(
    tableName = "cep_queries",
    indices = [Index(value = ["name"], unique = true)])
internal data class CepQueryModel(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true)  var id: Long? = null,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "statement") var statement: String,
    @ColumnInfo(name = "updated") var updated: Date = Date(),
    @ColumnInfo(name = "created") var created: Date = Date()
)