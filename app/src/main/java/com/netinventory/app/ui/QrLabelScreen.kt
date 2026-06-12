package com.netinventory.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.netinventory.app.util.QrUtils
import com.netinventory.app.viewmodel.EquipmentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrLabelScreen(
    vm: EquipmentViewModel,
    equipmentId: String,
    onBack: () -> Unit
) {
    val equipment by vm.observeEquipment(equipmentId).collectAsState(initial = null)
    val qrBitmap = remember(equipmentId) { QrUtils.generateImageBitmap(equipmentId, 600) }
    val eq = equipment

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eticheta QR") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Inapoi")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card {
                Column(
                    Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        eq?.name ?: "Echipament",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (eq != null) {
                        Text(
                            listOf(eq.manufacturer, eq.model).filter { it.isNotBlank() }
                                .joinToString(" "),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Image(
                        bitmap = qrBitmap,
                        contentDescription = "Cod QR pentru ${eq?.name}",
                        modifier = Modifier
                            .size(260.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(12.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        equipmentId,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            Text(
                "Fa o captura de ecran sau tipareste aceasta eticheta si lipeste-o pe echipament. " +
                    "La scanare, aplicatia va deschide direct aceasta fisa.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
