package com.example.whitenoises.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Cottage
import androidx.compose.material.icons.rounded.Dry
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.Sailing
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.SurroundSound
import androidx.compose.material.icons.rounded.Thunderstorm
import androidx.compose.material.icons.rounded.Train
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.Waves
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.whitenoises.model.SoundItem

@Composable
fun SoundCard(
    soundItem: SoundItem,
    isCurrentSound: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeBorderColor by animateColorAsState(
        targetValue = if (isCurrentSound) {
            if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        label = "borderColor"
    )

    val cardContainerColor by animateColorAsState(
        targetValue = if (isCurrentSound) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        },
        label = "containerColor"
    )

    val icon = getSoundIcon(soundItem.iconName)

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardContainerColor),
        border = BorderStroke(
            width = if (isCurrentSound) 2.dp else 1.dp,
            color = activeBorderColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCurrentSound) 4.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Tag
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Text(
                        text = soundItem.category.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                if (isCurrentSound && isPlaying) {
                    AnimatedEqualizerIndicator()
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isCurrentSound) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isCurrentSound) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = soundItem.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = soundItem.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                FilledIconButton(
                    onClick = onClick,
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (isCurrentSound && isPlaying) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        contentColor = if (isCurrentSound && isPlaying) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    ),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (isCurrentSound && isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (isCurrentSound && isPlaying) "Pause ${soundItem.title}" else "Play ${soundItem.title}"
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedEqualizerIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(14.dp)
                .scale(scaleY = scale, scaleX = 1f)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(18.dp)
                .scale(scaleY = 2f - scale, scaleX = 1f)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(12.dp)
                .scale(scaleY = scale, scaleX = 1f)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )
    }
}

private fun getSoundIcon(iconName: String): ImageVector {
    return when (iconName) {
        "GraphicEq" -> Icons.Rounded.GraphicEq
        "Equalizer" -> Icons.Rounded.Equalizer
        "SurroundSound" -> Icons.Rounded.SurroundSound
        "WaterDrop" -> Icons.Rounded.WaterDrop
        "Sailing" -> Icons.Rounded.Sailing
        "Air" -> Icons.Rounded.Air
        "LocalFireDepartment" -> Icons.Rounded.LocalFireDepartment
        "Waves" -> Icons.Rounded.Waves
        "Favorite" -> Icons.Rounded.Favorite
        "Dry" -> Icons.Rounded.Dry
        "MusicNote" -> Icons.Rounded.MusicNote
        "MonitorHeart" -> Icons.Rounded.MonitorHeart
        "AcUnit" -> Icons.Rounded.AcUnit
        "Bedtime" -> Icons.Rounded.Bedtime
        "Cottage" -> Icons.Rounded.Cottage
        "Flight" -> Icons.Rounded.Flight
        "Home" -> Icons.Rounded.Home
        "RecordVoiceOver" -> Icons.Rounded.RecordVoiceOver
        "Schedule" -> Icons.Rounded.Schedule
        "Thunderstorm" -> Icons.Rounded.Thunderstorm
        "Train" -> Icons.Rounded.Train
        else -> Icons.Rounded.GraphicEq
    }
}
