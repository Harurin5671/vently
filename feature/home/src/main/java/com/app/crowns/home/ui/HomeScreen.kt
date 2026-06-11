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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.app.crowns.designsystem.color.*
import com.app.crowns.designsystem.preview.ThemePreviews
import com.app.crowns.designsystem.theme.VentlyTheme
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
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    dashboardContent: @Composable () -> Unit,
    stockContent: @Composable () -> Unit,
    historyContent: @Composable () -> Unit,
    settingsContent: @Composable () -> Unit,
    onFabClick: () -> Unit,
) {
    Scaffold(
        containerColor = Surface,
        bottomBar = {
            HomeBottomBar(
                bottomNavItems = bottomNavItems,
                selectedTab = selectedTab,
            ) {
                onTabSelected(it)
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

            if (selectedTab == 1) {
                IconButton(
                    onClick = { onFabClick() },
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

@ThemePreviews
@Composable
fun HomeScreenPreview() {
    VentlyTheme {
        HomeScreen(
            selectedTab = 0,
            onTabSelected = {},
            dashboardContent = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Dashboard Content")
                }
            },
            stockContent = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Stock Content")
                }
            },
            historyContent = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "History Content")
                }
            },
            settingsContent = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Settings Content")
                }
            },
            onFabClick = { }
        )
    }
}
