package com.app.crowns.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.app.crowns.designsystem.color.OnPrimary
import com.app.crowns.designsystem.color.PrimaryContainer
import com.app.crowns.designsystem.color.Surface
import com.app.crowns.home.ui.components.BottomNavItem
import com.app.crowns.home.ui.components.HomeBottomBar

val bottomNavItems = listOf(
    BottomNavItem("Inicio", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem("Stock", Icons.Filled.Inventory2, Icons.Outlined.Inventory2),
    BottomNavItem("Historial", Icons.Filled.History, Icons.Outlined.History),
    BottomNavItem("Ajustes", Icons.Filled.Settings, Icons.Outlined.Settings),
)

@Composable
fun HomeScreen(
    dashboardContent: @Composable () -> Unit,
    stockContent: @Composable () -> Unit,
    historyContent: @Composable () -> Unit,
    settingsContent: @Composable () -> Unit,
    onFabClick: (tab: Int) -> Unit,
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        containerColor = Surface,
        bottomBar = {
            HomeBottomBar(
                bottomNavItems = bottomNavItems,
                selectedTab = selectedTab,
            ) {
                selectedTab = it
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> dashboardContent()
                1 -> stockContent()
                2 -> historyContent()
                3 -> settingsContent()
            }

            // FAB flotante sobre el bottom bar
            if (selectedTab == 1) { // Solo mostrar en Stock por ahora
                IconButton(
                    onClick = { onFabClick(selectedTab) },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 16.dp, end = 16.dp)
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(PrimaryContainer),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Nueva boleta o factura",
                        tint = OnPrimary,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
        }
    }
}
