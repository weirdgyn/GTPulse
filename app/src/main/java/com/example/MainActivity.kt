package com.example

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.compose.ui.res.stringResource
import com.example.data.database.AppDatabase
import com.example.data.repository.FitnessRepository
import com.example.ui.screens.*
import com.example.ui.theme.BorderGrey
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonLime
import com.example.ui.theme.SurfaceCharcoal
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Support edge-to-edge system transparent status and home-gesture overlays natively
        enableEdgeToEdge()

        // Setup unified storage & repository
        val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = FitnessRepository(database.fitnessDao())

                // Build Factory for ViewModel
        val factory = MainViewModelFactory(application, repository)
        val viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
            val useDarkTheme = when (themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> systemDark
            }
            MyApplicationTheme(darkTheme = useDarkTheme) {
                MainAppScaffold(viewModel = viewModel)
            }
        }
    }
}

class MainViewModelFactory(
    private val application: Application,
    private val repository: FitnessRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun MainAppScaffold(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val activeSession by viewModel.activeSession.collectAsStateWithLifecycle()
    val snackBarMsg by viewModel.snackBarMessage.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    var showSplash by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()

    // Interactive custom top alert dismiss routine
    LaunchedEffect(snackBarMsg) {
        if (snackBarMsg != null) {
            delay(4000)
            viewModel.clearSnackBar()
        }
    }

    if (showSplash) {
        SplashScreen(onSplashFinished = { showSplash = false })
    } else if (!userProfile.isCompleted) {
        OnboardingScreen(viewModel = viewModel)
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = DarkCharcoal,
            bottomBar = {
                // Hide bottom bar during active workout execution so athlete is focused entirely on reps logging
                if (activeSession == null) {
                    BottomNavigationBar(
                        selectedScreen = currentScreen,
                        onTabSelected = { screen -> viewModel.navigateTo(screen) }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Screen router transitions (crossfade for high prestige visual fluidity)
                AnimatedContent(
                    targetState = currentScreen,
                    label = "ScreenTransition",
                    transitionSpec = {
                        fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                    }
                ) { screen ->
                    when (screen) {
                        AppScreen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                        AppScreen.WORKOUT_PLAN -> WorkoutPlanScreen(viewModel = viewModel)
                        AppScreen.EXECUTION -> ActiveExecutionScreen(viewModel = viewModel)
                        AppScreen.EXERCISE_LIBRARY -> ExerciseLibraryScreen(viewModel = viewModel)
                        AppScreen.AI_ADVICE -> AiCoachScreen(viewModel = viewModel)
                    }
                }

                // High Fidelity Neon Toast alert bar overlay at top
                AnimatedVisibility(
                    visible = snackBarMsg != null,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                        .windowInsetsPadding(WindowInsets.statusBars)
                ) {
                snackBarMsg?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceCharcoal)
                            .border(1.2.dp, NeonLime, RoundedCornerShape(12.dp))
                            .clickable { viewModel.clearSnackBar() }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (msg.contains("🔔")) Icons.Default.NotificationsActive else Icons.Default.CheckCircle,
                                contentDescription = "Toast Icon",
                                tint = NeonLime,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = msg.replace("🔔 ", ""),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = Color.Gray,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
fun BottomNavigationBar(
    selectedScreen: AppScreen,
    onTabSelected: (AppScreen) -> Unit
) {
    NavigationBar(
        containerColor = SurfaceCharcoal,
        tonalElevation = 8.dp,
        modifier = Modifier
            .border(BorderStroke(0.5.dp, BorderGrey))
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        NavigationBarItem(
            selected = selectedScreen == AppScreen.DASHBOARD,
            onClick = { onTabSelected(AppScreen.DASHBOARD) },
            icon = { Icon(imageVector = Icons.Default.Dashboard, contentDescription = "Dashboard") },
            label = { Text(stringResource(R.string.tab_dashboard), fontWeight = FontWeight.Bold, fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = NeonLime,
                indicatorColor = NeonLime,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            ),
            modifier = Modifier.testTag("tab_dashboard")
        )

        NavigationBarItem(
            selected = selectedScreen == AppScreen.WORKOUT_PLAN,
            onClick = { onTabSelected(AppScreen.WORKOUT_PLAN) },
            icon = { Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Programmer") },
            label = { Text(stringResource(R.string.tab_plan_splits), fontWeight = FontWeight.Bold, fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = NeonLime,
                indicatorColor = NeonLime,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            ),
            modifier = Modifier.testTag("tab_splits")
        )

        NavigationBarItem(
            selected = selectedScreen == AppScreen.EXERCISE_LIBRARY,
            onClick = { onTabSelected(AppScreen.EXERCISE_LIBRARY) },
            icon = { Icon(imageVector = Icons.Default.MenuBook, contentDescription = "Exercise Library") },
            label = { Text(stringResource(R.string.tab_library), fontWeight = FontWeight.Bold, fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = NeonLime,
                indicatorColor = NeonLime,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            ),
            modifier = Modifier.testTag("tab_library")
        )

        NavigationBarItem(
            selected = selectedScreen == AppScreen.EXECUTION,
            onClick = { onTabSelected(AppScreen.EXECUTION) },
            icon = { Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = "Active Track") },
            label = { Text(stringResource(R.string.tab_train_log), fontWeight = FontWeight.Bold, fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = NeonLime,
                indicatorColor = NeonLime,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            ),
            modifier = Modifier.testTag("tab_execution")
        )

        NavigationBarItem(
            selected = selectedScreen == AppScreen.AI_ADVICE,
            onClick = { onTabSelected(AppScreen.AI_ADVICE) },
            icon = { Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI Expert") },
            label = { Text(stringResource(R.string.tab_ai_coach), fontWeight = FontWeight.Bold, fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = NeonLime,
                indicatorColor = NeonLime,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            ),
            modifier = Modifier.testTag("tab_ai")
        )
    }
}
