package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.HistoricalLocation
import com.example.ui.theme.JoseonGold
import com.example.ui.theme.JoseonGoldDark
import com.example.ui.theme.RoyalCrimson

@Composable
fun GoogleMapView(
    location: HistoricalLocation,
    modifier: Modifier = Modifier,
    showControls: Boolean = true,
    onCloseMap: (() -> Unit)? = null,
    onShareLocation: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var isMapLoaded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.5.dp, JoseonGold, RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Embedded Map View via WebView with Google Maps JS/Embed
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            isMapLoaded = true
                        }
                    }
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    loadUrl(location.getGoogleMapsEmbedUrl())
                }
            },
            update = { webView ->
                webView.loadUrl(location.getGoogleMapsEmbedUrl())
            },
            modifier = Modifier.fillMaxSize()
        )

        // Close Button
        if (onCloseMap != null) {
            IconButton(
                onClick = onCloseMap,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(20.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "닫기",
                    tint = Color.White
                )
            }
        }

        // Location Info Bottom Banner overlay
        if (showControls) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(RoyalCrimson, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = JoseonGold,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = location.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = location.region,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (location.historicalSignificance.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = location.historicalSignificance,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(location.getGeoUri()))
                                intent.setPackage("com.google.android.apps.maps")
                                if (intent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(intent)
                                } else {
                                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(location.getGoogleMapsUrl()))
                                    context.startActivity(webIntent)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RoyalCrimson,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("구글맵 열기", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        if (onShareLocation != null) {
                            OutlinedButton(
                                onClick = onShareLocation,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("위치 공유", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
