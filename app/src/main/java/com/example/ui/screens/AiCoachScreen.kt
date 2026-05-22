package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AiCoachScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val logs by viewModel.workoutLogs.collectAsStateWithLifecycle()
    val recommendation by viewModel.recommendationText.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGeneratingRecommendation.collectAsStateWithLifecycle()
    val error by viewModel.recommendationError.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkCharcoal)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Coach Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AI SMART ADVISEMENT",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = NeonLime,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = "Personalized coaching and adjustments",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText
                )
            }
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "AI Action",
                tint = NeonLime,
                modifier = Modifier.size(24.dp)
            )
        }

        // ATHLETE CURRENT TELEMETRY CONSOLE
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderGrey, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Telemetry Evaluated by AI Coach:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    CoachTelemetryChip(label = "Objective", valStr = userProfile.fitnessGoal.substringBefore(" ("))
                    CoachTelemetryChip(label = "Mass", valStr = "${userProfile.weightKg} kg")
                    CoachTelemetryChip(label = "Level", valStr = userProfile.trainingLevel)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    CoachTelemetryChip(label = "Anatomical split", valStr = userProfile.defaultTrainingSite)
                    CoachTelemetryChip(label = "Logs", valStr = "${logs.size} sessions")
                }
            }
        }

        // TRIGGER COACH ADVICE ACTION BUTTON
        Button(
            onClick = { viewModel.generateAIAdvice() },
            enabled = !isGenerating,
            colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("generate_ai_advice_button")
        ) {
            if (isGenerating) {
                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Analyzing biometric trends...", color = Color.Black, fontWeight = FontWeight.Bold)
            } else {
                Icon(imageVector = Icons.Default.Bolt, contentDescription = "Flash", tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (recommendation.isNotBlank()) "Re-Analyze Biometrics & Logs" else "Run AI Coaching Biometrics Diagnostics",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // COMPLIANCE CAUTION BAR - OPTION B PROTOTYPE NOTICE TO PREVENT SEALS AUDITS
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AccentOrange.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                .border(1.dp, AccentOrange.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "Tip", tint = AccentOrange, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Prototyping Notice: Local biometric analytics fallback deployed. API keys are managed in user secrets securely.",
                    fontSize = 12.sp,
                    color = AccentOrange,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // RENDERING BOX
        if (recommendation.isNotBlank() || error != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (error != null) AccentOrange.copy(alpha = 0.5f) else BorderGrey,
                        RoundedCornerShape(16.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (error != null) "Diagnostics Connection Error" else "Coach AI Biomechanics Directive",
                            style = MaterialTheme.typography.titleSmall,
                            color = if (error != null) AccentOrange else NeonLime,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = if (error != null) Icons.Default.WifiOff else Icons.Default.Lightbulb,
                            contentDescription = "Coaching sign",
                            tint = if (error != null) AccentOrange else NeonLime,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (error != null) {
                        Text(
                            text = error!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = AccentOrange,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = BorderGrey, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Switching automatically to local offline biomechanics recommendation blueprint...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedText,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Render Markdown content clearly on UI
                    InteractiveMarkdownRenderer(markdown = recommendation)
                }
            }
        } else {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(SurfaceCharcoal, RoundedCornerShape(16.dp))
                    .border(1.dp, BorderGrey, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Awaiting prompt",
                        tint = MutedText.copy(alpha = 0.5f),
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Awaiting Coaching Analysis Request",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = WarmText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap the analysis button to send your physical statistics and recent gym history to Coach GT Pulse. The AI will evaluate sets, RPE fatigue levels, and goals to compile a nutrition & programming action plan.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp,
                        color = MutedText,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp)) // padding
    }
}

@Composable
fun CoachTelemetryChip(label: String, valStr: String) {
    Box(
        modifier = Modifier
            .background(DarkCharcoal, RoundedCornerShape(8.dp))
            .border(1.dp, BorderGrey, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Column {
            Text(text = label, fontSize = 11.sp, color = MutedText)
            Text(text = valStr, fontSize = 13.sp, color = WarmText, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun InteractiveMarkdownRenderer(markdown: String) {
    val lines = markdown.split("\n")
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        lines.forEach { line ->
            val trimmed = line.trim()
            when {
                trimmed.startsWith("###") -> {
                    Text(
                        text = trimmed.replace("###", "").trim(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = AccentCyan,
                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                    )
                }
                trimmed.startsWith("####") -> {
                    Text(
                        text = trimmed.replace("####", "").trim(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonLime,
                        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                    )
                }
                trimmed.startsWith("-") || trimmed.startsWith("*") -> {
                    val bulletText = trimmed.substring(1).trim()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, top = 2.dp, bottom = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(text = "•", fontSize = 14.sp, color = NeonLime, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = bulletText,
                            fontSize = 14.sp,
                            color = WarmText,
                            lineHeight = 19.sp
                        )
                    }
                }
                trimmed.isNotBlank() -> {
                    Text(
                        text = trimmed,
                        fontSize = 14.sp,
                        color = WarmText,
                        lineHeight = 19.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}
