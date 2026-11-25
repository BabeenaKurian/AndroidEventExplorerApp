package com.babeena.myapplication.ui.events

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.babeena.myapplication.ui.theme.primaryRed
import com.babeena.myapplication.utils.DistanceUtils
import com.babeena.myapplication.utils.EventDateUtils.formatEventDate
import com.babeena.myapplication.R
@Composable
fun eventScreen(
    viewModel: EventViewModel, onEventClick: (String) -> Unit, onBookmarksClick: () -> Unit
) {
    val eventUiState by viewModel.eventUiState.collectAsStateWithLifecycle()
    val userLocation by viewModel.userLocation.collectAsStateWithLifecycle()
    val bookmarkedEventIds by viewModel.bookmarkedEventIds.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.fetchUserLocation()
        }
    }
    LaunchedEffect(Unit) {
        val hasLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (hasLocationPermission) {
            viewModel.fetchUserLocation()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 20.dp, end = 20.dp, bottom = 20.dp
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.events_list_title), style = MaterialTheme.typography.headlineSmall
                    )
                    TextButton(onClick = onBookmarksClick) {
                        Text(stringResource(R.string.bookmarks))
                    }
                }
                when {
                    eventUiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(12.dp))
                            Text(stringResource(R.string.loading))
                        }
                    }

                    eventUiState.errorMessage != null -> {
                        Column() {
                            Text(text = eventUiState.errorMessage ?:stringResource(R.string.please_try_again))
                        }
                    }

                    else -> {
                        if (eventUiState.data.isNotEmpty()) {
                            val events = eventUiState.data
                            Column() {
                                LazyColumn() {
                                    items(
                                        items = events, key = { event -> event.id }) { event ->
                                        val distanceInKm = userLocation?.let { location ->
                                            if (event.latitude != null && event.longitude != null) {
                                                DistanceUtils.calculateDistanceInKm(
                                                    userLatitude = location.userLatitude,
                                                    userLongitude = location.userLongitude,
                                                    eventLatitude = event.latitude,
                                                    eventLongitude = event.longitude
                                                )
                                            } else {
                                                null
                                            }
                                        }
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(
                                                    horizontal = 16.dp, vertical = 8.dp
                                                ),
                                            onClick = {
                                                onEventClick(event.id)
                                            },
                                            shape = RoundedCornerShape(20.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surface
                                            ),
                                            elevation = CardDefaults.cardElevation(
                                                defaultElevation = 3.dp
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(16.dp),
                                                verticalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                AsyncImage(
                                                    model = event.imageUrl,
                                                    contentDescription = event.title,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(200.dp)
                                                        .clip(
                                                            RoundedCornerShape(
                                                                topStart = 20.dp, topEnd = 20.dp
                                                            )
                                                        ),
                                                    contentScale = ContentScale.Crop
                                                )
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = event.title,
                                                        modifier = Modifier.weight(1f),
                                                        style = MaterialTheme.typography.titleLarge,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    IconButton(
                                                        onClick = {
                                                            viewModel.toggleBookmark(event)
                                                        }) {
                                                        Icon(
                                                            imageVector = if (event.id in bookmarkedEventIds) {
                                                                Icons.Default.Bookmark
                                                            } else {
                                                                Icons.Default.BookmarkBorder
                                                            },
                                                            contentDescription = if (event.id in bookmarkedEventIds) {
                                                                stringResource(R.string.remove_bookmark)
                                                            } else {
                                                                stringResource(R.string.add_bookmark)
                                                            },
                                                            tint = primaryRed
                                                        )
                                                    }
                                                }
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.CalendarMonth,
                                                        contentDescription = null,
                                                        tint = primaryRed,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Text(
                                                        text = formatEventDate(
                                                            event.dateTime ?: stringResource(R.string.date_not_available)
                                                        ),
                                                        modifier = Modifier.padding(
                                                            start = 8.dp
                                                        ),
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.LocationOn,
                                                        contentDescription = null,
                                                        tint = primaryRed,
                                                        modifier = Modifier.size(20.dp),

                                                        )
                                                    Text(
                                                        text = event.location
                                                            ?: stringResource(R.string.location_not_available),
                                                        modifier = Modifier.padding(
                                                            start = 8.dp
                                                        ),
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                if (distanceInKm != null) {
                                                    Text(
                                                        text = String.format(
                                                            java.util.Locale.getDefault(),
                                                            stringResource(R.string.distance_away),
                                                            distanceInKm
                                                        ),
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                                if (event.latitude != null && event.longitude != null) {
                                                    Button(
                                                        onClick = {
                                                            val mapUri = Uri.parse(
                                                                "geo:${event.latitude},${event.longitude}?q=${event.latitude},${event.longitude}(${
                                                                    Uri.encode(
                                                                        event.title
                                                                    )
                                                                })"
                                                            )
                                                            val mapIntent =
                                                                Intent(Intent.ACTION_VIEW, mapUri)
                                                            context.startActivity(mapIntent)
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = primaryRed,
                                                            contentColor = Color.White
                                                        )
                                                    ) {
                                                        Text(stringResource(R.string.open_in_maps))
                                                    }
                                                }
                                                event.description?.let {
                                                    Text(
                                                        text = it,
                                                        modifier = Modifier.padding(
                                                            top = 2.dp
                                                        ),
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Column() {
                                Text(stringResource(R.string.no_events_available))
                            }
                        }
                    }
                }
            }
        }
    }
}