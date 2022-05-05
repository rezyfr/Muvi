package com.andriiginting.common_widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import org.w3c.dom.Text

@Composable
fun ErrorScreen(
    message: String,
    asset: String
) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.Asset(asset))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
    ) {
        LottieAnimation(
            composition = composition,
            progress = progress,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = message,
            fontWeight = FontWeight.Bold,
            fontSize = 21.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}