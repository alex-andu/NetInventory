package com.netinventory.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipmentDao {

    // ---------- Equipment ----------

    @Query("SELECT * FROM equipment ORDER BY name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<Equipment>>

    @Query(
        """
        SELECT * FROM equipment
        WHERE name LIKE '%' || :q || '%'
           OR model LIKE '%' || :q || '%'
           OR serialNumber LIKE '%' || :q || '%'
           OR ipAddress LIKE '%' || :q || '%'
           OR rackLocation LIKE '%' || :q || '%'
        ORDER BY name COLLATE NOCASE ASC
        """
    )
    fun search(q: String): Flow<List<Equipment>>

    @Query("SELECT * FROM equipment WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<Equipment?>

    @Query("SELECT * FROM equipment WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): Equipment?

    @Query("SELECT COUNT(*) FROM equipment")
    fun count(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(equipment: Equipment)

    @Update
    suspend fun update(equipment: Equipment)

    @Delete
    suspend fun delete(equipment: Equipment)

    // ---------- Logs ----------

    @Transaction
    @Query("SELECT * FROM equipment WHERE id = :id LIMIT 1")
    fun observeWithLogs(id: String): Flow<EquipmentWithLogs?>

    @Query("SELECT * FROM log_entries WHERE equipmentId = :equipmentId ORDER BY timestamp DESC")
    fun observeLogs(equipmentId: String): Flow<List<LogEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: LogEntry)

    @Delete
    suspend fun deleteLog(log: LogEntry)
}
