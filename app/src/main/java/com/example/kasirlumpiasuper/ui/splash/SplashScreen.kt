package com.example.kasirlumpiasuper.ui.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.data.repository.FirestoreViewModel


@Composable
fun SplashScreen(
    viewModel: FirestoreViewModel = viewModel(),
    navToNext: () -> Unit
) {
    val quote by viewModel.quote.collectAsState()
    val name by viewModel.user.collectAsState()

    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnimation = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 3000),
        finishedListener = { navToNext() }
    )


    LaunchedEffect(Unit) {
        viewModel.loadQuote()
        viewModel.loadUser()
        startAnimation = true
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (name?.name != null && quote != null) {
            Text(
                text = "Welcome ${name?.name}!",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier
                    .alpha(alphaAnimation.value)
            )
            Text(
                text = "Quote: \"$quote\"",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .alpha(alphaAnimation.value)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(R.drawable.lumper_logo),
                contentDescription = "Logo Lumper",
                modifier = Modifier
                    .size(320.dp)
                    .alpha(alphaAnimation.value)
            )
        } else {
            CircularProgressIndicator()
        }
    }
}


