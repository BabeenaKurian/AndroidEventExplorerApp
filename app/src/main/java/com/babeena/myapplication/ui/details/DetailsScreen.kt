package com.babeena.myapplication.ui.details

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.babeena.myapplication.R
import com.babeena.myapplication.ui.theme.primaryRed
import com.babeena.myapplication.utils.DistanceUtils
import com.babeena.myapplication.utils.EventDateUtils.formatEventDate
import com.babeena.myapplication.utils.UserLocation


@Composable
fun detailsScreen(
    viewModel: DetailsViewModel, userLocation: UserLocation?, onBackClick: () -> Unit
) {
    val detailsUiState by viewModel.detailsUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(vertical = 4.dp)
                    .padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.go_back)
                    )
                }
                Text(
                    text = stringResource(R.string.event_details_title), style = MaterialTheme.typography.titleLarge
                )
            }
        }) { innerPadding ->
        when {
            detailsUiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text(stringResource(R.string.loading))
                }
            }

            detailsUiState.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = detailsUiState.errorMessage ?: stringResource(R.string.unable_to_load_event_details),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            else -> {
                if (detailsUiState.event != null) {
                    val event = detailsUiState.event!!

                    val distanceInKm =
                        if (userLocation != null && event.latitude != null && event.longitude != null) {
                            DistanceUtils.calculateDistanceInKm(
                                userLatitude = userLocation.userLatitude,
                                userLongitude = userLocation.userLongitude,
                                eventLatitude = event.latitude,
                                eventLongitude = event.longitude
                            )
                        } else {
                            null
                        }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AsyncImage(
                            model = event.imageUrl,
                            contentDescription = event.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = primaryRed
                            )

                            Text(
                                text = formatEventDate(
                                    event.dateTime ?: stringResource(R.string.date_not_available)
                                ),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = primaryRed
                            )
                            Text(
                                text = event.location
                                    ?: stringResource(R.string.location_not_available),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        if (distanceInKm != null) {
                            Text(
                                text = String.format(
                                    java.util.Locale.getDefault(), stringResource(R.string.distance_away), distanceInKm
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
                                    val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                                    context.startActivity(mapIntent)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = primaryRed, contentColor = Color.White
                                )
                            ) {
                                Text(stringResource(R.string.open_in_maps))
                            }
                        }
                        HorizontalDivider()
                        Text(
                            text = stringResource(R.string.about_this_event), style = MaterialTheme.typography.titleLarge
                        )
                        event.description?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyLarge,
                                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(stringResource(R.string.no_event_details_available))
                    }
                }
            }
        }

    }
}