package com.netinventory.app.data

import androidx.room.Embedded
import androidx.room.Relation

/** Un echipament împreună cu toate intrările lui de jurnal. */
data class EquipmentWithLogs(
    @Embedded val equipment: Equipment,
    @Relation(
        parentColumn = "id",
        entityColumn = "equipmentId"
    )
    val logs: List<LogEntry>
)
