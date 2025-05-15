package com.assignmentwaala.unidatahub.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.assignmentwaala.unidatahub.R
import com.assignmentwaala.unidatahub.presentation.Routes

data class BottomNavItem(
    val route: Routes,
    val label: String,
    val icon: ImageVector
)

@Composable
fun BottomNavBar(navController: NavHostController) {
    var bottomNavSelectedItem by rememberSaveable { mutableStateOf(0) }
    val bottomNavItems = listOf(
        BottomNavItem(
            route = Routes.Home,
            label = "Home",
            icon = Icons.Default.Home
        ),
        BottomNavItem(
            route = Routes.Community,
            label = "Community",
            icon = ImageVector.vectorResource(R.drawable.ic_group)
        ),
        BottomNavItem(
            route = Routes.Profile,
            label = "Profile",
            icon = Icons.Default.Person
        )
    )


    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    LaunchedEffect(currentDestination) {
        when (currentDestination) {
            Routes.Home::class.qualifiedName -> {
                bottomNavSelectedItem = 0
            }

            Routes.Community::class.qualifiedName -> {
                bottomNavSelectedItem = 1
            }

            Routes.Profile::class.qualifiedName -> {
                bottomNavSelectedItem = 2
            }
        }
    }

    BottomAppBar {
        bottomNavItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = index == bottomNavSelectedItem,
                onClick = {
                    if(bottomNavSelectedItem == index) return@NavigationBarItem
                    navController.navigate(item.route) {
                        popUpTo(Routes.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                    bottomNavSelectedItem = index
                },
                label = {
                    Text(text = item.label)
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(28.dp),
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF3D5AF1),
                    selectedTextColor = Color(0xFF3D5AF1),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color(0xFFEEF1FF)
                )
            )
        }
    }

}

