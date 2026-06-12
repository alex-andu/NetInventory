package com.netinventory.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Un echipament fizic de rețea. Cheia primară [id] este chiar conținutul
 * codului QR (un UUID generat la creare), astfel încât scanarea unei etichete
 * duce direct la fișa echipamentului.
 *
 * Totul este stocat LOCAL pe dispozitiv (SQLite via Room). Nu există server.
 */
@Entity(tableName = "equipment")
data class Equipment(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String = EquipmentType.OTHER,
    val manufacturer: String = "",
    val model: String = "",
    val serialNumber: String = "",
    val rackLocation: String = "",
    val ipAddress: String = "",
    val vlans: String = "",
    val firmwareVersion: String = "",
    val notes: String = "",
    val lastMaintenance: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

/** Tipurile de echipamente disponibile (folosite în dropdown la adăugare/editare). */
object EquipmentType {
    const val SWITCH = "Switch"
    const val ROUTER = "Router"
    const val FIREWALL = "Firewall"
    const val SERVER = "Server"
    const val ACCESS_POINT = "Access Point"
    const val UPS = "UPS"
    const val PATCH_PANEL = "Patch Panel"
    const val OTHER = "Altul"

    val ALL = listOf(SWITCH, ROUTER, FIREWALL, SERVER, ACCESS_POINT, UPS, PATCH_PANEL, OTHER)
}
