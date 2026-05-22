package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val athleteName: String = "Alex Rivera",
    val age: Int = 25,
    val weightKg: Double = 75.0,
    val heightCm: Double = 175.0,
    val gender: String = "Male",
    val fitnessGoal: String = "Hypertrophy (Building Muscle)", // e.g. Hypertrophy, Strength, Cutting, Endurance
    val trainingLevel: String = "Intermediate", // Beginner, Intermediate, Advanced
    val defaultTrainingSite: String = "Gold's Gym Main", // Standard training site name
    val isCompleted: Boolean = false
)

@Entity(tableName = "workout_exercises")
data class WorkoutExercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String, // e.g. Chest, Back, Legs, Shoulders, Arms, Core
    val primaryMuscle: String,
    val description: String,
    val isCustom: Boolean = false,
    val equipment: String = "Barbell",
    val instructions: String = "",
    val videoUrl: String = "",
    val pictureUrl: String = ""
)

@Entity(tableName = "workout_sessions")
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, // e.g. "Push Day A", "Heavy Pull", "Squat Focus"
    val targetSplit: String, // e.g. "Push", "Pull", "Legs", "Fullbody"
    val notes: String = "",
    val sequenceNumber: Int = 1,
    val dayOfWeek: String = "Monday"
)

@Entity(tableName = "session_exercises")
data class SessionExercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val exerciseId: Long,
    val exerciseName: String,
    val exerciseCategory: String,
    val orderIndex: Int,
    val targetSets: Int = 3,
    val targetReps: Int = 10,
    val targetWeightKg: Double = 20.0,
    val targetDurationMinutes: Int = 5,
    val targetRestTimeSeconds: Int = 60
)

@Entity(tableName = "training_logs")
data class TrainingLog(
    @PrimaryKey(autoGenerate = true) val logId: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val sessionName: String,
    val athleteWeightKg: Double = 75.0,
    val durationMinutes: Int = 45,
    val restTimeSeconds: Int = 60,
    val feedbackSatisfaction: Int = 4, // Rating 1 to 5
    val averageRpe: Double = 7.0, // Rating 1-10 rate of perceived exertion
    val notes: String = "",
    val sharedWithCoach: Boolean = false,
    val isWearableSynced: Boolean = false,
    val isCloudSynced: Boolean = false
)

@Entity(tableName = "workout_set_logs")
data class WorkoutSetLog(
    @PrimaryKey(autoGenerate = true) val setLogId: Long = 0,
    val logId: Long,
    val exerciseName: String,
    val category: String,
    val setIndex: Int, // 1, 2, 3...
    val targetWeight: Double = 0.0,
    val targetReps: Int = 10,
    val actualWeight: Double = 0.0,
    val actualReps: Int = 0,
    val rpe: Int = 8, // RPE feedback
    val completed: Boolean = true
)

// Simple UI model to hold a badge achievement
data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val iconResource: String, // Icon name
    val isUnlocked: Boolean = false,
    val unlockCriteria: String
)
