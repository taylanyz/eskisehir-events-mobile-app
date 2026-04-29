package com.eskisehir.eventapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.eskisehir.eventapp.data.model.WeatherDto

/**
 * Weather card component displaying current weather conditions.
 * Shows temperature, condition, humidity, and wind speed.
 */
@Composable
fun WeatherCard(weather: WeatherDto?) {
    if (weather == null) return

    val weatherIcon = when {
        weather.isRaining -> Icons.Default.CloudUpload
        weather.condition.contains("Sunny") -> Icons.Default.WbSunny
        else -> Icons.Default.Cloud
    }

    val weatherColor = when {
        weather.isRaining -> MaterialTheme.colorScheme.onErrorContainer
        weather.temperature > 30 -> MaterialTheme.colorScheme.error
        weather.temperature < 10 -> MaterialTheme.colorScheme.inversePrimary
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = weatherIcon,
                    contentDescription = weather.condition,
                    modifier = Modifier.size(32.dp),
                    tint = weatherColor
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = weather.condition,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${weather.temperature}°C",
                        style = MaterialTheme.typography.titleMedium,
                        color = weatherColor
                    )
                }
            }

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherMetric(
                    label = "Humidity",
                    value = "${weather.humidity}%",
                    modifier = Modifier.weight(1f)
                )
                WeatherMetric(
                    label = "Wind",
                    value = "${String.format("%.1f", weather.windSpeed)} m/s",
                    modifier = Modifier.weight(1f)
                )
                if (weather.isRaining) {
                    WeatherMetric(
                        label = "Raining",
                        value = "Yes",
                        modifier = Modifier.weight(1f),
                        isAlert = true
                    )
                }
            }
        }
    }
}

/**
 * Helper composable for displaying weather metrics.
 */
@Composable
private fun WeatherMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isAlert: Boolean = false
) {
    Column(
        modifier = modifier
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            color = if (isAlert) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
    }
}
