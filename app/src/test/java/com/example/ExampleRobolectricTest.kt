package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.AppDatabase
import com.example.data.repository.FitnessRepository
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("GT Pulse", appName)
  }

  @Test
  fun `database initialization and seeding validation`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val scope = CoroutineScope(Dispatchers.Unconfined)
    val db = AppDatabase.getDatabase(context, scope)
    val repository = FitnessRepository(db.fitnessDao())
    
    // First query opens the DB and triggers seeding
    val exercises = repository.allExercises.first()
    assertNotNull(exercises)
    
    // Verify user profile too
    val profile = repository.userProfile.first()
    assertNotNull(profile)
  }
}
