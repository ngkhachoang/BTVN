package com.example.btvn_nkh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.btvn_nkh.ui.MainScreen
import com.example.btvn_nkh.ui.TempResultScreen
import com.example.btvn_nkh.ui.theme.BTVN_NKHTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BTVN_NKHTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "main"
                    ) {
                        composable("main") {
                            val viewModel = hiltViewModel<com.example.btvn_nkh.ui.MainScreenViewModel>()
                            MainScreen(
                                modifier = Modifier.padding(innerPadding),
                                viewModel = viewModel,
                                navController = navController
                            )
                        }
                        composable(
                            "result?url={url}",
                            arguments = listOf(navArgument("url") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val url = backStackEntry.arguments?.getString("url") ?: ""
                            TempResultScreen(imageUrl = url, navController = navController)
                        }
                    }
                }
            }
        }
    }
}