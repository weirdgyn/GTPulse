package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.data.model.Badge
import com.example.data.model.TrainingLog
import com.example.data.model.UserProfile
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val workoutLogs by viewModel.workoutLogs.collectAsStateWithLifecycle()
    val badges by viewModel.badges.collectAsStateWithLifecycle()
    val wearableState by viewModel.wearableSyncState.collectAsStateWithLifecycle()
    val notificationState by viewModel.notificationsStatus.collectAsStateWithLifecycle()

    var isEditingProfile by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val isDevMode by viewModel.isDevMode.collectAsStateWithLifecycle()

    if (showSettingsSheet) {
        AlertDialog(
            onDismissRequest = { showSettingsSheet = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Theme palette",
                        tint = NeonLime
                    )
                    Text(
                        text = "Customize Visual Vibe",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = WarmText
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Choose your preferred interface skin style for GT Pulse Command Center:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    val themeOptions = listOf(
                        Triple("SYSTEM", "System Intelligence Default", Icons.Default.Settings),
                        Triple("DARK", "Dark Command Slate Theme", Icons.Default.Brightness2),
                        Triple("LIGHT", "Pristine Radiant Light Theme", Icons.Default.Brightness5)
                    )

                    themeOptions.forEach { (mode, titleText, icon) ->
                        val isSelected = themeMode == mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) NeonLime.copy(alpha = 0.15f) else Color.Transparent)
                                .border(
                                    border = BorderStroke(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) NeonLime else BorderGrey
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.setThemeMode(mode) }
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                                .testTag("theme_option_$mode"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = mode,
                                    tint = if (isSelected) NeonLime else MutedText,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = titleText,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (isSelected) NeonLime else WarmText
                                )
                            }
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.setThemeMode(mode) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = NeonLime,
                                    unselectedColor = MutedText
                                )
                            )
                        }
                    }

                    HorizontalDivider(color = BorderGrey, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text(
                                text = "Dev. mode",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarmText
                            )
                            Text(
                                text = "Show Developer Seed & Wearable Sync tools in Dashboard",
                                fontSize = 13.sp,
                                color = MutedText
                            )
                        }
                        Switch(
                            checked = isDevMode,
                            onCheckedChange = { viewModel.setDevMode(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = NeonLime,
                                uncheckedThumbColor = WarmText,
                                uncheckedTrackColor = SurfaceCharcoal
                            ),
                            modifier = Modifier.testTag("dev_mode_switch")
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSettingsSheet = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonLime,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("settings_done_button")
                ) {
                    Text(
                        text = "Apply Changes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            },
            containerColor = SurfaceCharcoal,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkCharcoal)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- HEADER TILE ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.app_name).uppercase(),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeonLime,
                        letterSpacing = 1.5.sp
                    )
                )
                Text(
                    text = "Pro Bodybuilding Command Center",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { showSettingsSheet = true },
                    modifier = Modifier
                        .background(SurfaceCharcoal, RoundedCornerShape(10.dp))
                        .border(1.dp, BorderGrey, RoundedCornerShape(10.dp))
                        .testTag("settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings Option",
                        tint = NeonLime
                    )
                }

                IconButton(
                    onClick = { viewModel.simulatePushNotification() },
                    modifier = Modifier
                        .background(SurfaceCharcoal, RoundedCornerShape(10.dp))
                        .border(1.dp, BorderGrey, RoundedCornerShape(10.dp))
                        .testTag("push_bell_button")
                ) {
                    Icon(
                        imageVector = if (notificationState) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                        contentDescription = "Alert test",
                        tint = if (notificationState) NeonLime else MutedText
                    )
                }
            }
        }

        // --- ATHLETE PROFILE PANEL (Phase 1) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, if (isEditingProfile) NeonLime else BorderGrey, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = NeonLime,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${userProfile.athleteName} - " + stringResource(R.string.athlete_profile),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = WarmText
                        )
                    }

                    IconButton(
                        onClick = { isEditingProfile = !isEditingProfile },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("edit_profile_toggle")
                    ) {
                        Icon(
                            imageVector = if (isEditingProfile) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = if (isEditingProfile) "Cancel Edit" else "Edit Stats",
                            tint = if (isEditingProfile) AccentOrange else NeonLime,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isEditingProfile) {
                    ProfileEditForm(
                        initialProfile = userProfile,
                        onSave = { updated ->
                            viewModel.updateProfile(
                                name = updated.athleteName,
                                age = updated.age,
                                weight = updated.weightKg,
                                height = updated.heightCm,
                                gender = updated.gender,
                                goal = updated.fitnessGoal,
                                level = updated.trainingLevel,
                                site = updated.defaultTrainingSite
                            )
                            isEditingProfile = false
                        }
                    )
                } else {
                    ProfileDisplayView(profile = userProfile)
                }
            }
        }

        // --- SCHEDULED WORKOUT TARGET BOARD (Requirement 3) ---
        val sessionsList by viewModel.sessionsList.collectAsStateWithLifecycle()
        val currentDayName = remember { SimpleDateFormat("EEEE", java.util.Locale.getDefault()).format(java.util.Date()) }
        val matchedSession = remember(sessionsList, currentDayName) {
            if (sessionsList.isEmpty()) {
                null
            } else {
                val dayMatch = sessionsList.find { it.dayOfWeek.trim().equals(currentDayName, ignoreCase = true) }
                if (dayMatch != null) {
                    dayMatch to "Scheduled for today (${currentDayName})"
                } else {
                    val sorted = sessionsList.sortedBy { it.sequenceNumber }
                    sorted.first() to "Next routine in your sequence"
                }
            }
        }

        Text(
            text = "Today's Scheduled Track",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = WarmText
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, if (matchedSession != null) NeonLime.copy(alpha = 0.5f) else BorderGrey, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (matchedSession == null) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventNote,
                            contentDescription = "No session",
                            tint = MutedText,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No Workout Plans Configured",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = WarmText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Navigate to the Scheduler tab to define a workout routine (e.g. Monday Push Day) with targeted sets and weight thresholds.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp,
                            color = MutedText,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            lineHeight = 18.sp
                        )
                    }
                } else {
                    val session = matchedSession.first
                    val infoText = matchedSession.second
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeonLime.copy(alpha = 0.15f))
                                    .padding(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FitnessCenter,
                                    contentDescription = "Workout run",
                                    tint = NeonLime,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = session.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = WarmText
                                )
                                Text(
                                    text = infoText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonLime,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AccentCyan.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Seq #${session.sequenceNumber}",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(DarkCharcoal)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Day: ${session.dayOfWeek}",
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmText
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(DarkCharcoal)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Split: ${session.targetSplit}",
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmText
                            )
                        }
                    }

                    if (session.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Coach notes: ${session.notes}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.startActiveWorkout(session) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dashboard_start_workout_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Start", tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Start Training Session", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- SEED SAMPLE DATA CALLOUT (IF NO HISTORICAL LOGS) ---
        if (workoutLogs.isEmpty() && isDevMode) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceCharcoal, RoundedCornerShape(16.dp))
                    .border(1.dp, AccentOrange, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No recorded logs in database yet",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AccentOrange
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.seed_description),
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp,
                        color = MutedText,
                        lineHeight = 18.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.seedHistoricalPerformanceData() },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("seed_database_button")
                    ) {
                        Icon(imageVector = Icons.Default.Storage, contentDescription = "Seed")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = stringResource(R.string.seed_button), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- DASHBOARD ANALYTICS COUNTERS ---
        Text(
            text = "Anatomical Metrics",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = WarmText
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricsCard(
                title = "Logged Sessions",
                value = "${workoutLogs.size}",
                icon = Icons.Default.FitnessCenter,
                tint = NeonLime,
                modifier = Modifier.weight(1f)
            )
            MetricsCard(
                title = "Last Bodyweight",
                value = "${userProfile.weightKg} kg",
                icon = Icons.Default.MonitorWeight,
                tint = AccentCyan,
                modifier = Modifier.weight(1f)
            )
        }

        // --- NATIVE PROGRESS GRAPH (Phase 3 analytics) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderGrey, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.milestone_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = WarmText
                        )
                        Text(
                            text = stringResource(R.string.milestone_subtitle),
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedText
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = "Trending",
                        tint = AccentCyan
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (workoutLogs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .background(DarkCharcoal, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.awaiting_records),
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText
                        )
                    }
                } else {
                    // Render custom Compose Canvas Graph!
                    BodyweightProgressCurve(logs = workoutLogs.reversed())
                }
            }
        }

        // --- MOTIVATION BADGES (Phase 3 motivational features) ---
        Text(
            text = stringResource(R.string.unlocked_badges),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = WarmText
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(badges) { badge ->
                BadgeItemTile(badge = badge)
            }
        }

        // --- HARDWARE WEARABLE INTEGRATION PANEL ---
        if (isDevMode) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGrey, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Watch,
                                contentDescription = "Wearable",
                                tint = AccentCyan,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = stringResource(R.string.wearable_title),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = WarmText
                                )
                                Text(
                                    text = stringResource(R.string.wearable_subtitle),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MutedText
                                )
                            }
                        }

                        val badgeColor = if (wearableState.contains("Active")) NeonLime else AccentOrange
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(badgeColor.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (wearableState.contains("Connected")) "ONLINE" else "DISCONNECTED",
                                color = badgeColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkCharcoal, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = stringResource(R.string.sync_device_status), style = MaterialTheme.typography.bodySmall, color = MutedText)
                                Text(
                                    text = wearableState,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (wearableState.contains("Active")) NeonLime else WarmText
                                )
                            }
                            if (wearableState.contains("Active")) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = stringResource(R.string.captured_metrics), style = MaterialTheme.typography.bodySmall, color = MutedText)
                                    Text(
                                        text = "Avg Heart rate: 135 bpm | 420 active kcal",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AccentCyan
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.syncWithWearableDevice() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sync_wearable_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = if (wearableState.contains("Active")) SurfaceCharcoal else AccentCyan),
                        border = if (wearableState.contains("Active")) BorderStroke(1.dp, BorderGrey) else null,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Sync icon",
                            tint = if (wearableState.contains("Active")) MutedText else Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (wearableState.contains("Active")) "SYNC" else "PAIR WEARABLE",
                            color = if (wearableState.contains("Active")) WarmText else Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // --- PRIVACY & COMPLIANCE NOTES ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = "Security",
                tint = NeonLime.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.privacy_rules_compliant),
                style = MaterialTheme.typography.labelSmall,
                color = MutedText.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun MetricsCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(SurfaceCharcoal, RoundedCornerShape(12.dp))
            .border(1.dp, BorderGrey, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, fontSize = 11.sp, color = MutedText, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 20.sp, color = WarmText, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun ProfileDisplayView(profile: UserProfile) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ProfileStatLabel(title = "Age", value = "${profile.age} years")
            ProfileStatLabel(title = "Weight", value = "${profile.weightKg} kg")
            ProfileStatLabel(title = "Height", value = "${profile.heightCm} cm")
        }

        HorizontalDivider(color = BorderGrey, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 4.dp))

        ProfileStatLabelDetail(title = "Athlete Name", value = profile.athleteName, icon = Icons.Default.Person)
        ProfileStatLabelDetail(title = "Target Goal", value = profile.fitnessGoal, icon = Icons.Default.EmojiEvents)
        ProfileStatLabelDetail(title = "Experience Level", value = profile.trainingLevel, icon = Icons.Default.MilitaryTech)
        ProfileStatLabelDetail(title = "Primary Training Site", value = profile.defaultTrainingSite, icon = Icons.Default.Storefront)
    }
}

