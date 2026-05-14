package com.app.crowns.home.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.app.crowns.designsystem.color.PrimaryContainer
import com.app.crowns.designsystem.color.Surface

data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

@Composable
fun HomeBottomBar(
    bottomNavItems: List<BottomNavItem>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
) {
    NavigationBar(
        containerColor = Surface,
        tonalElevation = 0.dp,
    ) {
        bottomNavItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = if (selectedTab == index) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryContainer,
                    selectedTextColor = PrimaryContainer,
                    indicatorColor = PrimaryContainer.copy(alpha = 0.12f),
                ),
            )
        }
    }
}