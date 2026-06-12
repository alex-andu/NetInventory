package com.netinventory.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.netinventory.app.data.Equipment
import com.netinventory.app.data.LogEntry
import com.netinventory.app.data.LogType
import com.netinventory.app.util.DateUtils
import com.netinventory.app.viewmodel.EquipmentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentDetailScreen(
    vm: EquipmentViewModel,
    equipmentId: String,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onShowQr: (String) -> Unit
) {
    val equipment by vm.observeEquipment(equipmentId).collectAsState(initial = null)
    val logs by vm.observeLogs(equipmentId).collectAsState(initial = emptyList())

    var showAddLog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val eq = equipment

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(eq?.name ?: "Echipament") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Înapoi")
                    }
                },
                actions = {
                    if (eq != null) {
                        IconButton(onClick = { onShowQr(eq.id) }) {
                            Icon(Icons.Filled.QrCode2, contentDescription = "Cod QR")
                        }
                        IconButton(onClick = { onEdit(eq.id) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Editează")
                        }
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Șterge")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (eq == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Echipamentul nu a fost găsit.")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            TagChip(eq.type)
            Spacer(Modifier.height(12.dp))

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    InfoRow("Producător", eq.manufacturer)
                    InfoRow("Model", eq.model)
                    InfoRow("Serial number", eq.serialNumber)
                    InfoRow("Poziție rack", eq.rackLocation)
                    InfoRow("Adresă IP", eq.ipAddress)
                    InfoRow("VLAN-uri", eq.vlans)
                    InfoRow("Firmware", eq.firmwareVersion)
                    InfoRow("Ultima mentenanță", DateUtils.formatShort(eq.lastMaintenance))
                    InfoRow("Adăugat la", DateUtils.formatShort(eq.createdAt))
                }
            }

            if (eq.notes.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                SectionTitle("Note")
                Card(Modifier.fillMaxWidth()) {
                    Text(eq.notes, modifier = Modifier.padding(16.dp))
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionTitle("Jurnal de evenimente")
                OutlinedButton(onClick = { showAddLog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Adaugă")
                }
            }

            if (logs.isEmpty()) {
                Text(
                    "Niciun eveniment înregistrat.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                logs.forEach { log ->
                    LogRow(log) { vm.deleteLog(log) }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }

    if (showAddLog) {
        AddLogDialog(
            onDismiss = { showAddLog = false },
            onConfirm = { type, message ->
                vm.addLog(eq!!.id, type, message)
                showAddLog = false
            }
        )
    }

    if (showDeleteConfirm && eq != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Ștergi echipamentul?") },
            text = { Text("\"${eq.name}\" și tot jurnalul asociat vor fi șterse definitiv.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.delete(eq)
                    showDeleteConfirm = false
                    onBack()
                }) { Text("Șterge") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Anulează") }
            }
        )
    }
}

@Composable
private fun LogRow(log: LogEntry, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TagChip(log.type)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        DateUtils.formatFull(log.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Șterge intrarea",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(log.message)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddLogDialog(
    onDismiss: () -> Unit,
    onConfirm: (type: String, message: String) -> Unit
) {
    var selectedType by remember { mutableStateOf(LogType.NOTE) }
    var message by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adaugă în jurnal") },
        text = {
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LogType.ALL.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type) }
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Descriere") },
                    modifier = Modifier.fillMaxWidth().height(120.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (message.isNotBlank()) onConfirm(selectedType, message.trim()) },
                enabled = message.isNotBlank()
            ) { Text("Salvează") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anulează") }
        }
    )
}
