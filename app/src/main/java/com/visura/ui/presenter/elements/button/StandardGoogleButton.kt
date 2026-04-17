package com.visura.ui.presenter.elements.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.visura.R
import com.visura.ui.presenter.theme.ComponentSize
import com.visura.ui.presenter.theme.IconSize

@Composable
fun StandardGoogleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ComponentSize.Medium)
    ) {
        StandardOutlinedButton(
            text = text,
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.fillMaxSize()
        )
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painter = painterResource(id = R.drawable.google_icon),
                contentDescription = null,
                modifier = Modifier.size(IconSize.XXLarge),
                tint = Color.Unspecified
            )
        }
    }
}
