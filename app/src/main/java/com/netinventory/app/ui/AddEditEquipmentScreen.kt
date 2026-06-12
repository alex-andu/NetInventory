package com.netinventory.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.netinventory.app.data.Equipment
import com.netinventory.app.data.EquipmentType
import com.netinventory.app.viewmodel.EquipmentViewModel
import java.util.UUID

/**
 * Ecran unic pentru adaugare si editare.
 * - editId == null  => echipament nou (id generat sau preluat din scanare prin prefillId)
 * - editId != null  => editarea unui echipament existent
 */
@Composable
fun AddEditEquipmentScreen(
    vm: EquipmentViewModel,
    editId: String?,
    prefillId: String?,
    onBack: () -> Unit,
    onSaved: (String) -> Unit
) {
    if (editId == null) {
        AddEditContent(
            initial = Equipment(id = prefillId ?: UUID.randomUUID().toString(), name = ""),
            isNew = true,
            onBack = onBack,
            onSave = { vm.save(it); onSaved(it.id) }
        )
        return
    }

    val existing by vm.observeEquipment(editId).collectAsState(initial = null)
    val current = existing
    if (current == null) {
        LoadingScaffold()
    } else {
        AddEditContent(
            initial = current,
            isNew = false,
            onBack = onBack,
            onSave = { vm.save(it); onSaved(it.id) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadingScaffold() {
    Scaffold(topBar = { TopAppBar(title = { Text("Se incarca...") }) }) { p ->
        Box(Modifier.fillMaxSize().padding(p))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditContent(
    initial: Equipment,
    isNew: Boolean,
    onBack: () -> Unit,
    onSave: (Equipment) -> Unit
) {
    var name by remember { mutableStateOf(initial.name) }
    var type by remember { mutableStateOf(initial.type) }
    var manufacturer by remember { mutableStateOf(initial.manufacturer) }
    var model by remember { mutableStateOf(initial.model) }
    var serial by remember { mutableStateOf(initial.serialNumber) }
    var rack by remember { mutableStateOf(initial.rackLocation) }
    var ip by remember { mutableStateOf(initial.ipAddress) }
    var vlans by remember { mutableStateOf(initial.vlans) }
    var firmware by remember { mutableStateOf(initial.firmwareVersion) }
    var notes by remember { mutableStateOf(initial.notes) }
    var typeExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isNew) "Echipament nou" else "Editeaza echipament") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Inapoi")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (name.isNotBlank()) {
                                onSave(
                                    initial.copy(
                                        name = name.trim(),
                                        type = type,
                                        manufacturer = manufacturer.trim(),
                                        model = model.trim(),
                                        serialNumber = serial.trim(),
                                        rackLocation = rack.trim(),
                                        ipAddress = ip.trim(),
                                        vlans = vlans.trim(),
                                        firmwareVersion = firmware.trim(),
                                        notes = notes.trim()
                                    )
                                )
                            }
                        },
                        enabled = name.isNotBlank()
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = "Salveaza")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (isNew) {
                Text(
                    "ID / continut QR: ${initial.id}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
            }

            Field("Nume *", name) { name = it }

            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = it }
            ) {
                OutlinedTextField(
                    value = type,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tip") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false }
                ) {
                    EquipmentType.ALL.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                type = option
                                typeExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            Field("Producator", manufacturer) { manufacturer = it }
            Field("Model", model) { model = it }
            Field("Serial number", serial) { serial = it }
            Field("Pozitie rack", rack) { rack = it }
            Field("Adresa IP", ip) { ip = it }
            Field("VLAN-uri", vlans) { vlans = it }
            Field("Firmware", firmware) { firmware = it }
            Field("Note", notes, lines = 4) { notes = it }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun Field(
    label: String,
    value: String,
    lines: Int = 1,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = lines == 1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .then(if (lines > 1) Modifier.height((lines * 28 + 32).dp) else Modifier)
    )
}
