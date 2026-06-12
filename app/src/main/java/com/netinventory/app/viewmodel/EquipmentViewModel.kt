package com.netinventory.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.netinventory.app.data.Equipment
import com.netinventory.app.data.EquipmentRepository
import com.netinventory.app.data.LogEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EquipmentViewModel(private val repo: EquipmentRepository) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val equipment: StateFlow<List<Equipment>> =
        _query
            .flatMapLatest { q -> repo.search(q) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val count: StateFlow<Int> =
        repo.count().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setQuery(q: String) {
        _query.value = q
    }

    fun observeEquipment(id: String): Flow<Equipment?> = repo.observeById(id)

    fun observeLogs(id: String): Flow<List<LogEntry>> = repo.observeLogs(id)

    fun save(equipment: Equipment) = viewModelScope.launch {
        repo.upsert(equipment)
    }

    fun delete(equipment: Equipment) = viewModelScope.launch {
        repo.delete(equipment)
    }

    fun addLog(equipmentId: String, type: String, message: String) = viewModelScope.launch {
        repo.addLog(LogEntry(equipmentId = equipmentId, type = type, message = message))
    }

    fun deleteLog(log: LogEntry) = viewModelScope.launch {
        repo.deleteLog(log)
    }

    /** Verifică dacă un id scanat există deja în baza locală. */
    suspend fun findById(id: String): Equipment? = repo.getById(id)

    class Factory(private val repo: EquipmentRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EquipmentViewModel::class.java)) {
                return EquipmentViewModel(repo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
