package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FitnessDao {

    // --- User Profile ---
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileDirect(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)

    // --- Workout Exercises ---
    @Query("SELECT * FROM workout_exercises ORDER BY name ASC")
    fun getAllExercisesFlow(): Flow<List<WorkoutExercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: WorkoutExercise): Long

    @Query("DELETE FROM workout_exercises WHERE id = :id")
    suspend fun deleteExerciseById(id: Long)

    // --- Workout Sessions (Routines) ---
    @Query("SELECT * FROM workout_sessions ORDER BY id DESC")
    fun getAllSessionsFlow(): Flow<List<WorkoutSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSession): Long

    @Query("DELETE FROM workout_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Long)

    // --- Session Exercises (Target mapping) ---
    @Query("SELECT * FROM session_exercises WHERE sessionId = :sessionId ORDER BY orderIndex ASC")
    fun getExercisesForSessionFlow(sessionId: Long): Flow<List<SessionExercise>>

    @Query("SELECT * FROM session_exercises WHERE sessionId = :sessionId ORDER BY orderIndex ASC")
    suspend fun getExercisesForSessionDirect(sessionId: Long): List<SessionExercise>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessionExercise(exercise: SessionExercise)

    @Query("DELETE FROM session_exercises WHERE sessionId = :sessionId")
    suspend fun deleteExercisesForSession(sessionId: Long)

    // --- Training Logs ---
    @Query("SELECT * FROM training_logs ORDER BY timestamp DESC")
    fun getAllLogsFlow(): Flow<List<TrainingLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrainingLog(log: TrainingLog): Long

    @Query("UPDATE training_logs SET sharedWithCoach = :shared WHERE logId = :logId")
    suspend fun updateLogShared(logId: Long, shared: Boolean)

    @Query("UPDATE training_logs SET isWearableSynced = :synced WHERE logId = :logId")
    suspend fun updateWearableSync(logId: Long, synced: Boolean)

    @Query("UPDATE training_logs SET isCloudSynced = :synced WHERE logId = :logId")
    suspend fun updateCloudSync(logId: Long, synced: Boolean)

    @Query("DELETE FROM training_logs WHERE logId = :logId")
    suspend fun deleteLogById(logId: Long)

    // --- Workout Set Logs ---
    @Query("SELECT * FROM workout_set_logs WHERE logId = :logId ORDER BY exerciseName ASC, setIndex ASC")
    fun getSetLogsForTrainingLogFlow(logId: Long): Flow<List<WorkoutSetLog>>

    @Query("SELECT * FROM workout_set_logs WHERE logId = :logId ORDER BY exerciseName ASC, setIndex ASC")
    suspend fun getSetLogsForTrainingLogDirect(logId: Long): List<WorkoutSetLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutSetLog(setLog: WorkoutSetLog)

    @Query("DELETE FROM workout_set_logs WHERE logId = :logId")
    suspend fun deleteSetLogsForLog(logId: Long)
}
