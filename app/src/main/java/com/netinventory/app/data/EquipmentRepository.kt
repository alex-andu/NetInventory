package com.netinventory.app.data

import kotlinx.coroutines.flow.Flow

/**
 * Strat intermediar între ViewModel și DAO. Toate operațiile lucrează
 * exclusiv cu baza de date locală.
 */
class EquipmentRepository(private val dao: EquipmentDao) {

    fun observeAll(): Flow<List<Equipment>> = dao.observeAll()

    fun search(query: String): Flow<List<Equipment>> =
        if (query.isBlank()) dao.observeAll() else dao.search(query.trim())

    fun observeById(id: String): Flow<Equipment?> = dao.observeById(id)

    fun observeWithLogs(id: String): Flow<EquipmentWithLogs?> = dao.observeWithLogs(id)

    fun observeLogs(id: String): Flow<List<LogEntry>> = dao.observeLogs(id)

    fun count(): Flow<Int> = dao.count()

    suspend fun getById(id: String): Equipment? = dao.getById(id)

    suspend fun upsert(equipment: Equipment) = dao.upsert(equipment)

    suspend fun delete(equipment: Equipment) = dao.delete(equipment)

    suspend fun addLog(log: LogEntry) = dao.insertLog(log)

    suspend fun deleteLog(log: LogEntry) = dao.deleteLog(log)
}
