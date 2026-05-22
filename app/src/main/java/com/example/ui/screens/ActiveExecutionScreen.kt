package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SessionExercise
import com.example.data.model.TrainingLog
import com.example.data.model.WorkoutSetLog
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActiveExecutionScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeSession by viewModel.activeSession.collectAsStateWithLifecycle()
    val activeExercises by viewModel.activeExercises.collectAsStateWithLifecycle()
    val activeSetLogs by viewModel.activeSetLogs.collectAsStateWithLifecycle()
    val workoutLogs by viewModel.workoutLogs.collectAsStateWithLifecycle()

    val focusManager = LocalFocusManager.current

    // Observe sharing text triggers from viewmodel
    val sharedText by viewModel.sharedContentText.collectAsStateWithLifecycle()
    LaunchedEffect(sharedText) {
        if (sharedText != null) {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, sharedText)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Share GT Pulse Progress Card")
            context.startActivity(shareIntent)
            viewModel.clearSharedIntent()
        }
    }

    if (activeSession != null) {
        // ACTIVE WORKOUT WRITER SCREEN
        ActiveTrainingConsole(
            sessionName = activeSession!!.name,
            exercises = activeExercises,
            setLogs = activeSetLogs,
            onUpdateSet = { exId, setIdx, wt, reps, rpe, completed ->
                viewModel.updateSetLog(exId, setIdx, wt, reps, rpe, completed)
            },
            onSave = {
                focusManager.clearFocus()
                viewModel.finishAndSaveWorkout()
            },
            onDiscard = {
                viewModel.discardWorkout()
            },
            viewModel = viewModel
        )
    } else {
        // WORKOUT FEEDBACK LOGS VIEWER
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(DarkCharcoal)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Text(
                    text = "TRAINING LOGS & PROGRESS",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = NeonLime,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = "Historical records of completed gym sessions",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText
                )
            }

            if (workoutLogs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(SurfaceCharcoal, RoundedCornerShape(12.dp))
                        .border(1.dp, BorderGrey, RoundedCornerShape(12.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Empty execution",
                            tint = MutedText,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Training Logs Present",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = WarmText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Navigate to the 'Plan' tab, launch one of your routine schedules, and tap complete to record interactive statistics.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(workoutLogs) { log ->
                        HistoricLogCardTile(
                            log = log,
                            onShare = { viewModel.shareLogExternal(log) },
                            onDelete = { viewModel.deleteLog(log) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActiveTrainingConsole(
    sessionName: String,
    exercises: List<SessionExercise>,
    setLogs: Map<String, WorkoutSetLog>,
    onUpdateSet: (Long, Int, Double, Int, Int, Boolean) -> Unit,
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    viewModel: MainViewModel
) {
    val scrollState = rememberScrollState()

    val satisfaction by viewModel.feedbackSatisfaction.collectAsStateWithLifecycle()
    val rpeValue by viewModel.feedbackRpe.collectAsStateWithLifecycle()
    val notesText by viewModel.feedbackNotes.collectAsStateWithLifecycle()
    val activeDur by viewModel.activeDurationMinutes.collectAsStateWithLifecycle()
    val activeRest by viewModel.activeRestTimeSeconds.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCharcoal)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Console Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ACTIVE ATHLETE TRACKER",
                    style = MaterialTheme.typography.labelSmall,
                    color = AccentCyan,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = sessionName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Red.copy(alpha = 0.15f))
                    .clickable { onDiscard() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("discard_workout_button"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Abort", tint = Color.Red, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "ABORT SESSION", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // List exercises & set entries
        exercises.forEach { ex ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGrey, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = ex.exerciseName,
                        fontWeight = FontWeight.Bold,
                        color = NeonLime,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Category: ${ex.exerciseCategory} | Standard: ${ex.targetSets} sets x ${ex.targetReps} reps",
                        fontSize = 13.sp,
                        color = MutedText
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "ST", fontSize = 12.sp, color = MutedText, modifier = Modifier.width(30.dp), fontWeight = FontWeight.Bold)
                        Text(text = "TARGETS", fontSize = 12.sp, color = MutedText, modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold)
                        Text(text = "ACTUAL WT(kg)", fontSize = 12.sp, color = MutedText, modifier = Modifier.weight(1.8f), fontWeight = FontWeight.Bold)
                        Text(text = "REPS", fontSize = 12.sp, color = MutedText, modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold)
                        Text(text = "RPE", fontSize = 12.sp, color = MutedText, modifier = Modifier.weight(1.4f), fontWeight = FontWeight.Bold)
                    }

                    // Render lines for each target sets
                    for (setIdx in 1..ex.targetSets) {
                        val key = "${ex.id}_$setIdx"
                        val model = setLogs[key] ?: WorkoutSetLog(logId = 0L, exerciseName = ex.exerciseName, category = ex.exerciseCategory, setIndex = setIdx)

                        var weightText by remember(model.actualWeight) { mutableStateOf(model.actualWeight.toString()) }
                        var repsText by remember(model.actualReps) { mutableStateOf(model.actualReps.toString()) }
                        var setRpe by remember(model.rpe) { mutableStateOf(model.rpe) }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Set Index
                            Text(text = "S$setIdx", fontSize = 12.sp, color = Color.White, modifier = Modifier.width(30.dp), fontWeight = FontWeight.Bold)

                            // Target Specs description
                            Text(
                                text = "${ex.targetWeightKg}kg x ${ex.targetReps}",
                                fontSize = 13.sp,
                                color = AccentCyan,
                                modifier = Modifier.weight(1.5f)
                            )

                            // Weight Field Input
                            OutlinedTextField(
                                value = weightText,
                                onValueChange = {
                                    weightText = it
                                    val parsedWeight = it.toDoubleOrNull() ?: 0.0
                                    onUpdateSet(ex.id, setIdx, parsedWeight, repsText.toIntOrNull() ?: 0, setRpe, true)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                colors = TextFieldDefaults.colors(
                                    focusedTextColor = WarmText,
                                    unfocusedTextColor = WarmText
                                ),
                                modifier = Modifier
                                    .weight(1.8f)
                                    .height(48.dp)
                                    .testTag("weight_input_${ex.id}_$setIdx")
                            )

                            // Reps Field Input
                            OutlinedTextField(
                                value = repsText,
                                onValueChange = {
                                    repsText = it
                                    val parsedReps = it.toIntOrNull() ?: 0
                                    onUpdateSet(ex.id, setIdx, weightText.toDoubleOrNull() ?: 0.0, parsedReps, setRpe, true)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = TextFieldDefaults.colors(
                                    focusedTextColor = WarmText,
                                    unfocusedTextColor = WarmText
                                ),
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(48.dp)
                                    .testTag("reps_input_${ex.id}_$setIdx")
                            )

                            // RPE Selector Box
                            IconButton(
                                onClick = {
                                    val nextRpe = if (setRpe == 10) 6 else setRpe + 1
                                    setRpe = nextRpe
                                    onUpdateSet(ex.id, setIdx, weightText.toDoubleOrNull() ?: 0.0, repsText.toIntOrNull() ?: 0, nextRpe, true)
                                },
                                modifier = Modifier
                                    .weight(1.4f)
                                    .background(SurfaceCharcoal, RoundedCornerShape(6.dp))
                                    .border(1.dp, BorderGrey, RoundedCornerShape(6.dp))
                                    .testTag("rpe_button_${ex.id}_$setIdx")
                            ) {
                                Text(text = "$setRpe", color = AccentOrange, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // SESSION REVIEW FORM
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderGrey, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Biomechanical Feedback & Satisfaction", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = NeonLime)

                Text(text = "RPE Rating (Overall Session exhaustion):", fontSize = 14.sp, color = MutedText)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    (6..10).forEach { score ->
                        val isSelected = rpeValue == score
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AccentOrange else DarkCharcoal)
                                .clickable { viewModel.feedbackRpe.value = score }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "$score", color = if (isSelected) Color.White else WarmText, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text(text = "Workout Session Satisfaction Rating:", fontSize = 14.sp, color = MutedText)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    (1..5).forEach { star ->
                        val isFull = star <= satisfaction
                        Icon(
                            imageVector = if (isFull) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Star",
                            tint = if (isFull) NeonLime else MutedText,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { viewModel.feedbackSatisfaction.value = star }
                                .padding(horizontal = 4.dp)
                        )
                    }
                }

                Text(text = "Actual Recorded Metrics (Duration & Rest Time):", fontSize = 14.sp, color = MutedText)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    var sDurText by remember(activeDur) { mutableStateOf(activeDur.toString()) }
                    OutlinedTextField(
                        value = sDurText,
                        onValueChange = {
                            sDurText = it
                            val parsed = it.toIntOrNull() ?: activeDur
                            viewModel.activeDurationMinutes.value = parsed
                        },
                        label = { Text("Actual Duration (min)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("feedback_actual_duration"),
                        colors = TextFieldDefaults.colors(focusedTextColor = WarmText, unfocusedTextColor = WarmText)
                    )

                    var sRestText by remember(activeRest) { mutableStateOf(activeRest.toString()) }
                    OutlinedTextField(
                        value = sRestText,
                        onValueChange = {
                            sRestText = it
                            val parsed = it.toIntOrNull() ?: activeRest
                            viewModel.activeRestTimeSeconds.value = parsed
                        },
                        label = { Text("Actual Rest Time (sec)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("feedback_actual_rest"),
                        colors = TextFieldDefaults.colors(focusedTextColor = WarmText, unfocusedTextColor = WarmText)
                    )
                }

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { viewModel.feedbackNotes.value = it },
                    label = { Text("How do your joints feel? Muscle pump notes?") },
                    modifier = Modifier.fillMaxWidth().testTag("session_feedback_notes"),
                    colors = TextFieldDefaults.colors(focusedTextColor = WarmText, unfocusedTextColor = WarmText)
                )
            }
        }

        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("save_workout_session_button"),
            colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(imageVector = Icons.Default.Save, contentDescription = "Finished", tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "SAVE OFFLINE LOG", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(40.dp)) // padding
    }
}

@Composable
fun HistoricLogCardTile(
    log: TrainingLog,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    val dateString = SimpleDateFormat("EEEE, MMM d, yyyy • HH:mm", Locale.getDefault()).format(Date(log.timestamp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceCharcoal, RoundedCornerShape(12.dp))
            .border(1.dp, BorderGrey, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(text = log.sessionName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    Text(text = dateString, fontSize = 11.sp, color = MutedText)
                }

                // Share & delete actions icon buttons
                Row {
                    IconButton(onClick = onShare, modifier = Modifier.testTag("share_log_${log.logId}")) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = NeonLime, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.testTag("delete_log_${log.logId}")) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = AccentOrange, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                HistoricMetricLabel(title = "Duration", value = "${log.durationMinutes} m", modifier = Modifier.weight(1f))
                HistoricMetricLabel(title = "Rest Sec", value = "${log.restTimeSeconds} s", modifier = Modifier.weight(1f))
                HistoricMetricLabel(title = "Athlete Wt", value = "${log.athleteWeightKg} kg", modifier = Modifier.weight(1.1f))
                HistoricMetricLabel(title = "Difficulty RPE", value = "RPE ${log.averageRpe}", modifier = Modifier.weight(1.2f))
                HistoricRatingStars(rating = log.feedbackSatisfaction, modifier = Modifier.weight(1f))
            }

            if (log.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkCharcoal, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(text = "Athlete Notes: ${log.notes}", fontSize = 12.sp, color = MutedText, lineHeight = 16.sp)
                }
            }

            // Sync indicators
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SyncIndicator(label = "Wearable sensor sync", active = log.isWearableSynced)
                SyncIndicator(label = "Cloud Backup", active = log.isCloudSynced)
            }
        }
    }
}

@Composable
fun HistoricMetricLabel(title: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(DarkCharcoal, RoundedCornerShape(6.dp))
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, fontSize = 9.sp, color = MutedText)
            Text(text = value, fontSize = 12.sp, color = WarmText, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun HistoricRatingStars(rating: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(DarkCharcoal, RoundedCornerShape(6.dp))
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Satisfy", fontSize = 9.sp, color = MutedText)
            Spacer(modifier = Modifier.height(2.dp))
            Row {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (index < rating) NeonLime else MutedText.copy(alpha = 0.2f),
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SyncIndicator(label: String, active: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(if (active) NeonLime else Color.Gray)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 9.sp, color = if (active) NeonLime else MutedText)
    }
}
