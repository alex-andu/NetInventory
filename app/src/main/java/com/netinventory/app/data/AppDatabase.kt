package com.netinventory.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Baza de date locală a aplicației (SQLite, pe dispozitiv).
 * Fișierul fizic: netinventory.db în stocarea privată a aplicației.
 */
@Database(
    entities = [Equipment::class, LogEntry::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun equipmentDao(): EquipmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "netinventory.db"
                )
                    .addCallback(SeedCallback(context))
                    .build()
                INSTANCE = db
                db
            }
        }
    }

    /**
     * La prima creare a bazei de date inserează câteva echipamente demo,
     * ca aplicația să nu fie goală la prima rulare.
     */
    private class SeedCallback(private val context: Context) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                val dao = getInstance(context).equipmentDao()
                val now = System.currentTimeMillis()

                val sw = Equipment(
                    id = UUID.randomUUID().toString(),
                    name = "Core-SW-01",
                    type = EquipmentType.SWITCH,
                    manufacturer = "Cisco",
                    model = "Catalyst 9300",
                    serialNumber = "FCW2148L0AB",
                    rackLocation = "Rack A1 / U24",
                    ipAddress = "10.0.10.2",
                    vlans = "10 (Management), 20 (Users), 99 (Native)",
                    firmwareVersion = "IOS-XE 17.9.4",
                    notes = "Switch de nucleu, uplink dublu către distribuție.",
                    lastMaintenance = now - 1000L * 60 * 60 * 24 * 30,
                    createdAt = now
                )
                val fw = Equipment(
                    id = UUID.randomUUID().toString(),
                    name = "EDGE-FW-01",
                    type = EquipmentType.FIREWALL,
                    manufacturer = "Fortinet",
                    model = "FortiGate 60F",
                    serialNumber = "FGT60F1234567890",
                    rackLocation = "Rack A1 / U10",
                    ipAddress = "10.0.10.1",
                    vlans = "10 (Management), 100 (WAN)",
                    firmwareVersion = "FortiOS 7.4.3",
                    notes = "Gateway principal + VPN IPsec către site-ul secundar.",
                    lastMaintenance = now - 1000L * 60 * 60 * 24 * 7,
                    createdAt = now
                )
                val srv = Equipment(
                    id = UUID.randomUUID().toString(),
                    name = "ESXi-Host-01",
                    type = EquipmentType.SERVER,
                    manufacturer = "Dell",
                    model = "PowerEdge R740",
                    serialNumber = "BXTR5K2",
                    rackLocation = "Rack B2 / U6-U7",
                    ipAddress = "10.0.10.20",
                    vlans = "10 (Management), 30 (Storage)",
                    firmwareVersion = "BIOS 2.19.1",
                    notes = "Gazdă de virtualizare. Rulează EVE-NG și serviciile de monitorizare.",
                    lastMaintenance = now - 1000L * 60 * 60 * 24 * 90,
                    createdAt = now
                )

                dao.upsert(sw)
                dao.upsert(fw)
                dao.upsert(srv)

                dao.insertLog(
                    LogEntry(
                        equipmentId = fw.id,
                        type = LogType.MAINTENANCE,
                        message = "Update firmware la FortiOS 7.4.3 efectuat cu succes.",
                        timestamp = now - 1000L * 60 * 60 * 24 * 7
                    )
                )
                dao.insertLog(
                    LogEntry(
                        equipmentId = sw.id,
                        type = LogType.INCIDENT,
                        message = "Port Gi1/0/12 raportat defect — înlocuit cablu, monitorizare în curs.",
                        timestamp = now - 1000L * 60 * 60 * 24 * 2
                    )
                )
            }
        }
    }
}
