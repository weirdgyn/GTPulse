package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import com.example.data.model.SessionExercise
import com.example.data.model.WorkoutExercise
import com.example.data.model.WorkoutSession
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorkoutPlanScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val exerciseList by viewModel.exerciseList.collectAsStateWithLifecycle()
    val sessionsList by viewModel.sessionsList.collectAsStateWithLifecycle()
    val editingSession by viewModel.editingSession.collectAsStateWithLifecycle()
    val plannedExercises by viewModel.plannedExercises.collectAsStateWithLifecycle()

    var showRoutineBuilder by remember { mutableStateOf(false) }
    var showCustomExerciseDialog by remember { mutableStateOf(false) }
    var selectedEditingExercise by remember { mutableStateOf<WorkoutExercise?>(null) }
    var isFabExpanded by remember { mutableStateOf(false) }

    // Form states for Session
    var routineName by remember { mutableStateOf("") }
    var targetSplit by remember { mutableStateOf("Fullbody") }
    var routineNotes by remember { mutableStateOf("") }
    var routineSequence by remember { mutableStateOf("1") }
    var routineDayOfWeek by remember { mutableStateOf("Monday") }

    // Workspace triggers
    LaunchedEffect(editingSession) {
        if (editingSession != null) {
            routineName = editingSession!!.name
            targetSplit = editingSession!!.targetSplit
            routineNotes = editingSession!!.notes
            routineSequence = editingSession!!.sequenceNumber.toString()
            routineDayOfWeek = editingSession!!.dayOfWeek
            showRoutineBuilder = true
        } else {
            routineName = ""
            targetSplit = "Fullbody"
            routineNotes = ""
            routineSequence = "1"
            routineDayOfWeek = "Monday"
            showRoutineBuilder = false
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkCharcoal,
        floatingActionButton = {
            if (!showRoutineBuilder) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Option 1: Add Custom Exercise to Library
                    AnimatedVisibility(
                        visible = isFabExpanded,
                        enter = fadeIn() + expandVertically() + slideInVertically(initialOffsetY = { it / 2 }),
                        exit = fadeOut() + shrinkVertically() + slideOutVertically(targetOffsetY = { it / 2 })
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Surface(
                                color = SurfaceCharcoal.copy(alpha = 0.95f),
                                border = BorderStroke(1.dp, BorderGrey),
                                shape = RoundedCornerShape(8.dp),
                                tonalElevation = 4.dp
                            ) {
                                Text(
                                    text = stringResource(R.string.action_add_exercise),
                                    color = WarmText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                            FloatingActionButton(
                                onClick = {
                                    isFabExpanded = false
                                    showCustomExerciseDialog = true
                                },
                                containerColor = SurfaceCharcoal,
                                contentColor = NeonLime,
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("fab_add_exercise_option"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LibraryAdd,
                                    contentDescription = stringResource(R.string.action_add_exercise)
                                )
                            }
                        }
                    }

                    // Option 2: Create New Workout Plan
                    AnimatedVisibility(
                        visible = isFabExpanded,
                        enter = fadeIn() + expandVertically() + slideInVertically(initialOffsetY = { it / 2 }),
                        exit = fadeOut() + shrinkVertically() + slideOutVertically(targetOffsetY = { it / 2 })
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Surface(
                                color = SurfaceCharcoal.copy(alpha = 0.95f),
                                border = BorderStroke(1.dp, BorderGrey),
                                shape = RoundedCornerShape(8.dp),
                                tonalElevation = 4.dp
                            ) {
                                Text(
                                    text = stringResource(R.string.action_add_plan),
                                    color = WarmText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                            FloatingActionButton(
                                onClick = {
                                    isFabExpanded = false
                                    viewModel.selectSessionForEdit(null)
                                    routineName = ""
                                    targetSplit = "Fullbody"
                                    routineNotes = ""
                                    routineSequence = "1"
                                    routineDayOfWeek = "Monday"
                                    showRoutineBuilder = true
                                },
                                containerColor = SurfaceCharcoal,
                                contentColor = NeonLime,
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("fab_add_plan_option"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = stringResource(R.string.action_add_plan)
                                )
                            }
                        }
                    }

                    // Main Toggle FAB (animates adding cross rotation into a close cross when open)
                    val rotationAngle by animateFloatAsState(
                        targetValue = if (isFabExpanded) 45f else 0f,
                        label = "fab_rotation"
                    )
                    FloatingActionButton(
                        onClick = { isFabExpanded = !isFabExpanded },
                        containerColor = NeonLime,
                        contentColor = Color.Black,
                        modifier = Modifier.testTag("create_routine_fab")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Toggle add options",
                            modifier = Modifier.rotate(rotationAngle)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Screen Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ATHLETE SCHEDULER",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonLime,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "Build bodybuilding splits and session exercises",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedText
                    )
                }
            }

            if (showRoutineBuilder) {
                // ROUTINE BUILDER WORKSPACE
                RoutineBuilderWorkspace(
                    routineName = routineName,
                    onNameChange = { routineName = it },
                    targetSplit = targetSplit,
                    onSplitChange = { targetSplit = it },
                    notes = routineNotes,
                    onNotesChange = { routineNotes = it },
                    sequenceNumber = routineSequence,
                    onSequenceChange = { routineSequence = it },
                    dayOfWeek = routineDayOfWeek,
                    onDayOfWeekChange = { routineDayOfWeek = it },
                    exerciseLibrary = exerciseList,
                    plannedExercises = plannedExercises,
                    onAddExercise = { ex -> viewModel.addExerciseToPlan(ex) },
                    onRemoveExercise = { idx -> viewModel.removeExerciseFromPlan(idx) },
                    onUpdateExerciseDetails = { idx, sets, reps, wt, dur, rest ->
                        viewModel.updatePlannedExercise(idx, sets, reps, wt, dur, rest)
                    },
                    onEditExercise = { ex -> selectedEditingExercise = ex },
                    onSave = {
                        val session = editingSession
                        val parsedSeq = routineSequence.toIntOrNull() ?: 1
                        if (session != null) {
                            viewModel.updateExistingSession(session, routineName, targetSplit, routineNotes, parsedSeq, routineDayOfWeek)
                        } else {
                            viewModel.startNewSessionCreation(routineName, targetSplit, routineNotes, parsedSeq, routineDayOfWeek)
                        }
                        showRoutineBuilder = false
                    },
                    onCancel = {
                        viewModel.selectSessionForEdit(null)
                        showRoutineBuilder = false
                    }
                )
            } else {
                // ACTIVE ROUTINES LIST
                if (sessionsList.isEmpty()) {
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
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Empty plan",
                                tint = MutedText,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Training Plan is Empty",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = WarmText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap the '+' knob at bottom right to construct your first muscle split routine (Push/Pull, Leg blaster, etc.) or seed sample databases.",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp,
                                color = MutedText,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 18.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(sessionsList) { session ->
                            RoutineSessionCardTile(
                                session = session,
                                onEdit = { viewModel.selectSessionForEdit(session) },
                                onDelete = { viewModel.deleteSession(session) },
                                onTrain = { viewModel.startActiveWorkout(session) }
                            )
                        }
                    }
                }
            }
        }

        // CUSTOM EXERCISE ADDER / EDITOR DIALOG
        if (showCustomExerciseDialog) {
            CustomExerciseDialog(
                editingExercise = null,
                onDismiss = { showCustomExerciseDialog = false },
                onSave = { name, category, muscle, desc, eq, instr, vid, pic ->
                    viewModel.addCustomExercise(name, category, muscle, desc, eq, instr, vid, pic)
                    showCustomExerciseDialog = false
                }
            )
        }

        if (selectedEditingExercise != null) {
            CustomExerciseDialog(
                editingExercise = selectedEditingExercise,
                onDismiss = { selectedEditingExercise = null },
                onSave = { name, category, muscle, desc, eq, instr, vid, pic ->
                    val updated = selectedEditingExercise!!.copy(
                        name = name,
                        category = category,
                        primaryMuscle = muscle,
                        description = desc,
                        equipment = eq,
                        instructions = instr,
                        videoUrl = vid,
                        pictureUrl = pic
                    )
                    viewModel.saveExercise(updated)
                    selectedEditingExercise = null
                }
            )
        }
    }
}