@Composable
fun ProfileStatLabel(title: String, value: String) {
    Column {
        Text(text = title, fontSize = 11.sp, color = MutedText)
        Text(text = value, fontSize = 15.sp, color = WarmText, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ProfileStatLabelDetail(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MutedText,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "$title: ", fontSize = 13.sp, color = MutedText)
        Text(text = value, fontSize = 13.sp, color = WarmText, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileEditForm(
    initialProfile: UserProfile,
    onSave: (UserProfile) -> Unit
) {
    var athleteName by remember { mutableStateOf(initialProfile.athleteName) }
    var age by remember { mutableStateOf(initialProfile.age.toString()) }
    var weight by remember { mutableStateOf(initialProfile.weightKg.toString()) }
    var height by remember { mutableStateOf(initialProfile.heightCm.toString()) }
    var gender by remember { mutableStateOf(initialProfile.gender) }
    var fitnessGoal by remember { mutableStateOf(initialProfile.fitnessGoal) }
    var trainingLevel by remember { mutableStateOf(initialProfile.trainingLevel) }
    var defaultSite by remember { mutableStateOf(initialProfile.defaultTrainingSite) }

    val focusManager = LocalFocusManager.current

    val goalsList = listOf(
        "Hypertrophy (Building Muscle)",
        "Raw Strength Builder",
        "Cutting (Fat Loss / Definition)",
        "Endurance & Conditioning"
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = athleteName,
            onValueChange = { athleteName = it },
            label = { Text("Athlete Name") },
            modifier = Modifier.fillMaxWidth().testTag("profile_name_input"),
            colors = TextFieldDefaults.colors(
                focusedTextColor = WarmText,
                unfocusedTextColor = WarmText
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f).testTag("profile_age_input"),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = WarmText,
                    unfocusedTextColor = WarmText
                )
            )
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f).testTag("profile_weight_input"),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = WarmText,
                    unfocusedTextColor = WarmText
                )
            )
            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                label = { Text("Height (cm)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f).testTag("profile_height_input"),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = WarmText,
                    unfocusedTextColor = WarmText
                )
            )
        }

        OutlinedTextField(
            value = defaultSite,
            onValueChange = { defaultSite = it },
            label = { Text("Preferred Training Split Location") },
            modifier = Modifier.fillMaxWidth().testTag("profile_site_input"),
            colors = TextFieldDefaults.colors(
                focusedTextColor = WarmText,
                unfocusedTextColor = WarmText
            )
        )

        Text(text = "Physical Target Objective:", fontSize = 12.sp, color = MutedText, fontWeight = FontWeight.Bold)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            goalsList.forEach { g ->
                val isSelected = fitnessGoal == g
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) NeonLime else SurfaceCharcoal)
                        .border(1.dp, if (isSelected) NeonLime else BorderGrey, RoundedCornerShape(8.dp))
                        .clickable { fitnessGoal = g }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = g,
                        color = if (isSelected) Color.Black else WarmText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Text(text = "Biomechanical Stage Level:", fontSize = 12.sp, color = MutedText, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Beginner", "Intermediate", "Advanced").forEach { level ->
                val isSelected = trainingLevel == level
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) AccentCyan else SurfaceCharcoal)
                        .border(1.dp, if (isSelected) AccentCyan else BorderGrey, RoundedCornerShape(8.dp))
                        .clickable { trainingLevel = level }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = level,
                        color = if (isSelected) Color.Black else WarmText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = {
                focusManager.clearFocus()
                val parsedAge = age.toIntOrNull() ?: 25
                val parsedWeight = weight.toDoubleOrNull() ?: 75.0
                val parsedHeight = height.toDoubleOrNull() ?: 175.0
                onSave(
                    UserProfile(
                        id = 1,
                        athleteName = athleteName,
                        age = parsedAge,
                        weightKg = parsedWeight,
                        heightCm = parsedHeight,
                        gender = gender,
                        fitnessGoal = fitnessGoal,
                        trainingLevel = trainingLevel,
                        defaultTrainingSite = defaultSite
                    )
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("save_profile_button")
        ) {
            Icon(imageVector = Icons.Default.Save, contentDescription = "Save Profile", tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "SAVE PROFILE", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun BadgeItemTile(badge: Badge) {
    Box(
        modifier = Modifier
            .width(150.dp)
            .background(SurfaceCharcoal, RoundedCornerShape(12.dp))
            .border(
                1.dp,
                if (badge.isUnlocked) NeonLime.copy(alpha = 0.5f) else BorderGrey,
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(if (badge.isUnlocked) NeonLime.copy(alpha = 0.15f) else BorderGrey.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (badge.iconResource) {
                        "person" -> Icons.Default.Check
                        "fitness_center" -> Icons.Default.FitnessCenter
                        "insights" -> Icons.Default.Leaderboard
                        "emoji_events" -> Icons.Default.EmojiEvents
                        else -> Icons.Default.Bolt
                    },
                    contentDescription = null,
                    tint = if (badge.isUnlocked) NeonLime else MutedText,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = badge.title,
                fontSize = 14.sp,
                color = if (badge.isUnlocked) Color.White else MutedText,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = badge.description,
                fontSize = 12.sp,
                color = MutedText,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 15.sp,
                maxLines = 2,
                modifier = Modifier.height(32.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (badge.isUnlocked) "UNLOCKED" else "LOCKED",
                fontSize = 10.sp,
                color = if (badge.isUnlocked) NeonLime else MutedText.copy(alpha = 0.5f),
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun BodyweightProgressCurve(logs: List<TrainingLog>) {
    val localBorderGrey = BorderGrey
    val localAccentCyan = AccentCyan
    val localDarkCharcoal = DarkCharcoal
    val localNeonLime = NeonLime

    // Elegant Canvas line draw
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .background(localDarkCharcoal, RoundedCornerShape(8.dp))
    ) {
        val width = size.width
        val height = size.height

        val paddingX = 40f
        val paddingY = 30f

        val pointsCount = logs.size
        if (pointsCount < 2) return@Canvas

        val maxWeight = logs.maxOf { it.athleteWeightKg }
        val minWeight = logs.minOf { it.athleteWeightKg }
        val weightRange = if (maxWeight == minWeight) 10.0 else (maxWeight - minWeight)

        val chartWidth = width - (paddingX * 2)
        val chartHeight = height - (paddingY * 2)

        val xSteps = chartWidth / (pointsCount - 1)

        val path = Path()
        val fillPath = Path()

        // Gather coordinates
        val points = logs.mapIndexed { index, log ->
            val x = paddingX + (index * xSteps)
            val normalizedY = (log.athleteWeightKg - minWeight) / weightRange
            // Invert coordinate because 0,0 is top-left
            val y = paddingY + (chartHeight - (normalizedY.toFloat() * chartHeight))
            Offset(x, y)
        }

        // Draw grids & guideline horizontal markers
        drawLine(
            color = localBorderGrey,
            start = Offset(paddingX, paddingY),
            end = Offset(width - paddingX, paddingY),
            strokeWidth = 1f
        )
        drawLine(
            color = localBorderGrey,
            start = Offset(paddingX, paddingY + (chartHeight / 2)),
            end = Offset(width - paddingX, paddingY + (chartHeight / 2)),
            strokeWidth = 1f
        )
        drawLine(
            color = localBorderGrey,
            start = Offset(paddingX, paddingY + chartHeight),
            end = Offset(width - paddingX, paddingY + chartHeight),
            strokeWidth = 1.5f
        )

        // Plot points & curve path
        path.moveTo(points[0].x, points[0].y)
        fillPath.moveTo(points[0].x, points[0].y)

        for (i in 1 until points.size) {
            val p0 = points[i - 1]
            val p1 = points[i]
            // Cubic bezier control points for beautiful smooth curves
            val controlX = (p0.x + p1.x) / 2
            path.cubicTo(
                controlX, p0.y,
                controlX, p1.y,
                p1.x, p1.y
            )
            fillPath.cubicTo(
                controlX, p0.y,
                controlX, p1.y,
                p1.x, p1.y
            )
        }

        // Draw visual glow area under curve
        fillPath.lineTo(points.last().x, paddingY + chartHeight)
        fillPath.lineTo(points.first().x, paddingY + chartHeight)
        fillPath.close()

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(localAccentCyan.copy(alpha = 0.25f), Color.Transparent),
                startY = paddingY,
                endY = paddingY + chartHeight
            )
        )

        drawPath(
            path = path,
            color = localAccentCyan,
            style = Stroke(width = 5f)
        )

        // Draw physical point anchor rings
        points.forEach { point ->
            drawCircle(
                color = localDarkCharcoal,
                radius = 11f,
                center = point
            )
            drawCircle(
                color = localNeonLime,
                radius = 7f,
                center = point
            )
        }
    }
}
