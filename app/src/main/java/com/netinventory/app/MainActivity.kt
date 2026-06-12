package com.netinventory.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.netinventory.app.ui.AddEditEquipmentScreen
import com.netinventory.app.ui.EquipmentDetailScreen
import com.netinventory.app.ui.EquipmentListScreen
import com.netinventory.app.ui.QrLabelScreen
import com.netinventory.app.ui.theme.NetInventoryTheme
import com.netinventory.app.viewmodel.EquipmentViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as NetInventoryApp

        setContent {
            NetInventoryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val vm: EquipmentViewModel = viewModel(
                        factory = EquipmentViewModel.Factory(app.repository)
                    )
                    AppNavigation(vm)
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun AppNavigation(vm: EquipmentViewModel) {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = "list") {

        composable("list") {
            EquipmentListScreen(
                vm = vm,
                onOpenEquipment = { id -> nav.navigate("detail/$id") },
                onAddNew = { prefillId ->
                    if (prefillId == null) nav.navigate("add")
                    else nav.navigate("add?prefillId=$prefillId")
                }
            )
        }

        composable(
            route = "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString("id") ?: return@composable
            EquipmentDetailScreen(
                vm = vm,
                equipmentId = id,
                onBack = { nav.popBackStack() },
                onEdit = { eid -> nav.navigate("edit/$eid") },
                onShowQr = { eid -> nav.navigate("qr/$eid") }
            )
        }

        composable(
            route = "add?prefillId={prefillId}",
            arguments = listOf(
                navArgument("prefillId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { entry ->
            val prefillId = entry.arguments?.getString("prefillId")
            AddEditEquipmentScreen(
                vm = vm,
                editId = null,
                prefillId = prefillId,
                onBack = { nav.popBackStack() },
                onSaved = { id ->
                    nav.popBackStack()
                    nav.navigate("detail/$id")
                }
            )
        }

        composable(
            route = "edit/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString("id") ?: return@composable
            AddEditEquipmentScreen(
                vm = vm,
                editId = id,
                prefillId = null,
                onBack = { nav.popBackStack() },
                onSaved = { nav.popBackStack() }
            )
        }

        composable(
            route = "qr/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString("id") ?: return@composable
            QrLabelScreen(
                vm = vm,
                equipmentId = id,
                onBack = { nav.popBackStack() }
            )
        }
    }
}
