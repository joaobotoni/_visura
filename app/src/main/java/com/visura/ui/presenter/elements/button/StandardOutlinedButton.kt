package com.visura.ui.presenter.elements.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.visura.ui.presenter.theme.CornerRadius

@Composable
fun StandardOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    @DrawableRes icon: Int? = null
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(CornerRadius.Medium),
        modifier = modifier,
        enabled = enabled
    ) {
        if (icon != null) {
            Icon(painter = painterResource(id = icon), contentDescription = null)
        }
        Text(text = text)
    }
}
