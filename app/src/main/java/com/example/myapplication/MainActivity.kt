package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // 1. Initialize the NavController
                    val navController = rememberNavController()

                    // 2. Set up the NavHost inside the scaffold padding to respect edge-to-edge
                    NavHost(
                        navController = navController,
                        startDestination = Home,
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        // Home Screen Destination
                        composable<Home> {
                            HomeScreen(onShowGreeting = { typedName ->
                                // Pass the name by creating a Greeting route object
                                navController.navigate(Greeting(userName = typedName))
                            })
                        }

                        // Greeting Screen Destination
                        composable<Greeting> { backStackEntry ->
                            // Rebuild the typed Greeting object on this screen
                            val greeting: Greeting = backStackEntry.toRoute()
                            GreetingScreen(userName = greeting.userName)
                        }
                    }
                }
            }
        }
    }
}