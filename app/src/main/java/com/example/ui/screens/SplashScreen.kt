package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Resolve dynamic theme-based colors at Composition time to avoid DrawScope nested @Composable requirements
    val neonLimeColor = NeonLime
    val mutedTextColor = MutedText
    val borderGreyColor = BorderGrey

    var progress by remember { mutableStateOf(0.0f) }
    var bootStageText by remember { mutableStateOf("Initializing system core...") }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 3500, easing = LinearOutSlowInEasing),
        label = "boot_progress"
    )

    // Pulse pulse scaling animation for the brand logo icon
    val infiniteLogoTransition = rememberInfiniteTransition(label = "logo_pulse")
    val logoScale by infiniteLogoTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )

    // Wave phase animation for the cardiac pulse wave line
    val infiniteWaveTransition = rememberInfiniteTransition(label = "wave_phase_transition")
    val wavePhase by infiniteWaveTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    // Trigger sequential loading stages
    LaunchedEffect(Unit) {
        progress = 0.1f
        bootStageText = "Validating secure SQL local state tables..."
        delay(700)
        
        progress = 0.35f
        bootStageText = "Mapping custom bodybuilding exercise definitions..."
        delay(800)
        
        progress = 0.65f
        bootStageText = "Synchronizing hardware wearable telemetry signals..."
        delay(900)
        
        progress = 0.88f
        bootStageText = "Assembling real-time AI metabolic coaching guidelines..."
        delay(700)
        
        progress = 1.0f
        bootStageText = "Command Center active & calibrated."
        delay(500)
        
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF03140A), // Extremely faint dark glow green
                        Color(0xFF0C0E12)  // Deep system background
                    )
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Glow backdrop behind logo
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .scale(logoScale),
                contentAlignment = Alignment.Center
            ) {
                // Subtle glowing background ring
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = neonLimeColor.copy(alpha = 0.08f),
                        radius = size.minDimension / 1.8f
                    )
                }

                // Application brand logo (GT Pulse Shield/Acorn)
                val acornLogo = painterResource(id = R.drawable.img_app_icon_1779292977388)
                Image(
                    painter = acornLogo,
                    contentDescription = "GT Pulse Shield Acorn Logo",
                    modifier = Modifier
                        .size(126.dp)
                        .clip(RoundedCornerShape(32.dp))
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App name & tagline
            Text(
                text = stringResource(id = R.string.app_name).uppercase(),
                fontSize = 28.sp,
                style = MaterialTheme.typography.displayMedium,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                color = neonLimeColor,
                letterSpacing = 2.5.sp
            )

            Text(
                text = "HYPER-INTELLIGENT ATHLETIC DESKTOP OPERATING SYSTEM",
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = mutedTextColor,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Dynamic Live Biometric Cardiac Wave Canvas (GT Pulse Signature)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                // Very subtle ECG Grid lines in background
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val gridStep = 20.dp.toPx()
                    val gridColor = Color(0xFF1B241E)
                    // Draw horizontal grid lines
                    var yTemp = 0f
                    while (yTemp < size.height) {
                        drawLine(gridColor, start = androidx.compose.ui.geometry.Offset(0f, yTemp), end = androidx.compose.ui.geometry.Offset(size.width, yTemp), strokeWidth = 0.5.dp.toPx())
                        yTemp += gridStep
                    }
                    // Draw vertical grid lines
                    var xTemp = 0f
                    while (xTemp < size.width) {
                        drawLine(gridColor, start = androidx.compose.ui.geometry.Offset(xTemp, 0f), end = androidx.compose.ui.geometry.Offset(xTemp, size.height), strokeWidth = 0.5.dp.toPx())
                        xTemp += gridStep
                    }
                }

                // Glowing animated ECG line
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val centerY = height / 2f
                    val path = Path()
                    path.moveTo(0f, centerY)

                    for (x in 0..width.toInt() step 4) {
                        val xProgress = x / width
                        // Dampen wave at edges using a smooth Gaussian window to create clean start/end flat lines
                        val waveWindow = Math.exp(-Math.pow((xProgress - 0.5).toDouble() / 0.15, 2.0)).toFloat()
                        
                        // ECG pulse wave shape modeled by combinations of sinusoids for custom athletic beats
                        val baseSine = Math.sin((xProgress * 15f - wavePhase).toDouble()).toFloat()
                        val highFreqInMiddle = Math.sin((xProgress * 45f + wavePhase * 1.5f).toDouble()).toFloat() * 0.4f
                        
                        val finalY = centerY + (baseSine + highFreqInMiddle) * 20.dp.toPx() * waveWindow
                        path.lineTo(x.toFloat(), finalY)
                    }

                    drawPath(
                        path = path,
                        color = neonLimeColor,
                        style = Stroke(
                            width = 2.5.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Tech diagnostic loader display (Percentage + Load Stage)
            Row(
                modifier = Modifier
                    .width(280.dp)
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = bootStageText.uppercase(),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = mutedTextColor,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    color = neonLimeColor,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Neo command progress bar
            Box(
                modifier = Modifier
                    .width(280.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF121B16))
                    .border(0.5.dp, borderGreyColor.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    neonLimeColor.copy(alpha = 0.4f),
                                    neonLimeColor
                                )
                            )
                        )
                )
            }
        }
    }
}
