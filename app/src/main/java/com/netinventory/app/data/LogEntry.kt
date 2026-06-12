package com.netinventory.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * O intrare în jurnalul unui echipament: notiță, mentenanță sau incident.
 * Legată de [Equipment] prin [equipmentId]. Ștergerea echipamentului
 * șterge automat intrările asociate (CASCADE).
 */
@Entity(
    tableName = "log_entries",
    foreignKeys = [
        ForeignKey(
            entity = Equipment::class,
            parentColumns = ["id"],
            childColumns = ["equipmentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("equipmentId")]
)
data class LogEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val equipmentId: String,
    val type: String = LogType.NOTE,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

object LogType {
    const val NOTE = "Notiță"
    const val MAINTENANCE = "Mentenanță"
    const val INCIDENT = "Incident"

    val ALL = listOf(NOTE, MAINTENANCE, INCIDENT)
}
