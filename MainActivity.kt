package com.example.composetutorial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.composetutorial.ui.theme.ComposeTutorialTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTutorialTheme {
                val context = LocalContext.current
                val userDao = AppDatabase.getDatabase(context).userDao()
                val userRepository = HandleUser(userDao, context)
                userRepository.setDefaultUser(User(0, "these shouldn't be here", null))
                NavHost(userRepository)
            }
        }
    }
}

@Composable
fun NavHost(handleUser: HandleUser) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable(route = "main") {
            Main(navController, handleUser)
        }
        composable(route = "settings") {
            Settings(navController, handleUser)
        }
    }
}

@Composable
fun Main(navController: NavController, handleUser: HandleUser) {
    Column {
        IconButton(
            onClick = { navController.navigate("settings") },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(
                Icons.Rounded.Settings, contentDescription = "",
            )
        }
        Conversation(SampleData.conversationSample, handleUser)
    }
}


@Composable
fun Settings(navController: NavController, handleUser: HandleUser) {

    var lastImage by remember {
        mutableStateOf(handleUser.findUserById(0).image ?: "null")
    }
    var lastName by remember {
        mutableStateOf(handleUser.findUserById(0).userName ?: "this shouldn't be here")
    }
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            handleUser.saveUser(lastName, handleUser.changeImage(uri).toString())
        }
        lastImage = handleUser.findUserById(0).image.toString()
    }

    Column {
        IconButton(
            onClick = {
                navController.navigate("Main") {
                    popUpTo("Main") {
                        inclusive = true
                        handleUser.saveUser(lastName, handleUser.findUserById(0).image)
                    }
                }
            },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(
                Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "",
            )
        }
        AsyncImage(
            model = handleUser.findUserById(0).image,
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .clickable {
                    photoPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
        )

        TextField(value = lastName, onValueChange = {
            lastName = it
        })
    }
}



