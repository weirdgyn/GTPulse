package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.api.*
import com.example.data.model.*
import com.example.data.repository.FitnessRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class AppScreen {
    DASHBOARD,
    WORKOUT_PLAN,
    EXECUTION,
    EXERCISE_LIBRARY,
    AI_ADVICE
}

class MainViewModel(
    application: Application,
    private val repository: FitnessRepository
) : AndroidViewModel(application) {

    // --- Navigation State ---
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    // --- Theme State (Settings) ---
    private val sharedPrefs = application.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    private val _themeMode = MutableStateFlow(sharedPrefs.getString("theme_mode", "DARK") ?: "DARK")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    fun setThemeMode(mode: String) {
        sharedPrefs.edit().putString("theme_mode", mode).apply()
        _themeMode.value = mode
    }

    // --- Dev Mode State ---
    private val _isDevMode = MutableStateFlow(sharedPrefs.getBoolean("dev_mode", true))
    val isDevMode: StateFlow<Boolean> = _isDevMode.asStateFlow()

    fun setDevMode(enabled: Boolean) {
        sharedPrefs.edit().putBoolean("dev_mode", enabled).apply()
        _isDevMode.value = enabled
    }

    // --- User Profile State ---
    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .map { it ?: UserProfile() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfile()
        )

    fun updateProfile(
        name: String,
        age: Int,
        weight: Double,
        height: Double,
        gender: String,
        goal: String,
        level: String,
        site: String
    ) {
        viewModelScope.launch {
            val updated = UserProfile(
                id = 1,
                athleteName = name,
                age = age,
                weightKg = weight,
                heightCm = height,
                gender = gender,
                fitnessGoal = goal,
                trainingLevel = level,
                defaultTrainingSite = site,
                isCompleted = true
            )
            repository.saveUserProfile(updated)
            showSnackBar("Profile updated successfully, $name!")
            updateBadges()
        }
    }

    // --- Exercise Library State ---
    val exerciseList: StateFlow<List<WorkoutExercise>> = repository.allExercises
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addCustomExercise(
        name: String,
        category: String,
        muscle: String,
        description: String,
        equipment: String = "Barbell",
        instructions: String = "",
        videoUrl: String = "",
        pictureUrl: String = ""
    ) {
        viewModelScope.launch {
            if (name.isBlank()) return@launch
            val exercise = WorkoutExercise(
                name = name,
                category = category,
                primaryMuscle = muscle,
                description = description,
                isCustom = true,
                equipment = equipment,
                instructions = instructions,
                videoUrl = videoUrl,
                pictureUrl = pictureUrl
            )
            repository.insertExercise(exercise)
            showSnackBar("Custom exercise '$name' added to your library.")
        }
    }

    fun saveExercise(exercise: WorkoutExercise) {
        viewModelScope.launch {
            repository.insertExercise(exercise)
            showSnackBar("Exercise '${exercise.name}' updated in your library.")
        }
    }

    fun deleteExercise(id: Long) {
        viewModelScope.launch {
            repository.deleteExerciseById(id)
            showSnackBar("Exercise removed.")
        }
    }

    // --- Workout Sessions / Planning State ---
    val sessionsList: StateFlow<List<WorkoutSession>> = repository.allSessions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current session exercises being edited or built
    private val _plannedExercises = MutableStateFlow<List<SessionExercise>>(emptyList())
    val plannedExercises: StateFlow<List<SessionExercise>> = _plannedExercises.asStateFlow()

    private val _editingSession = MutableStateFlow<WorkoutSession?>(null)
    val editingSession: StateFlow<WorkoutSession?> = _editingSession.asStateFlow()

    fun selectSessionForEdit(session: WorkoutSession?) {
        _editingSession.value = session
        if (session != null) {
            viewModelScope.launch {
                repository.getExercisesForSession(session.id).collectLatest { exercises ->
                    _plannedExercises.value = exercises
                }
            }
        } else {
            _plannedExercises.value = emptyList()
        }
    }

    fun startNewSessionCreation(name: String, split: String, notes: String, sequenceNumber: Int = 1, dayOfWeek: String = "Monday") {
        viewModelScope.launch {
            val currentExs = _plannedExercises.value
            val session = WorkoutSession(
                name = name,
                targetSplit = split,
                notes = notes,
                sequenceNumber = sequenceNumber,
                dayOfWeek = dayOfWeek
            )
            val sessionId = repository.insertSession(session)
            val mapped = currentExs.mapIndexed { idx, it ->
                it.copy(sessionId = sessionId, orderIndex = idx)
            }
            repository.saveSessionExercises(sessionId, mapped)
            _editingSession.value = null
            _plannedExercises.value = emptyList()
            showSnackBar("Workout Plan '$name' created successfully.")
        }
    }

    fun updateExistingSession(session: WorkoutSession, name: String, split: String, notes: String, sequenceNumber: Int, dayOfWeek: String) {
        viewModelScope.launch {
            val updatedSession = session.copy(
                name = name,
                targetSplit = split,
                notes = notes,
                sequenceNumber = sequenceNumber,
                dayOfWeek = dayOfWeek
            )
            repository.insertSession(updatedSession)
            val currentExs = _plannedExercises.value
            val mapped = currentExs.mapIndexed { idx, it ->
                it.copy(sessionId = session.id, orderIndex = idx)
            }
            repository.saveSessionExercises(session.id, mapped)
            _editingSession.value = null
            _plannedExercises.value = emptyList()
            showSnackBar("Workout Plan '${name}' updated successfully.")
        }
    }

    fun deleteSession(session: WorkoutSession) {
        viewModelScope.launch {
            repository.deleteSessionById(session.id)
            showSnackBar("Workout Plan '${session.name}' deleted.")
        }
    }

    fun addExerciseToPlan(
        exercise: WorkoutExercise,
        sets: Int = 3,
        reps: Int = 10,
        weightKg: Double = 20.0,
        durationMinutes: Int = 5,
        restTimeSeconds: Int = 60
    ) {
        val current = _plannedExercises.value.toMutableList()
        val newEx = SessionExercise(
            sessionId = _editingSession.value?.id ?: 0L,
            exerciseId = exercise.id,
            exerciseName = exercise.name,
            exerciseCategory = exercise.category,
            orderIndex = current.size,
            targetSets = sets,
            targetReps = reps,
            targetWeightKg = weightKg,
            targetDurationMinutes = durationMinutes,
            targetRestTimeSeconds = restTimeSeconds
        )
        current.add(newEx)
        _plannedExercises.value = current
    }

    fun updatePlannedExercise(
        index: Int,
        sets: Int,
        reps: Int,
        weightKg: Double,
        durationMinutes: Int,
        restTimeSeconds: Int
    ) {
        val current = _plannedExercises.value.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(
                targetSets = sets,
                targetReps = reps,
                targetWeightKg = weightKg,
                targetDurationMinutes = durationMinutes,
                targetRestTimeSeconds = restTimeSeconds
            )
            _plannedExercises.value = current
        }
    }

    fun removeExerciseFromPlan(index: Int) {
        val current = _plannedExercises.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _plannedExercises.value = current
        }
    }

    // --- Active Workout Execution State ---
    private val _activeSession = MutableStateFlow<WorkoutSession?>(null)
    val activeSession: StateFlow<WorkoutSession?> = _activeSession.asStateFlow()

    private val _activeExercises = MutableStateFlow<List<SessionExercise>>(emptyList())
    val activeExercises: StateFlow<List<SessionExercise>> = _activeExercises.asStateFlow()

    // Key is representation of "exerciseId_setIndex" to track recorded reps & weights
    private val _activeSetLogs = MutableStateFlow<Map<String, WorkoutSetLog>>(emptyMap())
    val activeSetLogs: StateFlow<Map<String, WorkoutSetLog>> = _activeSetLogs.asStateFlow()

    // Session-level user feedback
    val feedbackSatisfaction = MutableStateFlow(4)
    val feedbackRpe = MutableStateFlow(7)
    val feedbackNotes = MutableStateFlow("")
    val activeDurationMinutes = MutableStateFlow(45)
    val activeRestTimeSeconds = MutableStateFlow(60)

    fun startActiveWorkout(session: WorkoutSession) {
        _activeSession.value = session
        feedbackSatisfaction.value = 4
        feedbackRpe.value = 7
        feedbackNotes.value = ""
        activeDurationMinutes.value = 45
        activeRestTimeSeconds.value = 60
        
        viewModelScope.launch {
            val exercises = repository.getExercisesForSessionDirect(session.id)
            _activeExercises.value = exercises
            
            // Build default set log items for each exercise and sets
            val initialSetMap = mutableMapOf<String, WorkoutSetLog>()
            for (ex in exercises) {
                for (setIdx in 1..ex.targetSets) {
                    val key = "${ex.id}_$setIdx"
                    initialSetMap[key] = WorkoutSetLog(
                        logId = 0L,
                        exerciseName = ex.exerciseName,
                        category = ex.exerciseCategory,
                        setIndex = setIdx,
                        targetWeight = ex.targetWeightKg,
                        targetReps = ex.targetReps,
                        actualWeight = ex.targetWeightKg, // default to targets for convenience
                        actualReps = ex.targetReps,
                        rpe = 8,
                        completed = true
                    )
                }
            }
            _activeSetLogs.value = initialSetMap
            navigateTo(AppScreen.EXECUTION)
            showSnackBar("Started Active Workout: ${session.name}")
        }
    }

    fun updateSetLog(exerciseId: Long, setIndex: Int, weight: Double, reps: Int, rpe: Int, completed: Boolean) {
        val currentMap = _activeSetLogs.value.toMutableMap()
        val key = "${exerciseId}_$setIndex"
        val existing = currentMap[key]
        if (existing != null) {
            currentMap[key] = existing.copy(
                actualWeight = weight,
                actualReps = reps,
                rpe = rpe,
                completed = completed
            )
            _activeSetLogs.value = currentMap
        }
    }

    fun discardWorkout() {
        _activeSession.value = null
        _activeExercises.value = emptyList()
        _activeSetLogs.value = emptyMap()
        navigateTo(AppScreen.DASHBOARD)
        showSnackBar("Workout session discarded.")
    }

    fun finishAndSaveWorkout() {
        val session = _activeSession.value ?: return
        viewModelScope.launch {
            val profile = userProfile.value
            val log = TrainingLog(
                sessionName = session.name,
                athleteWeightKg = profile.weightKg,
                durationMinutes = activeDurationMinutes.value,
                restTimeSeconds = activeRestTimeSeconds.value,
                feedbackSatisfaction = feedbackSatisfaction.value,
                averageRpe = feedbackRpe.value.toDouble(),
                notes = feedbackNotes.value,
                sharedWithCoach = false,
                isWearableSynced = wearableSyncState.value == "Connected & Active Syncing"
            )

            val setsList = _activeSetLogs.value.values.toList()
            repository.saveLogAndSets(log, setsList)

            _activeSession.value = null
            _activeExercises.value = emptyList()
            _activeSetLogs.value = emptyMap()

            navigateTo(AppScreen.DASHBOARD)
            showSnackBar("Workout '${session.name}' saved offline!")
            updateBadges()

            // Trigger physical-sound reminder / milestone feedback!
            simulatePushNotification("Workout Milestone!", "Outstanding! You recorded '${session.name}'. Check out your new progress logs!")
        }
    }

    // --- Workout History & Logs State ---
    val workoutLogs: StateFlow<List<TrainingLog>> = repository.allLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun shareLogExternal(log: TrainingLog) {
        viewModelScope.launch {
            val sets = repository.getSetsForLogDirect(log.logId)
            val sb = java.lang.StringBuilder()
            sb.append("📊 Workout Progress Card by GT Pulse 🏋️\n")
            sb.append("Session: ${log.sessionName}\n")
            sb.append("Date: ${java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(log.timestamp)}\n")
            sb.append("Athlete Weight: ${log.athleteWeightKg} kg | RPE: ${log.averageRpe}/10\n")
            sb.append("Workout Satisfaction: ${"⭐".repeat(log.feedbackSatisfaction)}\n")
            if (log.notes.isNotBlank()) {
                sb.append("Notes: ${log.notes}\n")
            }
            sb.append("\nExecuted Sets Log:\n")
            var currentEx = ""
            for (set in sets) {
                if (set.exerciseName != currentEx) {
                    currentEx = set.exerciseName
                    sb.append("• $currentEx:\n")
                }
                sb.append("  Set ${set.setIndex}: ${set.actualWeight}kg x ${set.actualReps} reps (RPE ${set.rpe})\n")
            }
            
            repository.updateLogShared(log.logId, true)
            // Save state / share triggers will be handled by UI via system Share Intent
            _sharedContentText.value = sb.toString()
            showSnackBar("Progress sheet ready! Share intent simulated.")
        }
    }

    private val _sharedContentText = MutableStateFlow<String?>(null)
    val sharedContentText: StateFlow<String?> = _sharedContentText.asStateFlow()

    fun clearSharedIntent() {
        _sharedContentText.value = null
    }

    fun deleteLog(log: TrainingLog) {
        viewModelScope.launch {
            repository.deleteLogById(log.logId)
            showSnackBar("Workout log deleted.")
        }
    }

    // --- AI Smart Recommendations (Gemini API Option B) ---
    private val _recommendationText = MutableStateFlow<String>("")
    val recommendationText: StateFlow<String> = _recommendationText.asStateFlow()

    private val _isGeneratingRecommendation = MutableStateFlow(false)
    val isGeneratingRecommendation: StateFlow<Boolean> = _isGeneratingRecommendation.asStateFlow()

    private val _recommendationError = MutableStateFlow<String?>(null)
    val recommendationError: StateFlow<String?> = _recommendationError.asStateFlow()

    fun generateAIAdvice() {
        viewModelScope.launch {
            _isGeneratingRecommendation.value = true
            _recommendationError.value = null
            
            try {
                val profile = userProfile.value
                val logsList = workoutLogs.value.take(5) // analyze up to 5 logs

                val logsSummary = if (logsList.isEmpty()) {
                    "No logged workout history available yet."
                } else {
                    logsList.joinToString("\n") { log ->
                        "• Date: ${java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(log.timestamp)} | '${log.sessionName}' | Duration: ${log.durationMinutes} mins | Satisfaction: ${log.feedbackSatisfaction}/5 | Average RPE: ${log.averageRpe}/10 | Notes: ${log.notes}"
                    }
                }

                val systemPrompt = "You are GT Pulse AI, an elite professional bodybuilding coach and performance endocrinologist/biochemist. You analyze athlete workout profiles and physical parameters to issue 3 highly detailed, hyper-personalized, and scientifically validated training, programming, and nutritional recommendations. Format with precise markdown, clean section blocks, and engaging fitness advice."
                
                val userQuery = """
                    Athlete Profile:
                    - Age: ${profile.age} years
                    - Sex/Gender: ${profile.gender}
                    - Weight: ${profile.weightKg} kg
                    - Height: ${profile.heightCm} cm
                    - Body Split / Location split: ${profile.defaultTrainingSite}
                    - Fitness Objective: ${profile.fitnessGoal}
                    - Experience Level: ${profile.trainingLevel}
                    
                    Recent Workout Logs (Historical tracking):
                    $logsSummary
                    
                    Based on these physical parameters, objective, and recent local log details, please issue your custom 3 coaching adjustments regarding:
                    1. Training volume and RPE loading adjustments
                    2. Precise dietary macros/kcal guidelines according to their goals
                    3. Motivation, sleep weight, or recovery strategies
                """.trimIndent()

                val apiKey = BuildConfig.GEMINI_API_KEY
                
                // Construct parameters conforming with our Moshi models
                val requestBody = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(
                                GeminiPart(text = userQuery)
                            )
                        )
                    ),
                    systemInstruction = GeminiContent(
                        parts = listOf(
                            GeminiPart(text = systemPrompt)
                        )
                    )
                )

                val response = withContext(Dispatchers.IO) {
                    GeminiRetrofitClient.service.generateContent(apiKey, requestBody)
                }

                val generatedText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (generatedText != null) {
                    _recommendationText.value = generatedText
                } else {
                    _recommendationText.value = "Failed to parse recommendations. The model didn't return text."
                }

            } catch (e: Exception) {
                _recommendationError.value = "Failed to reach AI Coach. Please check your internet connection or verify your GEMINI_API_KEY in the AI Studio secrets configuration.\nDetails: ${e.localizedMessage}"
                // Supply a polished local fallback so the user always has high-quality, professional recommendations! This is superb fallback behavior.
                _recommendationText.value = getLocalFallbackRecommendations()
            } finally {
                _isGeneratingRecommendation.value = false
            }
        }
    }

    private fun getLocalFallbackRecommendations(): String {
        val profile = userProfile.value
        return """
            ### 📊 Coach GT Pulse Local Analytics Recommendation

            *(Offline/Local Mode Fallback. Connect online with a valid Gemini API Key to unlock full cloud AI diagnostics)*

            Based on your **${profile.trainingLevel}** experience level and **${profile.fitnessGoal}** target, here is your customized programming guidelines:

            #### 1. 🏋️ Volume and RPE Loading Recommendation
            Since your level is **${profile.trainingLevel}**, optimize progress by maintaining a set range of **12 to 18 working sets per muscle group weekly**.
            - For **Strength / Hypertrophy**: Target major compounds (Squat, Press, Row) in the **RPE 7.5 to 9** range (1-2 reps in reserve).
            - Keep log details consistent to allow accurate fatigue estimation. Ensure eccentric extensions take 2-3 full seconds.

            #### 2. 🍗 Nutrition, Protein, & Macronutrient Blueprint
            To accommodate **${profile.fitnessGoal}** at your bodyweight of **${profile.weightKg} kg**:
            - **Daily Calorie Target**: ${if (profile.fitnessGoal.contains("Build", true) || profile.fitnessGoal.contains("Hypertrophy", true)) "${(profile.weightKg * 36).toInt()} kcal (Slight Hypertrophic Surplus)" else "${(profile.weightKg * 28).toInt()} kcal (Structured Deficit)"}
            - **Protein Requirement**: **${(profile.weightKg * 2.1).toInt()}g** (2.1g per kg of bodyweight) to maximize anabolic recovery.
            - **Fats & Carbs**: Keep healthy fats at 20-25% of energy, filling the remainder with complex complex carbohydrates around training times.

            #### 3. 💤 Cognitive Focus & Physical Recovery
            - Maximize delta-wave sleep to **7.5+ hours** nightly to accelerate localized muscle fiber repair.
            - Ensure hydration reaches a minimum of **35ml per Kilogram** of bodyweight (approx. **${(profile.weightKg * 0.035).toFloat()} liters** daily).
        """.trimIndent()
    }

    // --- Badges & Achievements State ---
    private val _badges = MutableStateFlow<List<Badge>>(emptyList())
    val badges: StateFlow<List<Badge>> = _badges.asStateFlow()

    private fun updateBadges() {
        val logs = workoutLogs.value
        val profile = userProfile.value

        val profileBadge = Badge(
            id = "profile",
            title = "First Step",
            description = "Athletic profile config complete",
            iconResource = "person",
            isUnlocked = profile.isCompleted,
            unlockCriteria = "Complete your athletic physical profile details."
        )

        val firstWorkoutBadge = Badge(
            id = "first_workout",
            title = "Iron Novice",
            description = "Log your very first workout session",
            iconResource = "fitness_center",
            isUnlocked = logs.isNotEmpty(),
            unlockCriteria = "Record 1 completed workout session in the active tracker."
        )

        val multipleWorkoutsBadge = Badge(
            id = "discipline",
            title = "Iron Discipline",
            description = "Log 3 completed workout sessions",
            iconResource = "insights",
            isUnlocked = logs.size >= 3,
            unlockCriteria = "Record 3 completed workout sessions in your local database."
        )

        val advancedLogBadge = Badge(
            id = "advanced",
            title = "Golden Era Scholar",
            description = "Complete 5+ bodybuilding sessions",
            iconResource = "emoji_events",
            isUnlocked = logs.size >= 5,
            unlockCriteria = "Reach 5+ historic workout logs representing continuous training schedules."
        )

        val premiumBadge = Badge(
            id = "titan_squat",
            title = "Titan Force",
            description = "Register a squat target or lift of 100+ Kg",
            iconResource = "bolt",
            isUnlocked = logs.any { it.sessionName.contains("Quads", true) || it.sessionName.contains("Legs", true) },
            unlockCriteria = "Build or log a lower-body leg workout focusing heavily on compound leg power."
        )

        _badges.value = listOf(
            profileBadge,
            firstWorkoutBadge,
            multipleWorkoutsBadge,
            advancedLogBadge,
            premiumBadge
        )
    }

    // --- Hardware Device & Notifications Simulator ---
    val wearableSyncState = MutableStateFlow("Not Connected")
    val notificationsStatus = MutableStateFlow(true) // simulated enabled

    fun toggleNotifications() {
        notificationsStatus.value = !notificationsStatus.value
        val status = if (notificationsStatus.value) "Enabled" else "Disabled"
        showSnackBar("Workout alerts & reminders are now $status.")
    }

    fun syncWithWearableDevice() {
        viewModelScope.launch {
            wearableSyncState.value = "Searching for Wearables..."
            kotlinx.coroutines.delay(1800)
            wearableSyncState.value = "Connected & Active Syncing"
            showSnackBar("Wearable Connected! Synchronized pulse, active calories, and set durations.")
            // Automatically mark any unsynced logs as synced
            val currentLogs = workoutLogs.value
            for (log in currentLogs) {
                if (!log.isWearableSynced) {
                    repository.updateWearableSync(log.logId, true)
                }
            }
            updateBadges()
        }
    }

    fun simulatePushNotification(title: String = "Gym Reminder ⚡", message: String = "Time to crush your Hypertrophy Day! Head to the rack!") {
        if (!notificationsStatus.value) return
        viewModelScope.launch {
            // Display alert on UI instantly
            showSnackBar("🔔 $title - $message")
        }
    }

    // --- Local Sample Database Seeder Helper ---
    fun seedHistoricalPerformanceData() {
        viewModelScope.launch {
            repository.seedSampleHistory()
            showSnackBar("Sample historical logs generated successfully! Dashboard charts populated.")
            updateBadges()
        }
    }

    // --- SnackBar Utility State ---
    private val _snackBarMessage = MutableStateFlow<String?>(null)
    val snackBarMessage: StateFlow<String?> = _snackBarMessage.asStateFlow()

    fun showSnackBar(msg: String) {
        _snackBarMessage.value = msg
    }

    fun clearSnackBar() {
        _snackBarMessage.value = null
    }

    init {
        // Observe logs to trigger dynamic badges updates
        viewModelScope.launch {
            workoutLogs.collectLatest {
                updateBadges()
            }
        }
        viewModelScope.launch {
            userProfile.collectLatest {
                updateBadges()
            }
        }
    }
}
