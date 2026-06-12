package com.netinventory.app.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.netinventory.app.data.Equipment
import com.netinventory.app.viewmodel.EquipmentViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentListScreen(
    vm: EquipmentViewModel,
    onOpenEquipment: (String) -> Unit,
    onAddNew: (String?) -> Unit
) {
    val items by vm.equipment.collectAsState()
    val query by vm.query.collectAsState()
    val count by vm.count.collectAsState()
    val scope = rememberCoroutineScope()

    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        val contents = result.contents
        if (contents != null) {
            scope.launch {
                val existing = vm.findById(contents)
                if (existing != null) onOpenEquipment(contents) else onAddNew(contents)
            }
        }
    }

    fun launchScan() {
        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setPrompt("Scanează eticheta QR a echipamentului")
            setBeepEnabled(true)
            setOrientationLocked(false)
        }
        scanLauncher.launch(options)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Column {
                    Text("NetInventory")
                    Text(
                        "$count echipamente în inventar",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddNew(null) }) {
                Icon(Icons.Filled.Add, contentDescription = "Adaugă echipament")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { launchScan() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Filled.QrCodeScanner, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Scanează cod QR", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = query,
                onValueChange = vm::setQuery,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                placeholder = { Text("Caută după nume, model, IP, rack…") }
            )

            Spacer(Modifier.height(8.dp))

            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (query.isBlank()) "Niciun echipament. Apasă + pentru a adăuga."
                        else "Niciun rezultat pentru \"$query\".",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items, key = { it.id }) { eq ->
                        EquipmentCard(eq) { onOpenEquipment(eq.id) }
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun EquipmentCard(eq: Equipment, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    eq.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                TagChip(eq.type)
            }
            Spacer(Modifier.height(4.dp))
            val subtitle = listOf(eq.manufacturer, eq.model)
                .filter { it.isNotBlank() }
                .joinToString(" ")
            if (subtitle.isNotBlank()) {
                Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (eq.ipAddress.isNotBlank() || eq.rackLocation.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                val meta = listOfNotNull(
                    eq.ipAddress.takeIf { it.isNotBlank() }?.let { "IP $it" },
                    eq.rackLocation.takeIf { it.isNotBlank() }
                ).joinToString("  •  ")
                Text(
                    meta,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
