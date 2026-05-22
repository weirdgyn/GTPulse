package com.example.data.repository

import com.example.data.dao.FitnessDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

class FitnessRepository(private val fitnessDao: FitnessDao) {

    val userProfile: Flow<UserProfile?> = fitnessDao.getUserProfileFlow()

    suspend fun saveUserProfile(profile: UserProfile) {
        fitnessDao.insertUserProfile(profile)
    }

    val allExercises: Flow<List<WorkoutExercise>> = fitnessDao.getAllExercisesFlow()

    suspend fun insertExercise(exercise: WorkoutExercise): Long {
        return fitnessDao.insertExercise(exercise)
    }

    suspend fun deleteExerciseById(id: Long) {
        fitnessDao.deleteExerciseById(id)
    }

    val allSessions: Flow<List<WorkoutSession>> = fitnessDao.getAllSessionsFlow()

    suspend fun insertSession(session: WorkoutSession): Long {
        return fitnessDao.insertSession(session)
    }

    suspend fun deleteSessionById(id: Long) {
        fitnessDao.deleteSessionById(id)
        fitnessDao.deleteExercisesForSession(id)
    }

    fun getExercisesForSession(sessionId: Long): Flow<List<SessionExercise>> {
        return fitnessDao.getExercisesForSessionFlow(sessionId)
    }

    suspend fun getExercisesForSessionDirect(sessionId: Long): List<SessionExercise> {
        return fitnessDao.getExercisesForSessionDirect(sessionId)
    }

    suspend fun saveSessionExercises(sessionId: Long, exercises: List<SessionExercise>) {
        fitnessDao.deleteExercisesForSession(sessionId)
        for (ex in exercises) {
            fitnessDao.insertSessionExercise(ex.copy(sessionId = sessionId))
        }
    }

    val allLogs: Flow<List<TrainingLog>> = fitnessDao.getAllLogsFlow()

    suspend fun insertTrainingLog(log: TrainingLog): Long {
        return fitnessDao.insertTrainingLog(log)
    }

    suspend fun updateLogShared(logId: Long, shared: Boolean) {
        fitnessDao.updateLogShared(logId, shared)
    }

    suspend fun updateWearableSync(logId: Long, synced: Boolean) {
        fitnessDao.updateWearableSync(logId, synced)
    }

    suspend fun updateCloudSync(logId: Long, synced: Boolean) {
        fitnessDao.updateCloudSync(logId, synced)
    }

    suspend fun deleteLogById(logId: Long) {
        fitnessDao.deleteLogById(logId)
        fitnessDao.deleteSetLogsForLog(logId)
    }

    fun getSetsForLog(logId: Long): Flow<List<WorkoutSetLog>> {
        return fitnessDao.getSetLogsForTrainingLogFlow(logId)
    }

    suspend fun getSetsForLogDirect(logId: Long): List<WorkoutSetLog> {
        return fitnessDao.getSetLogsForTrainingLogDirect(logId)
    }

    suspend fun saveLogAndSets(log: TrainingLog, sets: List<WorkoutSetLog>): Long {
        val logId = fitnessDao.insertTrainingLog(log)
        // Clean out and write sets with the correct inserted ID
        fitnessDao.deleteSetLogsForLog(logId)
        for (set in sets) {
            fitnessDao.insertWorkoutSetLog(set.copy(logId = logId))
        }
        return logId
    }

    // Helper to generate elegant mock logs over the last 15 days to display impressive metrics and graph progress
    suspend fun seedSampleHistory() {
        val currentTime = System.currentTimeMillis()
        val oneDayMs = TimeUnit.DAYS.toMillis(1)

        val workoutHistory = listOf(
            Triple("Hypertrophy Push", currentTime - 12 * oneDayMs, 80.0),
            Triple("Heavy Pull Day", currentTime - 10 * oneDayMs, 81.0),
            Triple("Quads & Hamstrings", currentTime - 8 * oneDayMs, 81.5),
            Triple("Hypertrophy Push", currentTime - 6 * oneDayMs, 82.0),
            Triple("Heavy Pull Day", currentTime - 4 * oneDayMs, 82.5),
            Triple("Quads & Hamstrings", currentTime - 2 * oneDayMs, 83.0),
            Triple("Hypertrophy Push", currentTime - 12 * oneDayMs / 12, 83.5) // recent
        )

        val sampleExercises = mapOf(
            "Hypertrophy Push" to listOf(
                Pair("Barbell Bench Press", 80.0),
                Pair("Overhead Barbell Press", 50.0),
                Pair("Incline Dumbbell Press", 32.0)
            ),
            "Heavy Pull Day" to listOf(
                Pair("Barbell Row", 70.0),
                Pair("Pull-ups", 0.0),
                Pair("Barbell Bicep Curl", 35.0)
            ),
            "Quads & Hamstrings" to listOf(
                Pair("Barbell Squat", 100.0),
                Pair("Romanian Deadlift", 80.0)
            )
        )

        var daysAgo = 12
        for (workout in workoutHistory) {
            val sName = workout.first
            val logTime = workout.second
            val userWeight = workout.third

            val duration = 40 + (Math.random() * 25).toInt()
            val satisfaction = 3 + (Math.random() * 3).toInt()
            val rpe = 6.5 + (Math.random() * 3)

            val log = TrainingLog(
                timestamp = logTime,
                sessionName = sName,
                athleteWeightKg = userWeight,
                durationMinutes = duration,
                feedbackSatisfaction = satisfaction,
                averageRpe = Math.round(rpe * 10) / 10.0,
                notes = "Completed with solid focus and short rest intervals.",
                sharedWithCoach = Math.random() > 0.5,
                isWearableSynced = true,
                isCloudSynced = Math.random() > 0.4
            )

            val savedId = fitnessDao.insertTrainingLog(log)
            val exercises = sampleExercises[sName] ?: emptyList()
            
            for (ex in exercises) {
                // Add 3 sets of sample data
                for (setIdx in 1..3) {
                    val actualWt = ex.second + (setIdx * 2) + ((Math.random() * 5).toInt() - 2) // progressive or random load
                    fitnessDao.insertWorkoutSetLog(
                        WorkoutSetLog(
                            logId = savedId,
                            exerciseName = ex.first,
                            category = "Strength",
                            setIndex = setIdx,
                            targetWeight = ex.second,
                            targetReps = 8,
                            actualWeight = if (ex.second > 0) actualWt else 0.0,
                            actualReps = 8 + (Math.random() * 3).toInt() - 1,
                            rpe = 7 + (Math.random() * 3).toInt(),
                            completed = true
                        )
                    )
                }
            }
            daysAgo -= 2
        }
    }
}
