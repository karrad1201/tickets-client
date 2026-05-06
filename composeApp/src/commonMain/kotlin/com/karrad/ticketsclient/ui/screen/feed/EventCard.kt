package com.karrad.ticketsclient.ui.screen.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.crash.CrashReporter
import com.karrad.ticketsclient.data.api.dto.EventDto
import com.karrad.ticketsclient.di.AppContainer
import com.karrad.ticketsclient.ui.component.EventImage
import com.karrad.ticketsclient.ui.util.formatPrice
import kotlinx.coroutines.launch

// cardWidth = null → заполняет пространство пейджера; иначе — фиксированная ширина
@Composable
internal fun EventCard(
    event: EventDto,
    cardWidth: Dp?,
    imageHeight: Dp = 165.dp,
    onClick: () -> Unit
) {
    val widthMod = if (cardWidth != null) Modifier.width(cardWidth) else Modifier.fillMaxWidth()
    var isFav by remember { mutableStateOf(AppSession.isFavorite(event.id)) }
    val scope = rememberCoroutineScope()

    Column(modifier = widthMod.clickable { onClick() }) {
        Box(
            modifier = Modifier
                .then(widthMod)
                .height(imageHeight)
                .clip(RoundedCornerShape(14.dp))
        ) {
            EventImage(imageUrl = event.imageUrl, seed = event.id, modifier = Modifier.fillMaxWidth().height(imageHeight))

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.45f))
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
                Text(
                    text = event.ageRating ?: "0+",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.35f))
                    .clickable {
                        val newFav = !isFav
                        isFav = newFav
                        AppSession.toggleFavorite(event.id, newFav)
                        scope.launch {
                            runCatching {
                                if (newFav) AppContainer.favoriteService.add(event.id)
                                else AppContainer.favoriteService.remove(event.id)
                            }.onFailure {
                                CrashReporter.log(it)
                                isFav = !newFav
                                AppSession.toggleFavorite(event.id, !newFav)
                            }
                        }
                    }
            ) {
                Icon(
                    if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isFav) "Убрать из избранного" else "В избранное",
                    tint = if (isFav) Color(0xFFFF4D6D) else Color.White,
                    modifier = Modifier.size(15.dp).align(Alignment.Center)
                )
                Text(
                    "+",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 9.sp,
                    modifier = Modifier.align(Alignment.TopEnd).padding(top = 3.dp, end = 3.dp)
                )
            }

            event.minPrice?.let { price ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.72f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "от ${price.formatPrice()} ₽",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = event.label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            ),
            color = Color(0xFF1C1C1E),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 15.sp
        )
        Text(
            text = event.venueLabel ?: event.venueId.venueShort(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

internal fun String.venueShort(): String = when (this) {
    "venue-bolshoi"  -> "Большой театр"
    "venue-arena"    -> "Арена"
    "venue-cinema"   -> "Кинотеатр Октябрь"
    "venue-club"     -> "Известия Hall"
    "venue-museum"   -> "Музей совр. искусства"
    "venue-theater"  -> "Театр на Таганке"
    else             -> this
}
