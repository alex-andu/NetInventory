package com.netinventory.app

import android.app.Application
import com.netinventory.app.data.AppDatabase
import com.netinventory.app.data.EquipmentRepository

/**
 * Application class — punct unic de creare a bazei de date locale și a
 * repository-ului, partajate în toată aplicația.
 */
class NetInventoryApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val repository: EquipmentRepository by lazy { EquipmentRepository(database.equipmentDao()) }
}
