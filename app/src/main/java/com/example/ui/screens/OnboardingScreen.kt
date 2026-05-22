package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    var athleteName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var fitnessGoal by remember { mutableStateOf("Hypertrophy (Building Muscle)") }
    var trainingLevel by remember { mutableStateOf("Intermediate") }
    var defaultSite by remember { mutableStateOf("") }

    var showErrorMsg by remember { mutableStateOf<String?>(null) }

    val goalsList = listOf(
        "Hypertrophy (Building Muscle)",
        "Raw Strength Builder",
        "Cutting (Fat Loss / Definition)",
        "Endurance & Conditioning"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkCharcoal)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Brand Header with Premium Icons
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = "GT Pulse Shield Logo",
                tint = NeonLime,
                modifier = Modifier
                    .size(64.dp)
                    .testTag("onboarding_logo")
            )

            Text(
                text = "GT PULSE COMMAND CENTER",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                ),
                color = WarmText,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Configure your athletic specs to initialize your fitness computer, training charts, and AI-powered recommendations.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            HorizontalDivider(
                color = BorderGrey,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Dynamic specifications card container
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceCharcoal),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderGrey)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "ATHLETE PROFILE SPECS",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = NeonLime
                    )

                    // Athlete Name
                    OutlinedTextField(
                        value = athleteName,
                        onValueChange = { athleteName = it },
                        label = { Text("Athlete Name") },
                        placeholder = { Text("e.g. Alex Rivera") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_name_input"),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = WarmText,
                            unfocusedTextColor = WarmText
                        )
                    )

                    // Age, Weight, Height Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = age,
                            onValueChange = { age = it },
                            label = { Text("Age") },
                            placeholder = { Text("25") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("onboarding_age_input"),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = WarmText,
                                unfocusedTextColor = WarmText
                            )
                        )

                        OutlinedTextField(
                            value = weight,
                            onValueChange = { weight = it },
                            label = { Text("Weight (kg)") },
                            placeholder = { Text("75.0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("onboarding_weight_input"),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = WarmText,
                                unfocusedTextColor = WarmText
                            )
                        )

                        OutlinedTextField(
                            value = height,
                            onValueChange = { height = it },
                            label = { Text("Height (cm)") },
                            placeholder = { Text("175.0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("onboarding_height_input"),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = WarmText,
                                unfocusedTextColor = WarmText
                            )
                        )
                    }

                    // Gender Choice Row
                    Text(
                        text = "Gender Identification:",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MutedText
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Male", "Female", "Prefer Not to Say").forEach { g ->
                            val isSelected = gender == g
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) AccentCyan.copy(alpha = 0.2f) else Color.Transparent)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) AccentCyan else BorderGrey,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { gender = g }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = g,
                                    color = if (isSelected) AccentCyan else WarmText,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // Location Info
                    OutlinedTextField(
                        value = defaultSite,
                        onValueChange = { defaultSite = it },
                        label = { Text("Primary Workout Location (HQ)") },
                        placeholder = { Text("e.g. Iron Forge Gym / Gold's Gym") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_site_input"),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = WarmText,
                            unfocusedTextColor = WarmText
                        )
                    )

                    // Fitness Goals Selection FlowRow
                    Text(
                        text = "Primary Training Objective:",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MutedText
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        goalsList.forEach { g ->
                            val isSelected = fitnessGoal == g
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) NeonLime else SurfaceCharcoal)
                                    .border(1.dp, if (isSelected) NeonLime else BorderGrey, RoundedCornerShape(10.dp))
                                    .clickable { fitnessGoal = g }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = g,
                                    color = if (isSelected) Color.Black else WarmText,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Biomechanical level
                    Text(
                        text = "Biomechanical Experience Stage:",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MutedText
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Beginner", "Intermediate", "Advanced").forEach { lvl ->
                            val isSelected = trainingLevel == lvl
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) AccentCyan else SurfaceCharcoal)
                                    .border(1.dp, if (isSelected) AccentCyan else BorderGrey, RoundedCornerShape(8.dp))
                                    .clickable { trainingLevel = lvl }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lvl,
                                    color = if (isSelected) Color.Black else WarmText,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Error Display (If any validation fails)
            if (showErrorMsg != null) {
                Text(
                    text = showErrorMsg ?: "",
                    color = AccentOrange,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("onboarding_error_msg")
                )
            }

            // Submit Button
            Button(
                onClick = {
                    focusManager.clearFocus()
                    if (athleteName.trim().isEmpty()) {
                        showErrorMsg = "Please supply a valid Athlete Name."
                        return@Button
                    }
                    val parsedAge = age.toIntOrNull() ?: 0
                    if (parsedAge <= 0 || parsedAge > 120) {
                        showErrorMsg = "Please supply a sensible Age."
                        return@Button
                    }
                    val parsedWeight = weight.toDoubleOrNull() ?: 0.0
                    if (parsedWeight <= 0.0 || parsedWeight > 500.0) {
                        showErrorMsg = "Please supply a sensible Weight (kg)."
                        return@Button
                    }
                    val parsedHeight = height.toDoubleOrNull() ?: 0.0
                    if (parsedHeight <= 0.0 || parsedHeight > 300.0) {
                        showErrorMsg = "Please supply a sensible Height (cm)."
                        return@Button
                    }
                    val finalSite = if (defaultSite.trim().isEmpty()) "GT Pulse HQ Gym" else defaultSite.trim()

                    showErrorMsg = null
                    viewModel.updateProfile(
                        name = athleteName.trim(),
                        age = parsedAge,
                        weight = parsedWeight,
                        height = parsedHeight,
                        gender = gender,
                        goal = fitnessGoal,
                        level = trainingLevel,
                        site = finalSite
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("onboarding_submit_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonLime,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "INITIALIZE & LAUNCH SYSTEM",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Initialize Specs"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
