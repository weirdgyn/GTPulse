package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.FitnessDao
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfile::class,
        WorkoutExercise::class,
        WorkoutSession::class,
        SessionExercise::class,
        TrainingLog::class,
        WorkoutSetLog::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun fitnessDao(): FitnessDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ironpulse_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        prepopulateData(database.fitnessDao())
                    }
                }
            }

            private suspend fun prepopulateData(dao: FitnessDao) {
                // Initialize default profile but set as incomplete for onboarding
                dao.insertUserProfile(
                    UserProfile(
                        id = 1,
                        athleteName = "",
                        age = 0,
                        weightKg = 0.0,
                        heightCm = 0.0,
                        gender = "Male",
                        fitnessGoal = "Hypertrophy (Building Muscle)",
                        trainingLevel = "Intermediate",
                        defaultTrainingSite = "",
                        isCompleted = false
                    )
                )

                // Initialize standard bodybuilding exercises
                val defaultExercises = listOf(
                    WorkoutExercise(name = "Barbell Bench Press", category = "Chest", primaryMuscle = "Pectoralis Major", description = "Classic chest builder done on a flat bench with a barbell.", equipment = "Barbell", instructions = "1. Lie flat on the bench.\n2. Grip the barbell slightly wider than shoulder width.\n3. Unrack weight and lower to mid-chest levels.\n4. Press upward explosively while keeping shoulder blades retracted."),
                    WorkoutExercise(name = "Incline Dumbbell Press", category = "Chest", primaryMuscle = "Upper Chest", description = "Dumbbell press on an incline bench to target upper pectoral fibers.", equipment = "Dumbbells", instructions = "1. Set bench to 30-45 degree incline.\n2. Hold dumbbells at chest height.\n3. Press up until arms are extended but not locked.\n4. Bring dumbbells back down with control."),
                    WorkoutExercise(name = "Incline Barbell Bench Press", category = "Chest", primaryMuscle = "Upper Chest", description = "Barbell bench press on incline bench targeting upper chest.", equipment = "Barbell", instructions = "1. Unrack the bar above your upper chest.\n2. Lower the bar smoothly until it touches your upper chest.\n3. Push the bar back up forcefully to arms extended length."),
                    WorkoutExercise(name = "Dumbbell Flys", category = "Chest", primaryMuscle = "Pectoralis Major", description = "Isolation chest fly exercise performed with dumbbells on flat bench.", equipment = "Dumbbells", instructions = "1. Lie flat holding dumbbells overhead with palms facing.\n2. With a slight bend in elbows, lower weights sideways in a wide arc.\n3. Squeeze chest to bring weights back to starting position."),
                    WorkoutExercise(name = "Cable Crossovers", category = "Chest", primaryMuscle = "Lower Chest", description = "High-to-low cable crossover isolating lower chest muscles.", equipment = "Cables", instructions = "1. Set pulleys to the high position.\n2. Step forward with a split stance.\n3. Push handles down and together in front of waist, squeezing lower chest."),
                    WorkoutExercise(name = "Barbell Squat", category = "Legs", primaryMuscle = "Quadriceps", description = "The king of lower body movements. Back squat with a barbell.", equipment = "Barbell", instructions = "1. Rest barbell on upper traps.\n2. Keep chest high, squat down until hips are below parallel.\n3. Press back up from heels keeping core braced and spine neutral."),
                    WorkoutExercise(name = "Romanian Deadlift", category = "Legs", primaryMuscle = "Hamstrings & Glutes", description = "Hinge movement focusing on the hammies, glutes, and lower back.", equipment = "Barbell", instructions = "1. Stand with barbell at hip height.\n2. Push hips back, lowering bar along thighs with flat back.\n3. Squeeze glutes and hamstrings to stand back up."),
                    WorkoutExercise(name = "Deadlift", category = "Legs", primaryMuscle = "Posterior Chain", description = "Full body strength pull measuring total anatomical power.", equipment = "Barbell", instructions = "1. Stand with feet hip-width under bar.\n2. Keep flat back, pull bar upward close to legs until upright.\n3. Avoid rounding lower back. Control weight on descend."),
                    WorkoutExercise(name = "Leg Press", category = "Legs", primaryMuscle = "Quadriceps & Glutes", description = "Leg press on 45-degree angle sled machine.", equipment = "Machine", instructions = "1. Place feet shoulder-width on platform.\n2. Lower platform safely until knees bend 90 degrees.\n3. Push platform back up, leaving a tiny bend in knee joints."),
                    WorkoutExercise(name = "Leg Extensions", category = "Legs", primaryMuscle = "Quadriceps", description = "Machine isolation leg extensions targeting knee extensor strength.", equipment = "Machine", instructions = "1. Sit in machine with shin pad against lower ankles.\n2. Flex quads to raise legs until straight.\n3. Control weight down slowly."),
                    WorkoutExercise(name = "Lying Leg Curls", category = "Legs", primaryMuscle = "Hamstrings", description = "Isolation hamstring curl machine targeting knee flexor strength.", equipment = "Machine", instructions = "1. Lie face down on curl machine.\n2. Curl pad upward toward glutes.\n3. Return down with solid concentric and eccentric control."),
                    WorkoutExercise(name = "Calf Raises", category = "Legs", primaryMuscle = "Gastrocnemius", description = "Heel raises on platform for calf isolation.", equipment = "Dumbbells", instructions = "1. Stand with balls of feet on edge of platform.\n2. Let heels drop low for stretch, then raise up as high as possible.\n3. Squeeze calves on top."),
                    WorkoutExercise(name = "Barbell Row", category = "Back", primaryMuscle = "Latissimus Dorsi", description = "Bent over barbell rowing for mid and upper back thickness.", equipment = "Barbell", instructions = "1. Hinge body 45 degrees holding barbell.\n2. Pull barbell toward lower chest/belly button keeping elbows close.\n3. Squeeze back musculature on top, release under control."),
                    WorkoutExercise(name = "Pull-ups", category = "Back", primaryMuscle = "Lats", description = "Bodyweight powerhouse that builds a wide, V-taper frame.", equipment = "Bodyweight", instructions = "1. Hang from pull-up bar with wide grip.\n2. Pull collarbone to the bar by retracting shoulder blades.\n3. Lower slow to dead hang."),
                    WorkoutExercise(name = "Lat Pulldown", category = "Back", primaryMuscle = "Latissimus Dorsi", description = "Seated machine pulldown targeting upper outer lats.", equipment = "Cables", instructions = "1. Sit in machine, grip bar wide.\n2. Pull bar down toward upper chest, drawing elbows down.\n3. Squeeze lats, raise bar slow."),
                    WorkoutExercise(name = "Seated Cable Row", category = "Back", primaryMuscle = "Lats & Rhomboids", description = "Cable machine row targeting mid-back thickness.", equipment = "Cables", instructions = "1. Sit with knees slightly bent. Handle in hand.\n2. Pull handle to stomach keeping chest upright.\n3. Let cable stretch back."),
                    WorkoutExercise(name = "Overhead Barbell Press", category = "Shoulders", primaryMuscle = "Deltoids", description = "Standing military press targeting the shoulders and core stability.", equipment = "Barbell", instructions = "1. Pull bar to upper collarbone stance.\n2. Press barbell overhead until arms are locked out.\n3. Lower bar smoothly under tension."),
                    WorkoutExercise(name = "Dumbbell Lateral Raise", category = "Shoulders", primaryMuscle = "Lateral Deltoids", description = "Isolation move to build width and side deltoid volume.", equipment = "Dumbbells", instructions = "1. Hold dumbbells at sides; lean super slightly forward.\n2. Raise arms out to sides in a wide arc until hands align with shoulders.\n3. Lower slowly."),
                    WorkoutExercise(name = "Dumbbell Shoulder Press", category = "Shoulders", primaryMuscle = "Anterior Deltoid", description = "Seated shoulder press using dumbbells for shoulder mass.", equipment = "Dumbbells", instructions = "1. Sit upright, press dumbbells from shoulder level overhead.\n2. Bring them together closely but do not bang weights.\n3. Lower slowly."),
                    WorkoutExercise(name = "Face Pulls", category = "Shoulders", primaryMuscle = "Posterior Deltoid", description = "Cable rope pull to face to optimize rear delts and rotators.", equipment = "Cables", instructions = "1. Set cable pulleys to upper chest height.\n2. Pull rope to nose while pulling hands apart.\n3. Squeeze rear delts."),
                    WorkoutExercise(name = "Barbell Bicep Curl", category = "Arms", primaryMuscle = "Biceps Brachii", description = "Underhand standing bicep curl for sleeve-splitting peak building.", equipment = "Barbell", instructions = "1. Stand tall holding barbell underhand.\n2. Keep elbows pinned to sides, curl barbell up toward shoulders.\n3. Flex biceps hard at peak, lower slowly."),
                    WorkoutExercise(name = "Dumbbell Hammer Curl", category = "Arms", primaryMuscle = "Brachialis", description = "Neutral hammer grip bicep curl build for thicker biceps and forearms.", equipment = "Dumbbells", instructions = "1. Hold dumbbells with neutral palms facing inward.\n2. Curl dumbbells up together, keeping elbows locked at sides.\n3. Lower under perfect control."),
                    WorkoutExercise(name = "Preacher Curl", category = "Arms", primaryMuscle = "Biceps Short Head", description = "Preacher bench isolation curls pulling peak height to the biceps.", equipment = "EZ Bar", instructions = "1. Position arms on preacher pad.\n2. Underhand grab the EZ Bar.\n3. Curl weight up while upper arms stay flat on pad. Extend down fully."),
                    WorkoutExercise(name = "Tricep Overhead Extension", category = "Arms", primaryMuscle = "Triceps (Long head)", description = "Overhead dumbbell movement targeting the triceps long head.", equipment = "Dumbbells", instructions = "1. Core braced, cup dumbbell overhead with both hands.\n2. Bend elbows to lower weight behind head.\n3. Press up to lock out."),
                    WorkoutExercise(name = "Tricep Rope Pushdowns", category = "Arms", primaryMuscle = "Triceps", description = "Cable machine isolation builder for arm definition.", equipment = "Cables", instructions = "1. Grab rope handle attachment on high pulley.\n2. Pin elbows to ribs, press rope downward extending elbows fully.\n3. Flare rope ends outwards."),
                    WorkoutExercise(name = "Hanging Knee Raises", category = "Core", primaryMuscle = "Rectus Abdominis", description = "Core builder to define lower abs and improve hip flexor stability.", equipment = "Bodyweight", instructions = "1. Hang from bar with straight arms.\n2. Tuck knees toward chest pulling from lower pelvis.\n3. Lower slowly without swinging."),
                    WorkoutExercise(name = "Plank", category = "Core", primaryMuscle = "Transverse Abdominis", description = "Isotonic abdominal plank targeting full abdominal core stability.", equipment = "Bodyweight", instructions = "1. Plant forearms and toes on floor.\n2. Keep back flat, abs vacuumed tight, forming straight line.\n3. Hold for time."),
                    WorkoutExercise(name = "Cable Crunch", category = "Core", primaryMuscle = "Rectus Abdominis", description = "Kneeling cable crunch to add weighted resistance load to abs.", equipment = "Cables", instructions = "1. Kneel facing low cable machine holding ropes by ears.\n2. Flex abs, pulling elbows to knees and curling back.\n3. Unfurl slowly.")
                )

                for (ex in defaultExercises) {
                    dao.insertExercise(ex)
                }
            }
        }
    }
}
