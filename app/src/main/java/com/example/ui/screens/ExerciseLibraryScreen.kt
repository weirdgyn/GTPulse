package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.WorkoutExercise
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExerciseLibraryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val exerciseList by viewModel.exerciseList.collectAsStateWithLifecycle()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    
    var showAddDialog by remember { mutableStateOf(false) }
    var editingExercise by remember { mutableStateOf<WorkoutExercise?>(null) }
    
    // Smooth filtered list based on search and selected category
    val filteredExercises = remember(exerciseList, searchQuery, selectedCategory) {
        exerciseList.filter { ex ->
            val matchesSearch = ex.name.contains(searchQuery, ignoreCase = true) ||
                    ex.primaryMuscle.contains(searchQuery, ignoreCase = true) ||
                    ex.description.contains(searchQuery, ignoreCase = true) ||
                    ex.equipment.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == "All" || ex.category.equals(selectedCategory, ignoreCase = true)
            matchesSearch && matchesCategory
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkCharcoal,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    editingExercise = null
                    showAddDialog = true 
                },
                containerColor = NeonLime,
                contentColor = Color.Black,
                modifier = Modifier.testTag("add_library_exercise_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Exercise")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // --- Header Title Area ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Exercise Arsenal",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Browse, edit and expand your training database",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp,
                        color = MutedText
                    )
                }
                
                // Exercise count badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentCyan.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${exerciseList.size} Lib",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Elegant Search Field ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("library_search_input"),
                placeholder = { Text("Search by name, muscle, equipment...", color = Color.Gray, fontSize = 14.sp) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear search", tint = Color.Gray)
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = NeonLime,
                    focusedBorderColor = NeonLime,
                    unfocusedBorderColor = BorderGrey,
                    focusedTextColor = WarmText,
                    unfocusedTextColor = WarmText,
                    unfocusedContainerColor = SurfaceCharcoal,
                    focusedContainerColor = SurfaceCharcoal
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // --- Horizontal Category Selection Chips ---
            val categories = listOf("All", "Chest", "Back", "Legs", "Shoulders", "Arms", "Core")
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) NeonLime else SurfaceCharcoal)
                            .border(1.dp, if (isSelected) NeonLime else BorderGrey, RoundedCornerShape(8.dp))
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("library_category_chip_$category"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black else WarmText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Main Exercises List ---
            if (filteredExercises.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = "No match",
                            tint = MutedText,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No Guided Exercises Found",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = WarmText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try refining your search text or select a different category.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredExercises, key = { it.id }) { ex ->
                        var isExpanded by remember { mutableStateOf(false) }
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, if (isExpanded) NeonLime.copy(alpha = 0.4f) else BorderGrey, RoundedCornerShape(14.dp))
                                .testTag("exercise_card_${ex.id}"),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                // Top row: name and main actions
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = ex.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        
                                        Row(
                                            modifier = Modifier.padding(top = 4.dp),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Category tag
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(NeonLime.copy(alpha = 0.15f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = ex.category,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = NeonLime
                                                )
                                            }
                                            
                                            // Target muscle
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(AccentCyan.copy(alpha = 0.15f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = ex.primaryMuscle,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = AccentCyan
                                                )
                                            }
                                            
                                            if (ex.isCustom) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(AccentOrange.copy(alpha = 0.15f))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "Custom",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = AccentOrange
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    
                                    // Row of action buttons
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Edit Button
                                        IconButton(
                                            onClick = {
                                                editingExercise = ex
                                                showAddDialog = true
                                            },
                                            modifier = Modifier
                                                .size(28.dp)
                                                .testTag("edit_exercise_btn_${ex.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit details",
                                                tint = WarmText,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        
                                        Spacer(modifier = Modifier.width(4.dp))
                                        
                                        // Delete Button
                                        IconButton(
                                            onClick = { viewModel.deleteExercise(ex.id) },
                                            modifier = Modifier
                                                .size(28.dp)
                                                .testTag("delete_exercise_btn_${ex.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete from library",
                                                tint = AccentOrange,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        
                                        Spacer(modifier = Modifier.width(4.dp))
                                        
                                        // Expand details toggle
                                        IconButton(
                                            onClick = { isExpanded = !isExpanded },
                                            modifier = Modifier
                                                .size(28.dp)
                                                .testTag("expand_exercise_btn_${ex.id}")
                                        ) {
                                            Icon(
                                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                contentDescription = "Show details",
                                                tint = Color.Gray,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = ex.description,
                                    fontSize = 14.sp,
                                    color = WarmText,
                                    lineHeight = 18.sp
                                )

                                // Expanding technical execution section
                                AnimatedVisibility(
                                    visible = isExpanded,
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically() + fadeOut()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 12.dp)
                                            .border(0.5.dp, BorderGrey, RoundedCornerShape(8.dp))
                                            .background(DarkCharcoal)
                                            .padding(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Required Equipment:",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = ex.equipment.ifBlank { "Unspecified" },
                                                fontSize = 13.sp,
                                                color = NeonLime,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        
                                        if (ex.instructions.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = "Execution Tutorial & Guidance:",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(bottom = 4.dp)
                                            )
                                            Text(
                                                text = ex.instructions,
                                                fontSize = 13.sp,
                                                color = MutedText,
                                                lineHeight = 17.sp
                                            )
                                        }
                                        
                                        if (ex.videoUrl.isNotBlank() || ex.pictureUrl.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = "Coach Media Assets:",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(bottom = 4.dp)
                                            )
                                            if (ex.videoUrl.isNotBlank()) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(vertical = 2.dp)
                                                ) {
                                                    Icon(imageVector = Icons.Default.PlayCircle, contentDescription = "Video", tint = NeonLime, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(text = "Video URL: ${ex.videoUrl}", fontSize = 12.sp, color = AccentCyan)
                                                }
                                            }
                                            if (ex.pictureUrl.isNotBlank()) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(vertical = 2.dp)
                                                ) {
                                                    Icon(imageVector = Icons.Default.Image, contentDescription = "Picture", tint = NeonLime, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(text = "Visual Asset: ${ex.pictureUrl}", fontSize = 12.sp, color = AccentCyan)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal dialog to add or edit an exercise
    if (showAddDialog) {
        LibraryExerciseDialog(
            editingExercise = editingExercise,
            onDismiss = { showAddDialog = false },
            onSave = { name, category, muscle, desc, eq, instr, vid, pic ->
                if (editingExercise != null) {
                    val updated = editingExercise!!.copy(
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
                } else {
                    viewModel.addCustomExercise(
                        name = name,
                        category = category,
                        muscle = muscle,
                        description = desc,
                        equipment = eq,
                        instructions = instr,
                        videoUrl = vid,
                        pictureUrl = pic
                    )
                }
                showAddDialog = false
            }
        )
    }
}

@Composable
fun LibraryExerciseDialog(
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
                text = if (editingExercise != null) "Edit Gym Exercise" else "Add New Gym Exercise",
                color = NeonLime,
                fontWeight = FontWeight.Bold
            )
        },
        containerColor = SurfaceCharcoal,
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Exercise Name") },
                    modifier = Modifier.fillMaxWidth().testTag("add_custom_name"),
                    colors = TextFieldDefaults.colors(focusedTextColor = WarmText, unfocusedTextColor = WarmText)
                )

                Text(text = "Category Split Target:", fontSize = 11.sp, color = MutedText, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    categories.take(3).forEach { cat ->
                        val active = category == cat
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (active) NeonLime else DarkCharcoal)
                                .border(1.dp, if (active) NeonLime else BorderGrey, RoundedCornerShape(6.dp))
                                .clickable { category = cat }
                                .padding(vertical = 8.dp)
                                .testTag("cat_choice_$cat"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = cat, fontSize = 10.sp, color = if (active) Color.Black else WarmText, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    categories.takeLast(3).forEach { cat ->
                        val active = category == cat
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (active) NeonLime else DarkCharcoal)
                                .border(1.dp, if (active) NeonLime else BorderGrey, RoundedCornerShape(6.dp))
                                .clickable { category = cat }
                                .padding(vertical = 8.dp)
                                .testTag("cat_choice_$cat"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = cat, fontSize = 10.sp, color = if (active) Color.Black else WarmText, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                OutlinedTextField(
                    value = muscle,
                    onValueChange = { muscle = it },
                    label = { Text("Primary Targeted Muscle (e.g. Quads)") },
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
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_add_custom_button")
            ) {
                Text(text = "Cancel", color = WarmText)
            }
        }
    )
}