@Composable
fun RoutineSessionCardTile(
    session: WorkoutSession,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTrain: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceCharcoal, RoundedCornerShape(14.dp))
            .border(1.dp, BorderGrey, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Column {
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
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Target icon",
                            tint = NeonLime,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = session.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AccentCyan.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Seq #${session.sequenceNumber}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AccentCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NeonLime.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = session.dayOfWeek,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonLime,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(WarmText.copy(alpha = 0.1f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Split: ${session.targetSplit}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = WarmText,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Edit / Delete anchors
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.testTag("edit_routine_${session.id}")) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Routine", tint = MutedText, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.testTag("delete_routine_${session.id}")) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Routine", tint = AccentOrange, modifier = Modifier.size(20.dp))
                    }
                }
            }

            if (session.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Coach notes: ${session.notes}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp,
                    color = MutedText,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onTrain,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("start_train_${session.id}"),
                colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Train Now", tint = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Start Workout Session", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoutineBuilderWorkspace(
    routineName: String,
    onNameChange: (String) -> Unit,
    targetSplit: String,
    onSplitChange: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    sequenceNumber: String,
    onSequenceChange: (String) -> Unit,
    dayOfWeek: String,
    onDayOfWeekChange: (String) -> Unit,
    exerciseLibrary: List<WorkoutExercise>,
    plannedExercises: List<SessionExercise>,
    onAddExercise: (WorkoutExercise) -> Unit,
    onRemoveExercise: (Int) -> Unit,
    onUpdateExerciseDetails: (Int, Int, Int, Double, Int, Int) -> Unit,
    onEditExercise: (WorkoutExercise) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    var exerciseSearchQuery by remember { mutableStateOf("") }

    val splits = listOf("Push", "Pull", "Legs", "Arms", "Chest", "Core", "Fullbody")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderGrey, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Plan Meta Setup", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = NeonLime)

                OutlinedTextField(
                    value = routineName,
                    onValueChange = onNameChange,
                    label = { Text("Routine Name (e.g. Incline Hypertrophy Push)") },
                    modifier = Modifier.fillMaxWidth().testTag("routine_name_input"),
                    colors = TextFieldDefaults.colors(focusedTextColor = WarmText, unfocusedTextColor = WarmText)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = sequenceNumber,
                        onValueChange = onSequenceChange,
                        label = { Text("Sequence #") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("routine_sequence_input"),
                        colors = TextFieldDefaults.colors(focusedTextColor = WarmText, unfocusedTextColor = WarmText)
                    )

                    var expandedDayDropdown by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.weight(2.5f)) {
                        OutlinedTextField(
                            value = dayOfWeek,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Day of Week") },
                            modifier = Modifier.fillMaxWidth().clickable { expandedDayDropdown = true }.testTag("routine_day_dropdown"),
                            trailingIcon = {
                                IconButton(onClick = { expandedDayDropdown = !expandedDayDropdown }) {
                                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Choose Day", tint = NeonLime)
                                }
                            },
                            colors = TextFieldDefaults.colors(focusedTextColor = WarmText, unfocusedTextColor = WarmText)
                        )
                        DropdownMenu(
                            expanded = expandedDayDropdown,
                            onDismissRequest = { expandedDayDropdown = false },
                            modifier = Modifier.background(SurfaceCharcoal)
                        ) {
                            val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday", "As Needed", "Rest Day")
                            daysOfWeek.forEach { day ->
                                DropdownMenuItem(
                                    text = { Text(day, color = Color.White) },
                                    onClick = {
                                        onDayOfWeekChange(day)
                                        expandedDayDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                Text(text = "Anatomical Target Split:", fontSize = 14.sp, color = MutedText, fontWeight = FontWeight.Bold)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    splits.forEach { s ->
                        val isSelected = targetSplit == s
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) NeonLime else DarkCharcoal)
                                .border(1.dp, if (isSelected) NeonLime else BorderGrey, RoundedCornerShape(6.dp))
                                .clickable { onSplitChange(s) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = s,
                                color = if (isSelected) Color.Black else WarmText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = onNotesChange,
                    label = { Text("Coaching Objectives / Split Advice") },
                    modifier = Modifier.fillMaxWidth().testTag("routine_notes_input"),
                    colors = TextFieldDefaults.colors(focusedTextColor = WarmText, unfocusedTextColor = WarmText)
                )
            }
        }

        // CURRENT MAPPED EXERCISES IN PLAN
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderGrey, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Routines Exercises Mapping (${plannedExercises.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = AccentCyan)
                Spacer(modifier = Modifier.height(12.dp))

                if (plannedExercises.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkCharcoal, RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Select exercises from library tray below down to inject into your target plan.", style = MaterialTheme.typography.bodySmall, color = MutedText)
                    }
                } else {
                    plannedExercises.forEachIndexed { index, ex ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .background(DarkCharcoal, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${index + 1}. ${ex.exerciseName}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                IconButton(onClick = { onRemoveExercise(index) }, modifier = Modifier.size(24.dp).testTag("remove_ex_plan_$index")) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = AccentOrange, modifier = Modifier.size(16.dp))
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Sets input
                                var sText by remember(ex.targetSets) { mutableStateOf(ex.targetSets.toString()) }
                                OutlinedTextField(
                                    value = sText,
                                    onValueChange = {
                                        sText = it
                                        val parsed = it.toIntOrNull() ?: ex.targetSets
                                        onUpdateExerciseDetails(index, parsed, ex.targetReps, ex.targetWeightKg, ex.targetDurationMinutes, ex.targetRestTimeSeconds)
                                    },
                                    label = { Text("Sets", fontSize = 12.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    colors = TextFieldDefaults.colors(focusedTextColor = WarmText, unfocusedTextColor = WarmText)
                                )
                                
                                // Reps input
                                var rText by remember(ex.targetReps) { mutableStateOf(ex.targetReps.toString()) }
                                OutlinedTextField(
                                    value = rText,
                                    onValueChange = {
                                        rText = it
                                        val parsed = it.toIntOrNull() ?: ex.targetReps
                                        onUpdateExerciseDetails(index, ex.targetSets, parsed, ex.targetWeightKg, ex.targetDurationMinutes, ex.targetRestTimeSeconds)
                                    },
                                    label = { Text("Reps", fontSize = 12.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    colors = TextFieldDefaults.colors(focusedTextColor = WarmText, unfocusedTextColor = WarmText)
                                )
                                
                                // Load input
                                var wText by remember(ex.targetWeightKg) { mutableStateOf(ex.targetWeightKg.toString()) }
                                OutlinedTextField(
                                    value = wText,
                                    onValueChange = {
                                        wText = it
                                        val parsed = it.toDoubleOrNull() ?: ex.targetWeightKg
                                        onUpdateExerciseDetails(index, ex.targetSets, ex.targetReps, parsed, ex.targetDurationMinutes, ex.targetRestTimeSeconds)
                                    },
                                    label = { Text("Load (kg)", fontSize = 12.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.weight(1.2f).height(48.dp),
                                    colors = TextFieldDefaults.colors(focusedTextColor = WarmText, unfocusedTextColor = WarmText)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Duration minutes input
                                var dText by remember(ex.targetDurationMinutes) { mutableStateOf(ex.targetDurationMinutes.toString()) }
                                OutlinedTextField(
                                    value = dText,
                                    onValueChange = {
                                        dText = it
                                        val parsed = it.toIntOrNull() ?: ex.targetDurationMinutes
                                        onUpdateExerciseDetails(index, ex.targetSets, ex.targetReps, ex.targetWeightKg, parsed, ex.targetRestTimeSeconds)
                                    },
                                    label = { Text("Target Duration (min)", fontSize = 12.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1.5f).height(48.dp),
                                    colors = TextFieldDefaults.colors(focusedTextColor = WarmText, unfocusedTextColor = WarmText)
                                )
                                
                                // Rest Time seconds input
                                var restText by remember(ex.targetRestTimeSeconds) { mutableStateOf(ex.targetRestTimeSeconds.toString()) }
                                OutlinedTextField(
                                    value = restText,
                                    onValueChange = {
                                        restText = it
                                        val parsed = it.toIntOrNull() ?: ex.targetRestTimeSeconds
                                        onUpdateExerciseDetails(index, ex.targetSets, ex.targetReps, ex.targetWeightKg, ex.targetDurationMinutes, parsed)
                                    },
                                    label = { Text("Rest Time (sec)", fontSize = 12.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1.5f).height(48.dp),
                                    colors = TextFieldDefaults.colors(focusedTextColor = WarmText, unfocusedTextColor = WarmText)
                                )
                            }
                        }
                    }
                }
            }
        }

        // SELECTION TRAY: EXERCISE LIBRARY
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderGrey, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Select Exercises from Library", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = WarmText)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = exerciseSearchQuery,
                    onValueChange = { exerciseSearchQuery = it },
                    placeholder = { Text("Search exercises (e.g. Bench)") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = MutedText) },
                    modifier = Modifier.fillMaxWidth().testTag("exercise_search_input"),
                    colors = TextFieldDefaults.colors(focusedTextColor = WarmText, unfocusedTextColor = WarmText)
                )

                Spacer(modifier = Modifier.height(12.dp))

                val filtered = exerciseLibrary.filter {
                    it.name.contains(exerciseSearchQuery, ignoreCase = true) ||
                    it.category.contains(exerciseSearchQuery, ignoreCase = true)
                }

                Box(modifier = Modifier.height(180.dp)) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filtered) { ex ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkCharcoal)
                                    .clickable { onAddExercise(ex) }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = ex.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                    Text(text = "${ex.category} • ${ex.primaryMuscle}", fontSize = 11.sp, color = AccentCyan)
                                    Text(text = ex.description, fontSize = 9.sp, color = MutedText, maxLines = 1)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { onEditExercise(ex) },
                                        modifier = Modifier.size(28.dp).testTag("edit_exercise_lib_${ex.id}")
                                    ) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Exercise detail", tint = WarmText, modifier = Modifier.size(14.dp))
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Add", tint = NeonLime, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // SAVE / CANCEL BUTTONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f).testTag("cancel_routine_builder"),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, BorderGrey)
            ) {
                Icon(imageVector = Icons.Default.Cancel, contentDescription = "Cancel", tint = MutedText)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "CANCEL", color = MutedText, fontSize = 14.sp)
            }

            Button(
                onClick = {
                    focusManager.clearFocus()
                    onSave()
                },
                enabled = routineName.isNotBlank(),
                modifier = Modifier.weight(1f).testTag("save_routine_button"),
                colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = "Save", tint = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "SAVE", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(40.dp)) // padding
    }
}

@Composable
fun CustomExerciseDialog(
    editingExercise: WorkoutExercise? = null,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(editingExercise?.name ?: "") }
    var category by remember { mutableStateOf(editingExercise?.category ?: "Chest") }
    var muscle by remember { mutableStateOf(editingExercise?.primaryMuscle ?: "") }
    var description by remember { mutableStateOf(editingExercise?.description ?: "") }
    var equipment by remember { mutableStateOf(editingExercise?.equipment ?: "Barbell") }
    var instructions by remember { mutableStateOf(editingExercise?.instructions ?: "") }
    var videoUrl by remember { mutableStateOf(editingExercise?.videoUrl ?: "") }
    var pictureUrl by remember { mutableStateOf(editingExercise?.pictureUrl ?: "") }

    val categories = listOf("Chest", "Back", "Legs", "Shoulders", "Arms", "Core")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (editingExercise != null) "Edit Gym Exercise Details" else "Add Custom Gym Exercise",
                color = NeonLime,
                fontWeight = FontWeight.Bold
            )
        },
        containerColor = SurfaceCharcoal,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Exercise Name (e.g. Incline DB Flys)") },
                    modifier = Modifier.fillMaxWidth().testTag("add_custom_name"),
                    colors = TextFieldDefaults.colors(focusedTextColor = WarmText, unfocusedTextColor = WarmText)
                )

                Text(text = "Muscle Group Block:", fontSize = 12.sp, color = MutedText)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    categories.take(3).forEach { cat ->
                        val isSel = category == cat
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) AccentCyan else DarkCharcoal)
                                .clickable { category = cat }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = cat, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.Black else WarmText)
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    categories.takeLast(3).forEach { cat ->
                        val isSel = category == cat
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) AccentCyan else DarkCharcoal)
                                .clickable { category = cat }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = cat, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.Black else WarmText)
                        }
                    }
                }

                OutlinedTextField(
                    value = muscle,
                    onValueChange = { muscle = it },
                    label = { Text("Primary Target Fiber (e.g. Upper Pecs)") },
                    modifier = Modifier.fillMaxWidth().testTag("add_custom_muscle"),
                    colors = TextFieldDefaults.colors(focusedTextColor = WarmText, unfocusedTextColor = WarmText)
                )

                OutlinedTextField(
                    value = equipment,
                    onValueChange = { equipment = it },
                    label = { Text("Required Gym Equipment (e.g. Cables)") },
                    modifier = Modifier.fillMaxWidth().testTag("add_custom_equipment"),
                    colors = TextFieldDefaults.colors(focusedTextColor = WarmText, unfocusedTextColor = WarmText)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Short focus/purpose description") },
                    modifier = Modifier.fillMaxWidth().testTag("add_custom_desc"),
                    colors = TextFieldDefaults.colors(focusedTextColor = WarmText, unfocusedTextColor = WarmText)
                )

                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Step-by-step execution guidance") },
                    modifier = Modifier.fillMaxWidth().testTag("add_custom_instructions"),
                    colors = TextFieldDefaults.colors(focusedTextColor = WarmText, unfocusedTextColor = WarmText)
                )

                OutlinedTextField(
                    value = videoUrl,
                    onValueChange = { videoUrl = it },
                    label = { Text("Optional video tutorial url") },
                    modifier = Modifier.fillMaxWidth().testTag("add_custom_video"),
                    colors = TextFieldDefaults.colors(focusedTextColor = WarmText, unfocusedTextColor = WarmText)
                )

                OutlinedTextField(
                    value = pictureUrl,
                    onValueChange = { pictureUrl = it },
                    label = { Text("Optional coach picture/image url") },
                    modifier = Modifier.fillMaxWidth().testTag("add_custom_picture"),
                    colors = TextFieldDefaults.colors(focusedTextColor = WarmText, unfocusedTextColor = WarmText)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, category, muscle, description, equipment, instructions, videoUrl, pictureUrl) },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                modifier = Modifier.testTag("confirm_add_custom_button")
            ) {
                Text(
                    text = if (editingExercise != null) "Save Exercise Update" else "Save to Library",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Dismiss", color = WarmText)
            }
        }
    )
}
