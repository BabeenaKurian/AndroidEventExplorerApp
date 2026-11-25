package com.babeena.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.babeena.myapplication.ui.details.DetailsViewModel
import com.babeena.myapplication.ui.details.detailsScreen
import com.babeena.myapplication.ui.events.EventViewModel
import com.babeena.myapplication.ui.events.eventScreen


@Composable
fun navGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main_graph"
    ) {

        // Parent graph
        navigation(
            startDestination = "events",
            route = "main_graph"
        ) {

            composable("events") { backStackEntry ->

                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("main_graph")
                }

                val eventViewModel: EventViewModel =
                    hiltViewModel(parentEntry)

                eventScreen(
                    viewModel = eventViewModel,
                    onEventClick = { eventId ->
                        navController.navigate("eventDetails/$eventId")
                    })
            }

            composable(
                route = "eventDetails/{eventId}",
                arguments = listOf(
                    navArgument("eventId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->

                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("main_graph")
                }

                val eventViewModel: EventViewModel =
                    hiltViewModel(parentEntry)

                val userLocation by eventViewModel.userLocation
                    .collectAsStateWithLifecycle()

                val detailsViewModel: DetailsViewModel = hiltViewModel()

                val eventId = backStackEntry.arguments
                    ?.getString("eventId")

                LaunchedEffect(eventId) {
                    eventId?.let {
                        detailsViewModel.getEventDetails(it)
                    }
                }

                detailsScreen(
                    viewModel = detailsViewModel,
                    userLocation = userLocation,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

        }
    }
}